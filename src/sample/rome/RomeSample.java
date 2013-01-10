package sample.rome;

import java.net.URL;
import java.util.List;

import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class RomeSample {
	public static void main(String[] args) {
		FeedFetcher fetcher = new HttpURLFeedFetcher();

		try {
			URL url = new URL("http://feeds.feedburner.com/hatena/b/hotentry");
			SyndFeed feed = fetcher.retrieveFeed(url);

			for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
				//System.out.println(entry.getTitle());
				System.out.println(entry.getUri());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
