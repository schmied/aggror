package org.schmied.app;

public class V {

	public static final Integer getInteger(final Object o, final Integer defaultValue) {
		try {
			return Integer.valueOf(o.toString());
		} catch (final Exception e) {
			return defaultValue;
		}
	}

	public static final Integer getInteger(final Object o) {
		return getInteger(o, null);
	}

	public static final int getInt(final Object o, final int defaultValue) {
		if (o == null)
			return defaultValue;
		final Integer i = getInteger(o, null);
		if (i != null)
			return i.intValue();
		return defaultValue;
	}
}
