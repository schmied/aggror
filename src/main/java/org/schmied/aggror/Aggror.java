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

		ServerHandler.startServer();
	}

	public static Aggror app() {
		return App.app();
	}

	@Override
	public void initDb() throws Exception {
		db.execute("CREATE TABLE IF NOT EXISTS article (time SMALLINT NOT NULL, site SMALLINT NOT NULL, url_hash SMALLINT NOT NULL, heading TEXT)");
		db.execute("CREATE INDEX IF NOT EXISTS time_idx ON article USING btree (time)");
		db.execute("CREATE INDEX IF NOT EXISTS site_idx ON article USING btree (site)");
		db.execute("CREATE INDEX IF NOT EXISTS url_hash_idx ON article USING btree (url_hash)");
	}
}
