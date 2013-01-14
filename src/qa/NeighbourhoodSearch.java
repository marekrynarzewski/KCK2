package qa;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import morfologik.stemming.PolishStemmer;
import morfologik.stemming.WordData;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.TextExtractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NeighbourhoodSearch {
	private String adj;
	private String X;
	private String Y;
	boolean leaveAsIs = false;

	public NeighbourhoodSearch(String adj, String X, String Y) {
		this.adj = adj;
		this.X = X;
		this.Y = Y;
	}

	public NeighbourhoodSearch(String adj, String X, String Y,
			boolean leaveAsIs) {
		this.adj = adj;
		this.X = X;
		this.Y = Y;
		this.leaveAsIs = leaveAsIs;
	}

	public void search() throws IOException, JSONException {
		String gQuery = adj + X + " w " + Y;

		// The request also includes the userip parameter which provides the end
		// user's IP address. Doing so will help distinguish this legitimate
		// server-side traffic from traffic which doesn't come from an end-user.
		URL queryUrl = new URL(
				"https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
						+ "q=" + URLEncoder.encode(gQuery, "UTF-8"));
		System.out.println(queryUrl);
		URLConnection connection = queryUrl.openConnection();
		//connection.addRequestProperty("Referer", /* Enter the URL of your site here */);

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONObject json = new JSONObject(builder.toString());
		// now have some fun with the results...
		System.out.println(json.toString());
		JSONArray results = json.getJSONObject("responseData").getJSONArray("results");
		for (int i = 0; i < results.length(); ++i) {
			String url = results.getJSONObject(i).getString("url");
			System.out.println("### Checking out " + url + "...");
			getTextFromUrl(url);
		}
	}

	private void getTextFromUrl(String sourceUrlString) throws MalformedURLException, IOException {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
		MasonTagTypes.register();
		Source source = new Source(new URL(sourceUrlString));

		// Call fullSequentialParse manually as most of the source will be parsed.
		source.fullSequentialParse();

		/*System.out.println("Document title:");
		String title=getTitle(source);
		System.out.println(title==null ? "(none)" : title);

		System.out.println("\nDocument description:");
		String description=getMetaValue(source,"description");
		System.out.println(description==null ? "(none)" : description);

		System.out.println("\nDocument keywords:");
		String keywords=getMetaValue(source,"keywords");
		System.out.println(keywords==null ? "(none)" : keywords);*/

		/*System.out.println("\nLinks to other documents:");
		List<Element> linkElements=source.getAllElements(HTMLElementName.A);
		for (Element linkElement : linkElements) {
			String href=linkElement.getAttributeValue("href");
			if (href==null) continue;
			// A element can contain other tags so need to extract the text from it:
			String label=linkElement.getContent().getTextExtractor().toString();
			System.out.println(label+" <"+href+'>');
		}*/

		/*System.out.println("\nAll text from file (exluding content inside SCRIPT and STYLE ele7ments):\n");
		System.out.println(source.getTextExtractor().setIncludeAttributes(true).toString());*/



		System.out.println("### Text from page follows:");
		TextExtractor textExtractor=new TextExtractor(source) {
			public boolean excludeElement(StartTag startTag) {
				return startTag.getName() == HTMLElementName.SCRIPT
						|| startTag.getName() == HTMLElementName.STYLE;
			}
		};
		String text = textExtractor.setIncludeAttributes(false).toString();
		PolishStemmer polishStemmer = new PolishStemmer();
		Pattern p = Pattern.compile("[-a-zA-ZąśćżźęółńĄŚĆŻŹĘÓŁŃ]+");
		Matcher m = p.matcher(text);
		List<WordData> entries = new ArrayList<WordData>();
		while (m.find()) {
			String word = m.group();
			//System.out.println("### Looking up: " + word);
			
			if (! leaveAsIs)
				entries = polishStemmer.lookup(word.toLowerCase());

			if (entries.isEmpty())
				System.out.print(word + " ");
			else {
				System.out.print(entries.get(0).getStem().toString() + "* ");
				/*System.out.print("[");
				for (WordData wd : entries)
					System.out.print(wd.getStem().toString() + " ");
				System.out.print("] ");*/
			}
		}

	}
}

