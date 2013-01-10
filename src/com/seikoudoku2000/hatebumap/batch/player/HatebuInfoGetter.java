package com.seikoudoku2000.hatebumap.batch.player;

import net.arnx.jsonic.JSON;

import com.seikoudoku2000.hatebumap.util.HttpUtil;


/**
 * �͂ĂԂ̃G���g���[����API(http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo)
 * �o�R�Ŏ擾���邽�߂̃N���X
 */
public class HatebuInfoGetter {
	
	/**
	 * �͂ĂԃG���g���[���擾API�̊�b��URL
	 */
	private static final String HATEBU_GET_INFO_URL = "http://b.hatena.ne.jp/entry/json/?url=";
	
	/**
	 * �͂ĂԃG���g���[���擾API���C�g�ł̊�b��URL
	 */
	private static final String HATEBU_GET_INFO_LITE_URL = "http://b.hatena.ne.jp/entry/jsonlite/?url=";
	
	
	//HTTP�ڑ����s���I�u�W�F�N�g
	private static HttpUtil httpUtil = new HttpUtil();
	
	
	/**
	 * �͂ĂԂ̃G���g���[���擾�B���C�g�ŁB
	 * @param targetUrl
	 * @return�@HatebuInfoDto
	 */
	public static HatebuInfoDto getHatebuInfoLite(String targetUrl) {
		String responseJson = httpUtil.getResponseBody(getLiteQueryUrl(targetUrl), 10000);
		
		//���ʂ�parse���ĕԋp
		if(responseJson != null) {
			HatebuInfoDto dto 
				= JSON.decode(responseJson, HatebuInfoDto.class);
			return dto;
		} else {
			return null;
		}
	}
	
	
	/**
	 * �͂ĂԂ̃G���g���[���擾�B
	 *�@##����related��Dto�ɒ�`���Ă��Ȃ��̂ňӖ��Ȃ��B
	 * @param targetUrl
	 * @return�@HatebuInfoDto
	 */
	@Deprecated
	public static HatebuInfoDto getHatebuInfo(String targetUrl) {
		String responseJson = httpUtil.getResponseBody(getQueryUrl(targetUrl), 10000);
		
		//���ʂ�parse���ĕԋp
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
	 * ���ʊi�[�p��DTO�B
	 */
	public static class HatebuInfoDto {
		
		private String title;
		private int count;
		private String url;
		private String entry_url;
		private String screenshot;
		private long eid;
		
		//bookmarks��related�͍��͓���Ă��Ȃ��B
		
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
			sb.append("�^�C�g��: ").append(title).append("\r\n");
			sb.append("�u�N�}��: ").append(Integer.toString(count)).append("\r\n");
			sb.append("�摜URL: ").append(screenshot);
			return sb.toString();
		}
	}
	
	
	
	public static void main(String[] args) {
		HatebuInfoDto dto = getHatebuInfoLite("http://www.rakuten.co.jp/");
		System.out.println(dto);
	}
	
	
}
