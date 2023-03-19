package org.schmied.aggror.type;

import org.schmied.aggror.Aggror;
import org.schmied.app.*;

public class SitePk extends IntBase {

	protected static final int BIT_LENGTH = 12;
	protected static final long BIT_MASK = 0xfffL;

	private SitePk(final int value) {
		super(value, BIT_LENGTH);
	}

	// ---

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	// ---

	private static final SitePk valueOf(final int value) {
		return Aggror.app().sitePk(value);
	}

	public static final SitePk valueOf(final ArticlePk articlePk) {
		return articlePk == null ? null : valueOf((int) ((articlePk.value >> UrlPathHash.BIT_LENGTH) & UrlPathHash.BIT_MASK));
	}

	// ---

	public static SitePk create(final String name) throws Exception {
		final Db db = App.app().db;
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
