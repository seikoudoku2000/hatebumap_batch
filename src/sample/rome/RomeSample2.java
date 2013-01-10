package sample.rome;

import java.net.URL;
import java.util.List;

import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class RomeSample2 {
	// �t�B�[�h��URL
	private static final String[] FEED_URLS = {
		"http://journal.mycom.co.jp/haishin/rss/enterprise.rdf",
		"http://d.hatena.ne.jp/Syunpei/rss",
	};

	public static void main(String[] args) throws Exception {

		// HTTP�����Ƀt�B�[�h���擾����N���X�uFeedFetcher�v �c�c�c(1)
		FeedFetcher fetcher = new HttpURLFeedFetcher();

		// �t�B�[�h�̓��e�A�t�B�[�h�Ɋ܂܂��L���G���g���̓��e���o�͂���
		for (String url : FEED_URLS) {
			// �t�B�[�h�̎擾 �c�c�c(2)
			SyndFeed feed = fetcher.retrieveFeed(new URL(url));

			System.out.format("�t�B�[�h�^�C�g��:[%s] ����:[%s]\n",
					feed.getTitle(),
					feed.getUri());

			for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
				System.out.format("\t�X�V����:[%s] URL:[%s] �L���^�C�g��:[%s]\n",
						entry.getPublishedDate(),
						entry.getLink(),
						entry.getTitle());
			}
		}
	}
}
