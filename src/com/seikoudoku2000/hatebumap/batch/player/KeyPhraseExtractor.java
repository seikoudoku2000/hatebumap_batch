package com.seikoudoku2000.hatebumap.batch.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 特徴語の抽出を司るクラス
 * @author yosuke-tomita
 *
 */
public class KeyPhraseExtractor {

	//APIのベースURL
	private static String requesturl = "http://jlp.yahooapis.jp/KeyphraseService/V1/extract";
	//アプリケーションID
	private static String appid = "SwBXezCxg65SWW5hZOIEvm8gbFwuOhvWv11nBoQKzvOWBX5KuBpl8Wkgq0WjfI0WZxtuFyM-";
	
	
	/**
	 * 特徴語抽出用の関数
	 * @param wordsList 特徴語を抽出したい文章のリスト。APIが、その文章内で特徴的な単語を抽出するという特性を持っているため、ある程度の長さの文章を配列に格納しておくこと
	 * @return　特徴語のSet<String>
	 * @throws Exception
	 */
	public static Set<String> getKeyPhraseSet(List<String> wordsList) throws Exception {
		System.out.println("start getKeyPhraseSet!!");
		
		Set<String> keyPhraseSet = new HashSet<String>();
		for(String words : wordsList) {
			// キーフレーズ抽出APIを使う
			String parameters = getParametersString(appid, words);
			String xmlContent = getContent(new URL(requesturl), parameters);
			Document doc = getDocument(xmlContent);
			ResultSet rs = getResultSet(doc);
			for(int i=0; i<rs.result.length; i++){
				keyPhraseSet.add(rs.result[i].Keyphrase);
				System.out.println(rs.result[i].Keyphrase);
			}
		}
		return keyPhraseSet;
	}
	
	
	private static String getParametersString(String appid, String sentence) throws UnsupportedEncodingException{
		String parameters =
			"appid=" + appid +
			"&" +
			"sentence=" + URLEncoder.encode(sentence, "UTF-8");
		return parameters;
	}

	private static int getLength(XPath xpath, Document doc, String expression) throws XPathExpressionException{
		NodeList nodelist = (NodeList)xpath.evaluate(expression, doc, XPathConstants.NODESET);
		if(nodelist != null){
			return nodelist.getLength();
		}else{
			return 0;
		}
	}

	private static String getString(XPath xpath, Document doc, String expression) throws XPathExpressionException{
		return xpath.evaluate(expression, doc);
	}

	// XML 文書文字列からDocumentオブジェクトを生成
	private static Document getDocument(String xmlContent) throws IOException, SAXException, ParserConfigurationException {
		StringReader sr = new StringReader(xmlContent);
		InputSource is = new InputSource(sr);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		return doc;
	}

	// URLからコンテンツ(HTML/XMLページの文字列)を取得
	private static String getContent(URL url, String parameters) throws IOException, ParseException {

		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.connect();

		OutputStream os = con.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(parameters);
		bw.flush();
		bw.close();

		// ex. String ct = "text/xml; charset=\"utf-8\"";
		String ct = con.getContentType();
		String charset = "UTF-8";
		if(ct != null){
			String cs = new ContentType(ct).getParameter("charset");
			if(cs != null){
				charset = cs;
			}
		}

		InputStream is = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, charset);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer buf = new StringBuffer();
		String s;
		while ((s = br.readLine()) != null) {
			buf.append(s);
			buf.append("\r\n"); // 改行コードKIMEUCHI
		}
		br.close();
		con.disconnect();

		return buf.toString();
	}

	private static ResultSet getResultSet(Document doc) throws Exception {

		XPath xpath = XPathFactory.newInstance().newXPath();

		int length = getLength(xpath, doc, "ResultSet/Result");
		Result[] r = new Result[length]; 
		for(int i=0; i<length; i++){
			r[i] = new Result();
			r[i].Keyphrase = getString(xpath, doc, "ResultSet/Result[" + (i+1) + "]/Keyphrase");
			r[i].Score     = getString(xpath, doc, "ResultSet/Result[" + (i+1) + "]/Score");
		}

		ResultSet rs = new ResultSet();
		rs.result = r;
		return rs;
	}

	public static class ResultSet{
		public Result[] result;
	}

	public static class Result{
		public String Keyphrase; // キーフレーズ
		public String Score; // キーフレーズの重要度
	}
	
	
	public static void main(String args[]) throws Exception {
		List<String> strList = WebPageParser.parseWebPage("http://alfalfalfa.com/archives/399432.html");
		Set<String> resultSet = getKeyPhraseSet(strList);
		for(String str : resultSet) {
			System.out.println(str);
		}
	}
}
