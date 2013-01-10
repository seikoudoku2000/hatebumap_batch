package com.seikoudoku2000.hatebumap.batch.player;

import net.arnx.jsonic.JSON;

import com.seikoudoku2000.hatebumap.util.HttpUtil;


/**
 * はてぶのエントリー情報をAPI(http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo)
 * 経由で取得するためのクラス
 */
public class HatebuInfoGetter {
	
	/**
	 * はてぶエントリー情報取得APIの基礎のURL
	 */
	private static final String HATEBU_GET_INFO_URL = "http://b.hatena.ne.jp/entry/json/?url=";
	
	/**
	 * はてぶエントリー情報取得APIライト版の基礎のURL
	 */
	private static final String HATEBU_GET_INFO_LITE_URL = "http://b.hatena.ne.jp/entry/jsonlite/?url=";
	
	
	//HTTP接続を行うオブジェクト
	private static HttpUtil httpUtil = new HttpUtil();
	
	
	/**
	 * はてぶのエントリー情報取得。ライト版。
	 * @param targetUrl
	 * @return　HatebuInfoDto
	 */
	public static HatebuInfoDto getHatebuInfoLite(String targetUrl) {
		String responseJson = httpUtil.getResponseBody(getLiteQueryUrl(targetUrl), 10000);
		
		//結果をparseして返却
		if(responseJson != null) {
			HatebuInfoDto dto 
				= JSON.decode(responseJson, HatebuInfoDto.class);
			return dto;
		} else {
			return null;
		}
	}
	
	
	/**
	 * はてぶのエントリー情報取得。
	 *　##今はrelatedをDtoに定義していないので意味ない。
	 * @param targetUrl
	 * @return　HatebuInfoDto
	 */
	@Deprecated
	public static HatebuInfoDto getHatebuInfo(String targetUrl) {
		String responseJson = httpUtil.getResponseBody(getQueryUrl(targetUrl), 10000);
		
		//結果をparseして返却
		if(responseJson != null) {
			HatebuInfoDto dto 
				= JSON.decode(responseJson, HatebuInfoDto.class);
			return dto;
		} else {
			return null;
		}
	}
	
	
	private static String getLiteQueryUrl(String targetUrl) {
		return HATEBU_GET_INFO_LITE_URL + targetUrl;
	}
	
	private static String getQueryUrl(String targetUrl) {
		return HATEBU_GET_INFO_URL + targetUrl;
	}
	
	
	
	/**
	 * 結果格納用のDTO。
	 */
	public static class HatebuInfoDto {
		
		private String title;
		private int count;
		private String url;
		private String entry_url;
		private String screenshot;
		private long eid;
		
		//bookmarksとrelatedは今は入れていない。
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getEntry_url() {
			return entry_url;
		}
		public void setEntry_url(String entryUrl) {
			entry_url = entryUrl;
		}
		public String getScreenshot() {
			return screenshot;
		}
		public void setScreenshot(String screenshot) {
			this.screenshot = screenshot;
		}
		public long getEid() {
			return eid;
		}
		public void setEid(long eid) {
			this.eid = eid;
		}

		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("タイトル: ").append(title).append("\r\n");
			sb.append("ブクマ数: ").append(Integer.toString(count)).append("\r\n");
			sb.append("画像URL: ").append(screenshot);
			return sb.toString();
		}
	}
	
	
	
	public static void main(String[] args) {
		HatebuInfoDto dto = getHatebuInfoLite("http://www.rakuten.co.jp/");
		System.out.println(dto);
	}
	
	
}
