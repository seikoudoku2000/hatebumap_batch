package com.seikoudoku2000.hatebumap.batch.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
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

import net.geohex.GeoHex;
import net.geohex.GeoHex.Zone;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * POI特定のための検索を司るクラス
 * @author tomitayousuke
 *
 */
public class LocalSearcher {

	// リクエストURL
	private static String REQUEST_URL = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch";
	//APP_ID -> 自分で取得すること。
	private static String APP_ID = "hogefuga"; // <- アプリケーションIDを取得して設定
	
	public static final String SPLITTER = ",";
	
	
	/**
	 * 単語のリストを順にローカルサーチ検索し、POIデータを取得するための関数
	 * @param wordSet
	 * @param adCode
	 * @param genreCode
	 * @return
	 */
	public static Set<String> getPOIData(Set<String> wordSet, String adCode, String genreCode) {
		System.out.println("start getPOIData!!");
		
		Set<String> resultSet = new HashSet<String>();
		for(String word : wordSet) {
			List<String> recordListForWord = getPlaceRecord(word, adCode, genreCode);
			if(recordListForWord.size() > 0) {
				resultSet.addAll(recordListForWord);		
			}
		}
		return resultSet;
	}
	
	/**
	 * 単語をローカルサーチ検索し、POIデータを取得するための関数
	 * @param word
	 * @param adCode
	 * @param genreCode
	 * @return
	 */
	public static Set<String> getPOIData(String word, String adCode, String genreCode) {
		Set<String> resultSet = new HashSet<String>();
		List<String> recordListForWord = getPlaceRecord(word, adCode, genreCode);
		if(recordListForWord.size() > 0) {
			resultSet.addAll(recordListForWord);		
		}
		return resultSet;
	}

	
	private static List<String> getPlaceRecord(String searchWord, String adCode, String genreCode){
		List<String> resultList = new ArrayList<String>();
		try{
			// ローカルサーチAPIを使う
			String parameters = getParametersString(searchWord, adCode, genreCode);
			String xmlContent = getContent(new URL(REQUEST_URL+parameters), parameters);
			Document doc = getDocument(xmlContent);
			YDF ydf = getResultSet(doc);

			// 結果を出力
			int resultTotal = ydf.resultInfo.Total;

			//多すぎる時は地名や一般名刺の可能性が高いので除去
			//チェーン店の場合が難しい。。
			if(resultTotal > 0 && resultTotal <= 10) {
				//System.out.println(sentence + " , 総計：" + resultTotal);
				for(int i=0; i<ydf.featureArray.length; i++){
					/*
					System.out.println(ydf.featureArray[i].Name + " " 
							+ ydf.featureArray[i].geometry.Coordinates + " "
							+ ydf.featureArray[i].geometry.Type);
					 */
					String[] lonLat = ydf.featureArray[i].geometry.Coordinates.split(",");
					if(lonLat.length == 2){
						StringBuilder sb = new StringBuilder();
						//スポット名
						sb.append(ydf.featureArray[i].Name).append(SPLITTER);
						//level9-14のZoneのx座標とy座標。
						//地図上にマッピングするときは座標での範囲検索を行う予定なので、このような形式にしている。
						for(int level = 9; level <= 14; level++) {
							Zone zone = GeoHex.getZoneByLocation(
									Double.parseDouble(lonLat[1]), Double.parseDouble(lonLat[0]), level);
							sb.append((int)zone.x).append(SPLITTER).append((int)zone.y).append(SPLITTER);
						}
						//緯度経度
						sb.append(lonLat[1]).append(SPLITTER).append(lonLat[0]);
						//System.out.println(sb.toString());
						resultList.add(sb.toString());
					}
				}
				//System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return resultList;
	}

	private static String getParametersString(String sentence, String adCode, String genreCode) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder("?appid=" + APP_ID);
		if(adCode != null && adCode.length() > 0) {
				sb.append("&ac=" + adCode);
		}
		if(genreCode != null && genreCode.length() > 0) {
			sb.append("&gc=" + genreCode);
		}
		sb.append("&results=10");
		sb.append("&query=" + URLEncoder.encode(sentence, "UTF-8"));
		return sb.toString();
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
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.connect();

		//OutputStream os = con.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		//BufferedWriter bw = new BufferedWriter(osw);
		//bw.write(parameters);
		//bw.flush();
		//bw.close();

		// ex. String ct = "text/xml; charset=\"utf-8\"";
		String ct = con.getContentType();
		String charset = "UTF-8"; // Content Type が無ければ UTF-8 KIMEUCHI
		if(ct != null){
			// JavaMail なクラス ContentType
			// Java SE にあればいいのに……
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

	private static YDF getResultSet(Document doc) throws Exception {

		XPath xpath = XPathFactory.newInstance().newXPath();

		YDF ydf = new YDF();
		ydf.resultInfo = new ResultInfo();
		ydf.resultInfo.Total = Integer.parseInt(getString(xpath, doc, "YDF/ResultInfo/Total"));

		int length = getLength(xpath, doc, "YDF/Feature");
		Feature[] featureArray = new Feature[length]; 
		for(int i=0; i<length; i++){
			featureArray[i] = new Feature();
			featureArray[i].Name = getString(xpath, doc, "YDF/Feature[" + (i+1) + "]/Name");
			featureArray[i].geometry = new Geometry();
			featureArray[i].geometry.Type =	getString(xpath, doc, "YDF/Feature[" + (i+1) + "]/Geometry/Type");
			featureArray[i].geometry.Coordinates =	getString(xpath, doc, "YDF/Feature[" + (i+1) + "]/Geometry/Coordinates");
		}

		ydf.featureArray = featureArray;
		return ydf;
	}

	public static class YDF{
		public ResultInfo resultInfo;
		public Feature[] featureArray;
	}

	public static class ResultInfo {
		public int Count;
		public int Total;
		public int Start;
		public int Status;
	}

	public static class Feature {
		public String Name; // 名称
		public Geometry geometry;
	}

	public static class Geometry {
		public String Type;
		public String Coordinates;
	}
	
	public static void main(String[] args) throws Exception {
		/*
		List<String> strList = WebPageParser.parseWebPage("http://alfalfalfa.com/archives/399432.html");
		Set<String> wordSet = KeyPhraseExtractor.getKeyPhraseSet(strList);
		Set<String> resultSet = getPOIData(wordSet, "12", "0106");
		for(String result : resultSet) {
			System.out.println(result);
		}
		*/
		Set<String> resultSet = getPOIData("大盛堂書店", "13", null);
		for(String result : resultSet) {
			System.out.println(result);
		}
	}
}
