package com.seikoudoku2000.hatebumap.batch.player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * web�y�[�W�̉�͂��i��N���X
 * @author yosuke-tomita
 *
 */
public class WebPageParser {

	private static String regex = "[a-zA-Z0-9].*";
	private static Pattern p = Pattern.compile(regex);
	private static List<String> text = new ArrayList<String>();
	private static int SENTENCE_LEGTH = 1000;
	
	/**
	 * web�y�[�W����͂�������𒊏o���邽�߂̊֐�
	 * @param targetUrl
	 * @return 
	 * @throws Exception
	 */
	public static List<String> parseWebPage(String targetUrl) throws Exception {
		System.out.println("start parseWebPage!!");
		
		Parser.getConnectionManager().setRedirectionProcessingEnabled(true);
		Parser.getConnectionManager().setCookieProcessingEnabled(true);

		Parser parser = new Parser();

		// URL���w��
		parser.setResource(targetUrl);

		// ���o�Ώۂ��w��
		NodeFilter filter = null;
		// AndFilter, OrFilter, RegexFilter, etc...
		// filter = new TagNameFilter("A");

		// �p�[�X
		try {
			parse(parser, filter);
		} catch (EncodingChangeException ece) {
			ece.printStackTrace();
			parser.reset();
			parse(parser, filter);
		}
		
		
		String[] texts = getTexts();
		List<String> wordsList = new ArrayList<String>();
		String concatText = new String("");
		for (int i = 0; i < texts.length; i++) {
			//System.out.println("Text: " + texts[i]);
			if(texts[i] != null) {
				String nextText = texts[i].trim();
				if(shouldAnalyze(nextText)){            			
					//�����ꒊ�oAPI���^����ꂽ���͓��ő��ΓI�ɓ���������o���Ƃ������������邽�߁A������x�̒����Ɍ������Ă��B
					concatText = concatText + nextText + "�B ";
					if(concatText.length() >= SENTENCE_LEGTH) {
						wordsList.add(concatText);
						concatText = new String("");
					}
				}
			}
		}
		
		return wordsList;
	}
	
	
	/**
	 * ���̃e�L�X�g���Ӗ�����e�L�X�g�ƌ��Ȃ����ۂ��̔���
	 * @param targetText
	 * @return
	 */
	public static boolean shouldAnalyze(String targetText) {
		if(targetText.length() < 5 ||
			targetText.contains(";")  ||
			targetText.contains("������")
		) {
			return false;
		}
		
		Matcher m = p.matcher(targetText);
		if(m.find()) {
			return false;
		}
		
		return true;
	}
	
	private static void parse(Parser parser, NodeFilter filter) throws ParserException {
		NodeList list = parser.parse(filter);
		NodeIterator i = list.elements();
		while (i.hasMoreNodes()) {
			analyze(i.nextNode());
		}
	}

	private static String[] getTexts() {
		return (String[]) text.toArray(new String[text.size()]);
	}
	
	private static void analyze(Node node) throws ParserException {

		if (node instanceof TextNode) { // Text
			text.add(((TextNode) node).getText());
		}

		// �ċA
		NodeList children = node.getChildren();
		if (children != null) {
			NodeIterator i = children.elements();
			while (i.hasMoreNodes())
				analyze(i.nextNode());
		}
	}

	
	public static void main(String[] args) throws Exception {
		List<String> resultList = parseWebPage("http://alfalfalfa.com/archives/399432.html");
		for(String str : resultList) {
			System.out.println(str);
		}
	}
}
