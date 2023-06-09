package org.schmied.aggror;

import java.net.URL;
import java.util.*;

import org.schmied.app.App;

public class Aggror extends App {

	public static void main(final String[] args) {
		try {
			final Aggror app = new Aggror();
			final Article a = Article.valueOf(new URL(
					"https://apnews.com/article/us-russia-china-ukraine-icc-putin-57774b3a58d6ec1c75c921f71d9ebe90?utm_source=homepage&utm_medium=TopNews&utm_campaign=position_01"));
			System.out.println(">>>>>>>>>> " + a.path(app, "png"));
		} catch (final Exception e) {
			exit(1, e);
		}
	}

	// ---

//	public static final float GOLDEN_RATIO_0 = 0.618033988749f;
//	public static final float GOLDEN_RATIO_1 = 1.618033988749f;

	public final Sites sites;
	public final Articles articles;

	public final SortedMap<String, Integer> tagPriorities;

	public Aggror() throws Exception {
		tagPriorities = new TreeMap<>();
		final String tpVal = prop.get("tagPriority");
		final String[] tps = tpVal == null ? new String[0] : tpVal.split(">");
		int idx = 0;
		for (final String tp : tps) {
			final String[] tags = tp.split(" ");
			for (final String s : tags) {
				final String tag = s.trim();
				if (!tag.isEmpty())
					tagPriorities.put(tag, Integer.valueOf(idx));
			}
			idx++;
		}
		log.info("{}", tagPriorities);

		sites = new Sites(this);
		articles = new Articles();

		/*
		siteNames = new TreeMap<>();
		siteIds = new TreeMap<>();
		try (final ResultSet rs = db.query("SELECT site_id, name FROM site")) {
			while (rs.next()) {
				final Short siteId = Short.valueOf(rs.getShort(1));
				final String siteName = rs.getString(2);
				siteNames.put(siteId, siteName);
				siteIds.put(siteName, siteId);
			}
		} catch (final Exception e) {
			throw e;
		}
		*/

		startScheduler();

		ServerHandler.startServer(this);
	}

	public static Aggror app() {
		return App.app();
	}

	// ---

//	public Sites sites() {
//		return sites;
//	}

//	public SortedSet<Site> sites() {
//		return sites;
//	}
//
//	public Site site(final SitePk sitePk) {
//		return sitesByPk.get(sitePk);
//	}
//
//	public Site site(final String siteName) {
//		return sitesByName.get(siteName);
//	}
//
//	public SitePk sitePk(final int sitePk) {
//		IntBase.checkBits(sitePk, SitePk.BIT_LENGTH);
//		return sitePks[sitePk];
//	}

	public Article article(final URL url) {
		return Article.valueOf(url);
	}

	// ---

	@Override
	public void initDb() throws Exception {

		// site
		db.update("CREATE TABLE IF NOT EXISTS q (q_id BIGINT PRIMARY KEY, last_fetch INT NOT NULL)");
		//db.update("CREATE INDEX IF NOT EXISTS site_name_idx ON site USING btree (name)");

		// site
		db.update("CREATE TABLE IF NOT EXISTS site (site_id SMALLSERIAL PRIMARY KEY, name TEXT)");
		db.update("CREATE INDEX IF NOT EXISTS site_name_idx ON site USING btree (name)");

		// article
		db.update(
				"CREATE TABLE IF NOT EXISTS article (article_id BIGINT PRIMARY KEY, url_hash INT NOT NULL, site_id SMALLINT NOT NULL, rating SMALLINT NOT NULL, heading TEXT," //
						+ " CONSTRAINT article_site_fk FOREIGN KEY(site_id) REFERENCES site(site_id))");
		db.update("CREATE INDEX IF NOT EXISTS article_url_hash_idx ON article USING btree (url_hash)");
		db.update("CREATE INDEX IF NOT EXISTS article_site_id_idx ON article USING btree (site_id)");
	}
}
