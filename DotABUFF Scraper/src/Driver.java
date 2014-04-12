import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableHeader;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

public class Driver {
	public static void main(String[] args) {
		int mID;
		String lobbyType;
		String gameMode;
		String region;
		Time duration;
		boolean dRVictory;
		Timestamp timestamp;
		PlayerInstance[] players;
		
		try {
			scrapeMatch(605912917);
			WebClient webClient = new WebClient();
			turnOffWarnings(webClient);
			HtmlPage page = webClient.getPage("http://dotabuff.com/matches/605912917");

			HtmlDivision div = (HtmlDivision) page.getByXPath("//div[@class='team-results']").get(0);
			for (HtmlElement he : div.getHtmlElementsByTagName("section")) {
				HtmlElement teamTable = he.getHtmlElementsByTagName("tbody").get(0);
				for (HtmlElement player : teamTable.getHtmlElementsByTagName("tr")) { 
					System.out.println(player.asText());// .replace("\n", ""));
				}
				System.out.println();
			}

			System.out.println();

			webClient.closeAllWindows();
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Match scrapeMatch(int id) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		turnOffWarnings(webClient);
		HtmlPage page = webClient.getPage("http://dotabuff.com/matches/" + id);
		List<HtmlTable> teamTables = (List<HtmlTable>) page.getByXPath("//table");
		for (HtmlTable team : teamTables) {
			HtmlTableHeader header = team.getHeader();
			List<HtmlTableRow> headerRows = header.getRows();
			for (HtmlTableRow row : headerRows) {
				System.out.println("Column Headers:");
				for (HtmlTableCell cell : row.getCells()) {
					System.out.print(cell.asText() + " ");
				}
				System.out.println();
			}
			for (HtmlTableBody body : team.getBodies()) {
				List<HtmlTableRow> rows = body.getRows();
				for (HtmlTableRow row : rows) {
					for (final HtmlTableCell cell : row.getCells()) {
						System.out.println("   Found cell: " + cell.asText());
					}
				}

			}
		}
		webClient.closeAllWindows();

		return new Match();
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
