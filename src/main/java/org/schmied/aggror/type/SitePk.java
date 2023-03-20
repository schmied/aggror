package org.schmied.aggror.type;

import org.schmied.aggror.*;
import org.schmied.app.Db;

public class SitePk extends IntBase {

	public static final int BIT_LENGTH = 12;
	protected static final long BIT_MASK = 0xfffL;

	private SitePk(final int value) {
		super(value, BIT_LENGTH);
	}

	// ---

	@Override
	public String toString() {
		return toString(value, 3, BIT_MASK);
	}

//	@Override
//	public String toString() {
//		return Integer.toString(value);
//	}

	// ---

	private static final SitePk valueOf(final Sites sites, final int value) {
//		final Aggror app = Aggror.app();
//		if (app != null)
//			return app.sites.sitePk(value);
		if (sites != null)
			return sites.sitePk(value);
		return new SitePk(value);
	}

	public static final SitePk valueOf(final Sites sites, final ArticlePk articlePk) {
		return articlePk == null ? null : valueOf(sites, (int) ((articlePk.value >> UrlPathHash.BIT_LENGTH) & BIT_MASK));
	}

	// ---

	public static SitePk create(final Aggror app, final String name) throws Exception {
		final Db db = app.db;
		final String query = "SELECT site_id FROM site WHERE name = '" + name + "'";
		Integer pk = db.queryObject(query, Integer.class);
		if (pk == null) {
			db.update("INSERT INTO site (name) VALUES ('" + name + "')");
			pk = db.queryObject(query, Integer.class);
		}
		if (pk == null)
			throw new Exception("Cannot insert site.");
		return new SitePk(pk.intValue());
	}
}
