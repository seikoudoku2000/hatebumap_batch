package sample.post;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import net.geohex.GeoHex;
import net.geohex.GeoHex.Zone;

import org.w3c.dom.*;
import org.xml.sax.*;

public class LocalSearchSample {
	public static void main(String[] args) throws Exception {

		//���̓p�����[�^
		String appid = "SwBXezCxg65SWW5hZOIEvm8gbFwuOhvWv11nBoQKzvOWBX5KuBpl8Wkgq0WjfI0WZxtuFyM-"; // <- �A�v���P�[�V����ID���擾���Đݒ�
		//�s���{���R�[�h 
		String adCode = "26";


		/*
		for(int i=0; i<sentence.length; i++){
			doit(appid, sentence[i]);
		}
		 */


		BufferedReader bufferReader = null; 
		String str = ""; 
		try { 
			bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/tomitayousuke/eclipse/workspace/hatebumap/sample/src/words.txt"),"Shift_JIS")); 

			Set<String> wordSet = new HashSet<String>();
			//��s���ǂݍ��݂Ȃ���A�d������P��͏���
			while ((str = bufferReader.readLine()) != null) { 
				wordSet.add(str);
			}
			//�e�P�ꖈ�Ƀ��[�J���T�[�`�����s����
			for(String nextWord : wordSet) {
				doit(appid, nextWord, adCode);
			}

		} catch (Exception e) { 
			System.out.println(e.getMessage()); 
		} finally { 
			try { 
				if(bufferReader != null){ 
					bufferReader.close(); 
				} 
			} catch(Exception e) { 
			} 
		}

	}

	private static void doit(String appid, String sentence, String adCode){
		try{
			// ���N�G�X�gURL
			String requesturl = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch";

			// ���[�J���T�[�`API���g��
			String parameters = getParametersString(appid, sentence, adCode);
			String xmlContent = getContent(new URL(requesturl+parameters), parameters);
			Document doc = getDocument(xmlContent);
			YDF ydf = getResultSet(doc);

			// ���ʂ��o��
			int resultTotal = ydf.resultInfo.Total;
			
			//�������鎞�͒n�����ʖ��h�̉\���������̂ŏ���
			//�`�F�[���X�̏ꍇ������B�B
			if(resultTotal > 0 && resultTotal <= 10) {
				//System.out.println(sentence + " , ���v�F" + resultTotal);
				for(int i=0; i<ydf.featureArray.length; i++){
					/*
					System.out.println(ydf.featureArray[i].Name + " " 
							+ ydf.featureArray[i].geometry.Coordinates + " "
							+ ydf.featureArray[i].geometry.Type);
					 */
					String[] lonLat = ydf.featureArray[i].geometry.Coordinates.split(",");
					if(lonLat.length == 2){
						StringBuilder sb = new StringBuilder();
						//�X�|�b�g��
						sb.append(ydf.featureArray[i].Name).append("\t");
						//level9-14��Zone��x���W��y���W�B
						//�n�}��Ƀ}�b�s���O����Ƃ��͍��W�ł͈̔͌������s���\��Ȃ̂ŁA���̂悤�Ȍ`���ɂ��Ă���B
						for(int level = 9; level <= 14; level++) {
							Zone zone = GeoHex.getZoneByLocation(
									Double.parseDouble(lonLat[1]), Double.parseDouble(lonLat[0]), level);
							sb.append((int)zone.x).append("\t").append((int)zone.y).append("\t");
						}
						//�ܓx�o�x
						sb.append(lonLat[1]).append("\t").append(lonLat[0]);
						System.out.println(sb.toString());
					}

			}
			//System.out.println();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}

private static String getParametersString(String appid, String sentence, String adCode) throws UnsupportedEncodingException{
	String parameters =
		"?appid=" + appid +
		"&ac=" + adCode +
		"&gc=" + "0106" +
		//"&gc=" + "0106001" +
		"&result" + "10" + 
		"&query=" + URLEncoder.encode(sentence, "UTF-8");
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

// XML ���������񂩂�Document�I�u�W�F�N�g�𐶐�
private static Document getDocument(String xmlContent) throws IOException, SAXException, ParserConfigurationException {
	StringReader sr = new StringReader(xmlContent);
	InputSource is = new InputSource(sr);
	Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	return doc;
}

// URL����R���e���c(HTML/XML�y�[�W�̕�����)���擾
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
	String charset = "UTF-8"; // Content Type ��������� UTF-8 KIMEUCHI
	if(ct != null){
		// JavaMail �ȃN���X ContentType
		// Java SE �ɂ���΂����̂Ɂc�c
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
		buf.append("\r\n"); // ���s�R�[�hKIMEUCHI
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
	public String Name; // ����
	public Geometry geometry;
}

public static class Geometry {
	public String Type;
	public String Coordinates;
}

}
