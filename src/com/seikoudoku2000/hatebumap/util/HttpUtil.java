package com.seikoudoku2000.hatebumap.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	/**
	 * HTTP�ڑ����s���A���X�|���X��body��������擾����֐�
	 * @param urlStr �ڑ���URL�̕�����
	 * @param timeout�@�R�l�N�V�����^�C���A�E�g�܂ł̎��ԁB�~���b�Ŏw��B
	 * @return ���X�|���XBody�̕�����B�^�C���A�E�g��G���[��������null���Ԃ�B
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
	 * response header��contentType����A�����R�[�h�𔲂��o��
	 * @param contentType
	 * @return
	 */
	private String getCharSetString(String contentType) {
		//�擾�ł��Ȃ�����UTF-8�Ƃ��Ă��܂��B
		String charset = "UTF-8";
		int charsetIndex = contentType.indexOf("=");
		if(charsetIndex > 0){ 
			charset = contentType.substring(charsetIndex+1, contentType.length());
		}
		return charset;
	}
}
