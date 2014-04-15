import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.google.gson.Gson;

public class Driver {
	public static void main(String[] args) {

		boolean oneGame = true;

		int single = 611858683;

		int start = 490600006;
		int end = 610693718;

		Gson gson = new Gson();
		Match match;
		for (int i = (oneGame ? single : start); i < (oneGame ? single + 1 : end); i++) {
			try {
				match = scrapeMatch(i);
				System.out.println(gson.toJson(match));
			} catch (MatchNotFoundException e) {
				System.err.println("Match " + i + " not found!");
			} catch (IOException e) {
				System.err.println("Failed to scrape page!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Testing postJSON()
		// try {
		// JSONObject j = new JSONObject();
		// j.put("darp", 3);
		// j.put("durp", 4);
		// postJSON(j);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private static Match scrapeMatch(int id) throws MatchNotFoundException, IOException {
		WebClient webClient = new WebClient();
		turnOffWarnings(webClient);
		HtmlPage page = webClient.getPage("http://dotabuff.com/matches/" + id);

		try {
			page.getHtmlElementById("status");
			throw new MatchNotFoundException();
		} catch (ElementNotFoundException e) {
		}

		int mID = id;
		String lobbyType = "null";
		String gameMode = "null";
		String region = "null";
		String duration = "00:00:00";
		boolean radiantVictory = false;
		Timestamp timestamp = Timestamp.valueOf("0000-01-01 00:00:00");
		PlayerInstance[] players = new PlayerInstance[10];

		// Match metadata
		HtmlDivision div = page.getHtmlElementById("content-header-secondary");
		List<HtmlElement> he = div.getHtmlElementsByTagName("dl");
		int index = 0;
		// ... we can just ignore skill brackets for now
		if (!he.get(index).getHtmlElementsByTagName("dt").get(0).asText().equals("Lobby Type")) {
			index++;
		}
		// Get fields
		lobbyType = he.get(index++).getHtmlElementsByTagName("dd").get(0).asText();
		gameMode = he.get(index++).getHtmlElementsByTagName("dd").get(0).asText();
		region = he.get(index++).getHtmlElementsByTagName("dd").get(0).asText();
		// format the duration
		String temp = he.get(index++).getHtmlElementsByTagName("dd").get(0).asText();
		while (temp.length() < 6)
			temp = "00:" + temp;
		duration = temp;
		// format the timestamp
		temp = he.get(index++).getHtmlElementsByTagName("dd").get(0).getHtmlElementsByTagName("time").get(0).getAttribute("datetime");
		temp = temp.substring(0, temp.indexOf("+")).replace("T", " ") + ".0";
		timestamp = Timestamp.valueOf(temp);
		// find out who won by checking for an existing randiant win tag
		if (page.getByXPath("//div[@class='match-result team radiant']").size() != 0)
			radiantVictory = true;
		// System.out.printf("Found match:\n\tID:\t\t%d\n\tLobby Type:\t%s\n\tGame Mode:\t%s\n\tRegion:\t\t%s\n\tDuration:\t%s\n\tVictor:\t\t%s\n\tPlayed:\t\t%s\n", mID, lobbyType, gameMode, region, duration.toString(), (radiantVictory ? "Radiant" : "Dire"), timestamp.toString());

		// Team info
		int playerIndex = 0;
		List<HtmlTable> teamTables = (List<HtmlTable>) page.getByXPath("//table");
		boolean radiant = true;
		for (HtmlTable team : teamTables) {
			HtmlTableHeader header = team.getHeader();
			List<HtmlTableRow> headerRows = header.getRows();
			for (HtmlTableRow row : headerRows) {
				// System.out.println("Column Headers:");
				for (HtmlTableCell cell : row.getCells()) {
					// System.out.print(cell.asText() + " ");
				}
				// System.out.println();
			}
			for (HtmlTableBody body : team.getBodies()) {
				List<HtmlTableRow> rows = body.getRows();

				for (HtmlTableRow row : rows) {
					players[playerIndex++] = parsePlayer(row, radiant);
				}
			}
			radiant = false;
		}
		webClient.closeAllWindows();
		Match match = new Match(mID, lobbyType, gameMode, region, duration, radiantVictory, timestamp, players);
		return match;
	}

	public static PlayerInstance parsePlayer(HtmlTableRow row, boolean radiant) {
		String playerName = "";
		int pID = 0;
		String heroName = "";
		int[] stats = new int[12];
		int stat = 0;

		String[] itemBuild;
		HashMap<Integer, Integer> skillBuild = null;

		// extract stats
		for (final HtmlTableCell cell : row.getCells()) {
			if (stat == 1) {
				// System.out.println("Player found: " + cell.asText());
				playerName = cell.asText();
			} else if (stat >= 3 && stat < 15) {
				// System.out.println("   Found stat: " + cell.asText());
				stats[stat - 3] = statToInt(cell.asText());
			}
			stat++;
		}

		// get player ID or anonymous
		String r = row.asXml();
		int i = r.indexOf("/players/");
		if (i < 0) {
			pID = 0;
		} else {
			r = r.substring(i + 9);
			r = r.substring(0, r.indexOf("\">"));
			pID = Integer.valueOf(r);
		}
		// System.out.println("Player ID: " + pID);

		// get unique hero id
		heroName = row.asXml();
		heroName = heroName.substring(heroName.indexOf("/heroes/") + 8);
		heroName = heroName.substring(0, heroName.indexOf("\">"));
		// System.out.println("Hero: " + heroName);

		// get items
		ArrayList<String> arrayList = new ArrayList<String>();
		HtmlTableCell items = row.getCells().get(row.getCells().size() - 1);
		for (DomElement item : items.getChildElements()) {
			String itemID = item.asXml();
			itemID = itemID.substring(itemID.indexOf("/items/") + 7);
			itemID = itemID.substring(0, itemID.indexOf("\">"));
			// System.out.printf("Item ID: %s\n", itemID);
			arrayList.add(itemID);
		}

		itemBuild = arrayList.toArray(new String[0]);
		return new PlayerInstance(playerName, pID, heroName, stats, radiant, itemBuild, skillBuild);

	}

	public static int statToInt(String stat) {
		int value = 0;
		boolean k = stat.contains("k");
		boolean decimal = stat.contains(".");
		String temp = stat.replace("k", "");
		temp = temp.replace(".", "");
		value = Integer.valueOf(temp);
		if (k)
			value *= 1000;
		if (decimal)
			value /= 10;
		return value;
	}

	// Need to try this on server machine...
	private static void postJSON(JSONObject json) throws UnsupportedEncodingException {
		HttpPost postRequest = new HttpPost("http://localhost:5984/");// external IP: 192.168.1.212

		StringEntity jsonData = new StringEntity(json.toString());
		jsonData.setContentType("application/json");
		postRequest.setEntity(jsonData);

		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			client.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void turnOffWarnings(WebClient webClient) {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);

		webClient.setIncorrectnessListener(new IncorrectnessListener() {

			@Override
			public void notify(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}
		});
		webClient.setCssErrorHandler(new ErrorHandler() {

			@Override
			public void warning(CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void fatalError(CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void error(CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub

			}
		});
		webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

			@Override
			public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void scriptException(HtmlPage arg0, ScriptException arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
				// TODO Auto-generated method stub

			}
		});
		webClient.setHTMLParserListener(new HTMLParserListener() {

			@Override
			public void error(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
				// TODO Auto-generated method stub

			}

			@Override
			public void warning(String arg0, URL arg1, String arg2, int arg3, int arg4, String arg5) {
				// TODO Auto-generated method stub

			}
		});

		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}
}
