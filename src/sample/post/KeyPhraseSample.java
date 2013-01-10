package sample.post;
import java.io.*;
import java.net.*;

import javax.mail.internet.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class KeyPhraseSample {
	public static void main(String[] args) throws Exception {

		// ���̓p�����[�^
		String appid = "SwBXezCxg65SWW5hZOIEvm8gbFwuOhvWv11nBoQKzvOWBX5KuBpl8Wkgq0WjfI0WZxtuFyM-"; // <- �A�v���P�[�V����ID���擾���Đݒ�

		/*
		for(int i=0; i<sentence.length; i++){
			doit(appid, sentence[i]);
		}
		 */
		
		
		BufferedReader bufferReader = null; 
		String str = ""; 
		try { 
			bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/tomitayousuke/eclipse/workspace/hatebumap/sample/src/parsedStrings.txt"),"Shift_JIS")); 

			//��s���ǂݍ��� 
			while ((str = bufferReader.readLine()) != null) { 
				//System.out.println(str);
				doit(appid, str);
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

	private static void doit(String appid, String sentence){
		try{
			// ���N�G�X�gURL
			String requesturl = "http://jlp.yahooapis.jp/KeyphraseService/V1/extract";

			// �L�[�t���[�Y���oAPI���g��
			String parameters = getParametersString(appid, sentence);
			String xmlContent = getContent(new URL(requesturl), parameters);
			Document doc = getDocument(xmlContent);
			ResultSet rs = getResultSet(doc);

			// ���ʂ��o��
			//System.out.println("[��͑Ώۂ̃e�L�X�g]");
			//System.out.println(sentence);
			//System.out.println("[��͌���]");
			for(int i=0; i<rs.result.length; i++){
				//System.out.println(rs.result[i].Keyphrase + ": " + rs.result[i].Score);
				System.out.println(rs.result[i].Keyphrase);
			}
			//System.out.println();

		}catch(Exception e){
			e.printStackTrace();
		}
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
		public String Keyphrase; // �L�[�t���[�Y
		public String Score; // �L�[�t���[�Y�̏d�v�x
	}
}
