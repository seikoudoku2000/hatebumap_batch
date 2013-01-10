package sample.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.http.*;
import org.htmlparser.util.*;
import org.htmlparser.tags.*;
import org.htmlparser.nodes.*;

public class WebPageInfo {

	public static void main(String[] args) throws Exception {

		//String url = "http://gigazine.net/news/20111014_sbm_iphone_ios5/";
		//String url = "http://brow2ing.doorblog.jp/archives/1664682.html";
		String url = "http://togetter.com/li/186070";
		WebPageInfo w = new WebPageInfo(url);

		/*System.out.println("URL: " + url);
		System.out.println("Title: " + w.getTitle());
		 */
		String[] links = w.getLinkUrls();
		/*for (int i = 0; i < links.length; i++) {
			System.out.println("Link: " + links[i]);
		}*/

		String[] images = w.getImageUrls();
		/*for (int i = 0; i < images.length; i++) {
			System.out.println("Image: " + images[i]);
		}*/

		String[] texts = w.getTexts();
		String concatText = "";
		for (int i = 0; i < texts.length; i++) {
			//System.out.println("Text: " + texts[i]);
			if(texts[i] != null) {
				String nextText = texts[i].trim();
				if(shouldAnalyze(nextText)){            			
					//System.out.print(nextText + " ");
					concatText = concatText + nextText + "。 ";
					if(concatText.length() >= 1000) {
						System.out.println(concatText);
						concatText = "";
					}
				}
			}
		}

		String[] comments = w.getComments();
		/*for (int i = 0; i < comments.length; i++) {
			System.out.println("Comment: " + comments[i]);
		}*/

		// String[] tags = w.getTags();
		// for (int i = 0; i < tags.length; i++) {
		//   System.out.println("Tag: " + tags[i]);
		// }
	}
	
	
	public static String regex = "[a-zA-Z0-9].*";
	public static Pattern p = Pattern.compile(regex);
	
	/**
	 * このテキストを意味あるテキストと見なすか否かの判定
	 * @param targetText
	 * @return
	 */
	public static boolean shouldAnalyze(String targetText) {
		if(targetText.length() < 5 ||
			targetText.contains(";")  ||
			targetText.contains("名無し")
		) {
			return false;
		}
		/*
		Matcher m = p.matcher(targetText);
		if(m.find()) {
			return false;
		}
		*/
		return true;
	}
	
	private String title;
	private List<String> text = new ArrayList<String>();
	private List<String> comment = new ArrayList<String>();
	private List<LinkTag> linktag = new ArrayList<LinkTag>();
	private List<ImageTag> imagetag = new ArrayList<ImageTag>();
	private List<TagNode> tag = new ArrayList<TagNode>();

	public WebPageInfo(String url) throws Exception {

		Parser.getConnectionManager().setRedirectionProcessingEnabled(true);
		Parser.getConnectionManager().setCookieProcessingEnabled(true);

		Parser parser = new Parser();

		// URLを指定
		parser.setResource(url);

		// 抽出対象を指定
		NodeFilter filter = null;
		// AndFilter, OrFilter, RegexFilter, etc...
		// filter = new TagNameFilter("A");

		// パース
		try {
			parse(parser, filter);
		} catch (EncodingChangeException ece) {
			// Parser#reset した後でリトライすると適切なエンコーディングで処理してくれるらしい(未検証)
			ece.printStackTrace();
			parser.reset();
			parse(parser, filter);
		}
	}

	private void parse(Parser parser, NodeFilter filter) throws ParserException {
		NodeList list = parser.parse(filter);
		NodeIterator i = list.elements();
		while (i.hasMoreNodes()) {
			analyze(i.nextNode());
		}
	}

	public String getTitle() {
		return title;
	}

	public String[] getTexts() {
		return (String[]) text.toArray(new String[text.size()]);
	}

	public String[] getComments() {
		return (String[]) comment.toArray(new String[comment.size()]);
	}

	public String[] getLinkUrls() {
		String[] urls = new String[linktag.size()];
		for (int i = 0; i < linktag.size(); i++) {
			urls[i] = linktag.get(i).getLink(); // 絶対URL
		}
		return urls;
	}

	public String[] getImageUrls() {
		String[] urls = new String[imagetag.size()];
		for (int i = 0; i < imagetag.size(); i++) {
			urls[i] = imagetag.get(i).getImageURL(); // 絶対URL
		}
		return urls;
	}

	public String[] getTags() {
		String[] tegs = new String[tag.size()];
		for (int i = 0; i < tag.size(); i++) {
			tegs[i] = tag.get(i).toHtml();
		}
		return tegs;
	}

	private void analyze(Node node) throws ParserException {

		if (node instanceof TextNode) { // Text
			text.add(((TextNode) node).getText());

		} /*else if (node instanceof RemarkNode) { // Remark
			comment.add(((RemarkNode) node).getText());

		} else if (node instanceof TagNode) { // Tag

			if (node instanceof TitleTag) {
				title = ((TitleTag) node).getTitle();

			} else if (node instanceof LinkTag) {
				linktag.add((LinkTag) node);

			} else if (node instanceof ImageTag) {
				imagetag.add((ImageTag) node);

			}

			tag.add((TagNode) node);
		}*/

		// 再帰
		NodeList children = node.getChildren();
		if (children != null) {
			NodeIterator i = children.elements();
			while (i.hasMoreNodes())
				analyze(i.nextNode());
		}
	}

}
