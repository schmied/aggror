package org.schmied.aggror;

import java.util.*;

import org.schmied.app.App;

public class Aggror extends App {

	public static void main(final String[] args) {
		try {
			final Aggror app = new Aggror();
		} catch (final Exception e) {
			exit(1, e);
		}
	}

	// ---

//	public static final float GOLDEN_RATIO_0 = 0.618033988749f;
//	public static final float GOLDEN_RATIO_1 = 1.618033988749f;
//
//	private static final int HOURS_PER_INTERVAL = 6;
//	private static final int SECONDS_PER_INTERVAL = 60 * 60 * HOURS_PER_INTERVAL;

	public final SortedMap<String, Integer> tagPriorities;
	public final SortedSet<Site> sites;

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

		sites = Site.sites(prop.getMapOfString("site"));
		for (final Site site : sites)
			log.info("{}", site.toString());

		startScheduler();

		ServerHandler.startServer();
	}

	public static Aggror app() {
		return App.app();
	}

	@Override
	public void initDb() throws Exception {

		// site
		db.update("CREATE TABLE IF NOT EXISTS site (site_id SMALLSERIAL PRIMARY KEY, name TEXT)");
		db.update("CREATE INDEX IF NOT EXISTS site_name_idx ON site USING btree (name)");

		// article
		db.update("CREATE TABLE IF NOT EXISTS article (time SMALLINT NOT NULL, site_id SMALLINT NOT NULL, url_hash SMALLINT NOT NULL, heading TEXT," //
				+ " CONSTRAINT article_site_fk FOREIGN KEY(site_id) REFERENCES site(site_id))");
		db.update("CREATE INDEX IF NOT EXISTS article_time_idx ON article USING btree (time)");
		db.update("CREATE INDEX IF NOT EXISTS article_site_id_idx ON article USING btree (site_id)");
		db.update("CREATE INDEX IF NOT EXISTS article_url_hash_idx ON article USING btree (url_hash)");
	}
}
