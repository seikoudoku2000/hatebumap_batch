package sample.rome;

import java.net.URL;
import java.util.List;

import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class RomeSample2 {
	// フィードのURL
	private static final String[] FEED_URLS = {
		"http://journal.mycom.co.jp/haishin/rss/enterprise.rdf",
		"http://d.hatena.ne.jp/Syunpei/rss",
	};

	public static void main(String[] args) throws Exception {

		// HTTPを元にフィードを取得するクラス「FeedFetcher」 ………(1)
		FeedFetcher fetcher = new HttpURLFeedFetcher();

		// フィードの内容、フィードに含まれる記事エントリの内容を出力する
		for (String url : FEED_URLS) {
			// フィードの取得 ………(2)
			SyndFeed feed = fetcher.retrieveFeed(new URL(url));

			System.out.format("フィードタイトル:[%s] 著者:[%s]\n",
					feed.getTitle(),
					feed.getUri());

			for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
				System.out.format("\t更新時刻:[%s] URL:[%s] 記事タイトル:[%s]\n",
						entry.getPublishedDate(),
						entry.getLink(),
						entry.getTitle());
			}
		}
	}
}
