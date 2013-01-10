package com.seikoudoku2000.hatebumap.batch.player;

import java.io.*;
import java.net.*;

public class HatebuCountChecker {

	/**
	 * ブックマーク数取得用の関数
	 * @param targetUrl
	 * @return
	 */
	public static int getHatebuCount(String targetUrl) {
		try {
			URL url = new URL("http://api.b.st-hatena.com/entry.count?url=" + targetUrl);
			Object content = url.getContent();
			if (content instanceof InputStream) {
				BufferedReader bf = new BufferedReader(new InputStreamReader( (InputStream)content) );        
				String line;
				while ((line = bf.readLine()) != null) {
					//System.out.println(line);
					try {
						int hatebuCount = Integer.parseInt(line);
						return hatebuCount;
					} catch(NumberFormatException nfe) {
						continue;
					}
				}
			}
			else {
				//System.out.println(content.toString());
			}
		} catch (IOException e) {
			System.err.println(e);
			return 0;
		}

		return 0;
	}
	
	
	public static void main(String[] args) {
		getHatebuCount("http://ma7.mashupaward.jp");
	}

}
