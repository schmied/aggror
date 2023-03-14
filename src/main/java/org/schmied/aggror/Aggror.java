package org.schmied.aggror;

import java.net.URL;
import java.util.*;

import org.schmied.app.App;

public class Aggror extends App {

	public static void main(final String[] args) {
		try {
			final Aggror app = new Aggror();
			final Site s = app.sites().first();
			final Article a = new Article(s, new URL(
					"https://www.spiegel.de/kultur/david-grossman-und-999-weitere-israelische-kuenstler-fordern-absage-von-netanyahus-berlin-besuch-a-4821c5ef-f883-478d-883e-7d00f542d4ab?abdf=345345"),
					"Alle mal aufgepasst");
			System.out.println(">>>>>>>>>> " + a.path());
		} catch (final Exception e) {
			exit(1, e);
		}
	}

	// ---

//	public static final float GOLDEN_RATIO_0 = 0.618033988749f;
//	public static final float GOLDEN_RATIO_1 = 1.618033988749f;
//
	public static final int INTERVAL_IN_HOURS = 6;
	public static final int INTERVAL_IN_SECONDS = 60 * 60 * INTERVAL_IN_HOURS;
	public static final long INTERVAL_IN_MILLIS = 1000 * INTERVAL_IN_SECONDS;

	private final SortedSet<Site> sites;
	private final SortedMap<Short, Site> sitesById;
	private final SortedMap<String, Site> sitesByName;

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

		sites = Site.sites(prop.getMapOfString("site"));
		sitesById = new TreeMap<>();
		sitesByName = new TreeMap<>();
		for (final Site site : sites) {
			log.info("{}", site.toString());
			sitesById.put(site.id, site);
			sitesByName.put(site.name, site);
		}

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

		ServerHandler.startServer();
	}

	public static Aggror app() {
		return App.app();
	}

	public Site site(final Short siteId) {
		return sitesById.get(siteId);
	}

	public Site site(final String siteName) {
		return sitesByName.get(siteName);
	}

	public SortedSet<Site> sites() {
		return sites;
	}

	/*
	public String siteName(final Short siteId) {
		return siteNames.get(siteId);
	}
	
	public Short siteId(final String siteName) {
		return siteIds.get(siteName);
	}
	*/

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
