package sample.neko;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;

public class NekoSample {

	static String[] urlArray = {
		"http://gigazine.net/news/20111014_sbm_iphone_ios5/",
		"http://mamesoku.com/archives/3090665.html"
		};
	
	public static void main(String[] argv) throws Exception {
		DOMParser parser = new DOMParser();
		for (int i = 0; i < urlArray.length; i++) {
			parser.parse(urlArray[i]);
			print(parser.getDocument(), "");
		}
	}
	public static void print(Node node, String indent) {
		System.out.println(indent+node.getClass().getName());
		Node child = node.getFirstChild();
		while (child != null) {
			print(child, indent+" ");
			child = child.getNextSibling();
		}
	}

}
