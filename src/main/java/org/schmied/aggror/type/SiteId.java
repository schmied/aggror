package org.schmied.aggror.type;

public class SiteId extends IntBase {

	private SiteId(final int value) {
		super(value);
	}

	public static final SiteId valueOf(final Integer i) {
		if (i == null)
			return null;
		return new SiteId(i.intValue());
	}
}
