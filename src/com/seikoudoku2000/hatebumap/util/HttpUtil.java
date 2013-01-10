package com.seikoudoku2000.hatebumap.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	/**
	 * HTTP接続を行い、レスポンスのbody文字列を取得する関数
	 * @param urlStr 接続先URLの文字列
	 * @param timeout　コネクションタイムアウトまでの時間。ミリ秒で指定。
	 * @return レスポンスBodyの文字列。タイムアウトやエラー発生時はnullが返る。
	 */
	public String getResponseBody(String urlStr, int timeout) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConn 
				= (HttpURLConnection)url.openConnection();
			urlConn.setConnectTimeout(timeout);
			urlConn.connect();
			String charSet = getCharSetString(urlConn.getContentType());
			BufferedReader reader = 
				new BufferedReader(
						new InputStreamReader(urlConn.getInputStream(), charSet));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
			urlConn.disconnect();
			return sb.toString();

		} catch(Exception e) {
			return null;
		}
	}


	/**
	 * response headerのcontentTypeから、文字コードを抜き出す
	 * @param contentType
	 * @return
	 */
	private String getCharSetString(String contentType) {
		//取得できない時はUTF-8としてしまう。
		String charset = "UTF-8";
		int charsetIndex = contentType.indexOf("=");
		if(charsetIndex > 0){ 
			charset = contentType.substring(charsetIndex+1, contentType.length());
		}
		return charset;
	}
}
