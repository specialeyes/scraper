import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
	private static Gson gson = new Gson();

	public enum Heroes {
		abaddon("abaddon"), alchemist("alchemist"), ancient_apparition("ancient-apparition"), anti_mage("anti-mage"), axe("axe"), bane("bane"), batrider("batrider"), beastmaster("beastmaster"), bloodseeker("bloodseeker"), bounty_hunter("bounty-hunter"), brewmaster("brewmaster"), bristleback("bristleback"), broodmother("broodmother"), centaur_warrunner("centaur-warrunner"), chaos_knight("chaos-knight"), chen("chen"), clinkz("clinkz"), clockwerk("clockwerk"), crystal_maiden("crystal-maiden"), dark_seer("dark-seer"), dazzle("dazzle"), death_prophet("death-prophet"), disruptor("disruptor"), doom("doom"), dragon_knight("dragon-knight"), drow_ranger("drow-ranger"), earthshaker("earthshaker"), earth_spirit("earth-spirit"), elder_titan("elder-titan"), ember_spirit("ember-spirit"), enchantress("enchantress"), enigma("enigma"), faceless_void("faceless-void"), gyrocopter("gyrocopter"), huskar("huskar"), invoker("invoker"), io("io"), jakiro("jakiro"), juggernaut("juggernaut"), keeper_of_the_light("keeper-of-the-light"), kunkka("kunkka"), legion_commander("legion-commander"), leshrac("leshrac"), lich("lich"), lifestealer("lifestealer"), lina("lina"), lion("lion"), lone_druid("lone-druid"), luna("luna"), lycan("lycan"), magnus("magnus"), medusa("medusa"), meepo("meepo"), mirana("mirana"), morphling("morphling"), naga_siren("naga-siren"), natures_prophet("natures-prophet"), necrophos("necrophos"), night_stalker("night-stalker"), nyx_assassin("nyx-assassin"), ogre_magi("ogre-magi"), omniknight("omniknight"), outworld_devourer("outworld-devourer"), phantom_assassin("phantom-assassin"), phantom_lancer("phantom-lancer"), phoenix("phoenix"), puck("puck"), pudge("pudge"), pugna("pugna"), queen_of_pain("queen-of-pain"), razor("razor"), riki("riki"), rubick("rubick"), sand_king("sand-king"), shadow_demon("shadow-demon"), shadow_fiend("shadow-fiend"), shadow_shaman("shadow-shaman"), silencer("silencer"), skywrath_mage("skywrath-mage"), slardar("slardar"), slark("slark"), sniper("sniper"), spectre("spectre"), spirit_breaker("spirit-breaker"), storm_spirit("storm-spirit"), sven("sven"), templar_assassin("templar-assassin"), terrorblade("terrorblade"), tidehunter("tidehunter"), timbersaw("timbersaw"), tinker("tinker"), tiny("tiny"), treant_protector("treant-protector"), troll_warlord("troll-warlord"), tusk("tusk"), undying("undying"), ursa("ursa"), vengeful_spirit("vengeful-spirit"), venomancer("venomancer"), viper("viper"), visage("visage"), warlock("warlock"), weaver("weaver"), windranger("windranger"), witch_doctor("witch-doctor"), wraith_king("wraith-king"), zeus("zeus");
		private String uid;

		private Heroes(String uid) {
			this.uid = uid;
		}

		public String getUID() {
			return uid;
		}
	}

	private static class ItemMeta {
		String id, itemName, itemImageURL;
		int matches;
		double winrate;

		public ItemMeta(String id, String itemName, String itemImageURL, int matches, double winrate) {
			super();
			this.id = id;
			this.itemName = itemName;
			this.itemImageURL = itemImageURL;
			this.matches = matches;
			this.winrate = winrate;
		}
	}

	/**
	 * Generates the data files used by Team SpecialEyes's InfoVis project.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * Specify range of match IDs to scrape, and the directory to store the output in.
		 */
		String outputDirectory = "D:\\Desktop\\matches";
		int start = 490600006;
		int end = 490600016;
		// scrapeMatches(outputDirectory, start);
		// [start,end)
		scrapeMatches(outputDirectory, start, end);

		generateTeamCompositionStats(outputDirectory, outputDirectory + "\\teamCompositionStats");

		generateItemMetaMap(outputDirectory + "\\itemWR");
	}

	private static void scrapeMatches(String outputDirectory, int matchID) {
		scrapeMatches(outputDirectory, matchID, matchID + 1);
	}

	private static void scrapeMatches(String outputDirectory, int start, int end) {
		Match match;
		File f = new File(outputDirectory);
		f.mkdirs();
		System.out.println("Scraping matches.");
		for (int i = start; i < end; i++) {
			try {
				match = scrapeMatch(i);
				String json = gson.toJson(match);
				System.out.println(json);
				f = new File(outputDirectory + "\\" + i + ".json");
				BufferedWriter writer = new BufferedWriter(new FileWriter(f));
				try {
					f.getParentFile().mkdirs();
					f.createNewFile();
					writer.write(json);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					writer.close();
				}

			} catch (MatchNotFoundException e) {
				System.out.println("Match " + i + " not found!");
			} catch (IOException e) {
				System.out.println("Failed to scrape page!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	private static void generateTeamCompositionStats(String matchDirectory, String outputDirectory) {
		HashMap<String, TeamComposition> teams = new HashMap<String, TeamComposition>();
		System.out.println("Generating team composition stats.");
		try {
			BufferedReader buff = null;
			File folder = new File(matchDirectory);
			File[] listOfFiles = folder.listFiles();
			String line, temp;
			int duration = 0, index = 0;
			String[] heroes = new String[5];
			boolean radiantVictory = false;

			for (File f : listOfFiles) {
				buff = new BufferedReader(new FileReader(f));
				dirty: while ((line = buff.readLine()) != null) {
					Match match = gson.fromJson(line, Match.class);
					temp = match.getDuration();
					duration = Integer.parseInt(temp.substring(0, temp.indexOf(":"))) * 60;
					duration += Integer.parseInt(temp.substring(temp.indexOf(":") + 1, temp.indexOf(":") + 3));
					radiantVictory = match.isRadiantVictory();
					duration /= 5;
					if (duration > 17)
						duration = 17;
					for (int i = 0; i < 10; i += 5) {
						for (int j = i; j < i + 5; j++) {
							heroes[j % 5] = match.getPlayers()[j].getHeroName();
							if (heroes[j % 5].contains("class")) {
								System.out.println("Skipping " + match.getmID() + ": " + match.getLobbyType() + " (Abandon)");
								continue dirty;
							}
						}
						Arrays.sort(heroes);
						// System.out.println(Arrays.toString(heroes));
						for (int j = 0; j < 5; j++) {
							temp = heroes[j];
							// System.out.println(temp);
							recordGame(teams, temp, duration, radiantVictory);
							for (int k = j + 1; k < 5; k++) {
								temp = heroes[j];
								temp += heroes[k];
								// System.out.println(temp);
								recordGame(teams, temp, duration, radiantVictory);
								for (int l = k + 1; l < 5; l++) {
									temp = heroes[j];
									temp += heroes[k];
									temp += heroes[l];
									// System.out.println(temp);
									recordGame(teams, temp, duration, radiantVictory);
									for (int m = l + 1; m < 5; m++) {
										temp = heroes[j];
										temp += heroes[k];
										temp += heroes[l];
										temp += heroes[m];
										// System.out.println(temp);
										recordGame(teams, temp, duration, radiantVictory);
										for (int n = m + 1; n < 5; n++) {
											temp = heroes[j];
											temp += heroes[k];
											temp += heroes[l];
											temp += heroes[m];
											temp += heroes[n];
											// System.out.println(temp);
											recordGame(teams, temp, duration, radiantVictory);
										}
									}
								}
							}
						}
						radiantVictory = !radiantVictory;
					}
				}
				index++;
				System.out.println(index + "/" + listOfFiles.length);
			}
			if (buff != null)
				buff.close();

			File output = new File(outputDirectory);
			output.mkdirs();
			System.out.println("Writing output...");
			index = 0;
			duration = teams.keySet().size();
			for (String s : teams.keySet()) {
				if (index % 100 == 0)
					System.out.printf("Writing (%06d/%d) %f%%\n", index, duration, (index * 100.0 / duration));
				output = new File(outputDirectory + "\\" + s + ".json");
				BufferedWriter writer = new BufferedWriter(new FileWriter(output));
				writer.write(gson.toJson(teams.get(s)));
				writer.close();
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
	}

	private static void recordGame(HashMap<String, TeamComposition> teams, String teamComp, int duration, boolean radiantVictory) {
		if (!teams.containsKey(teamComp)) {
			teams.put(teamComp, new TeamComposition());
		}
		teams.get(teamComp).record(duration, radiantVictory);
	}

	private static void generateItemMetaMap(String outputDirectory) {
		Gson gson = new Gson();
		HashMap<String, ArrayList<ItemMeta>> map = new HashMap<String, ArrayList<ItemMeta>>();
		int i = 0;
		int max = Heroes.values().length;
		System.out.println("Generating item stats file.");
		for (Heroes h : Heroes.values()) {
			try {
				ArrayList<Object[]> heroItems = scrapeItemsForHero(h.uid);
				final ArrayList<ItemMeta> itemList = new ArrayList<ItemMeta>();

				for (Object[] s : heroItems) {
					String id = (String) s[0];
					String itemName = (String) s[1];
					String itemImageURL = (String) s[2];
					int matches = Integer.parseInt(((String) s[3]).replace(",", ""));
					double winrate = Double.parseDouble(((String) s[4]).replace("%", ""));

					itemList.add(new ItemMeta(id, itemName, itemImageURL, matches, winrate));
				}

				map.put(h.uid, itemList);
				i++;
				System.out.println((int) (i / (max * 1.0) * 100) + "%");
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File f = new File(outputDirectory);
		f.mkdirs();
		BufferedWriter writer = null;
		try {
			f = new File(outputDirectory + "\\itemWinrates.json");
			writer = new BufferedWriter(new FileWriter(f));
			writer.write(gson.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done.");
	}

	private static Match scrapeMatch(int id) throws MatchNotFoundException, IOException {
		// Establish connection to webpage
		WebClient webClient = new WebClient();
		turnOffWarnings(webClient);
		HtmlPage page = webClient.getPage("http://dotabuff.com/matches/" + id);
		// Check if match exists or not
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

	private static ArrayList<Object[]> scrapeItemsForHero(String heroID) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		// Establish connection with webpage (only care about use after patch 6.80)
		WebClient webClient = new WebClient();
		turnOffWarnings(webClient);
		HtmlPage page = webClient.getPage("http://dotabuff.com/heroes/" + heroID + "/items?date=patch_6.80");

		ArrayList<Object[]> data = new ArrayList<Object[]>();

		// Pull information from table [item, matchesPlayed, winRate]
		HtmlTable itemTable = ((List<HtmlTable>) page.getByXPath("//table")).get(0);
		HtmlTableHeader header = itemTable.getHeader();
		List<HtmlTableRow> headerRows = header.getRows();
		for (HtmlTableBody body : itemTable.getBodies()) {
			List<HtmlTableRow> rows = body.getRows();
			for (HtmlTableRow row : rows) {
				String matchesPlayed, winRate, itemName = "", itemImageURL = "";
				String itemID = row.asXml();
				itemName = itemImageURL = itemID = itemID.substring(itemID.indexOf("/items/"));
				itemID = itemID.substring(7, itemID.indexOf("\">"));

				itemName = itemName.substring(itemName.indexOf("alt=") + 5);
				itemName = itemName.substring(0, itemName.indexOf("\""));

				itemImageURL = itemImageURL.substring(itemImageURL.indexOf("src=") + 5);
				itemImageURL = itemImageURL.substring(0, itemImageURL.indexOf("\""));

				matchesPlayed = row.getCell(2).asText();
				winRate = row.getCell(3).asText();

				data.add(new String[] { itemID, itemName, itemImageURL, matchesPlayed, winRate });
			}
		}
		webClient.closeAllWindows();

		return data;
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
