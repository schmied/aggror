package org.schmied.aggror.type;

import java.nio.file.Path;
import java.time.*;

public class Time extends IntOrderBase {

	protected static final int BIT_LENGTH = 20;
	protected static final long BIT_MASK = 0xfffffL;

	private Time(final int value) {
		super(value, BIT_LENGTH);
	}

	// ---

	@Override
	public String toString() {
		return toString(value, 5, BIT_MASK);
	}

	// ---

	private static final Time valueOf(final int value) {
		return new Time(value);
	}

	public static final Time valueOf(final ArticlePk articlePk) {
		return articlePk == null ? null : valueOf((int) ((articlePk.value >> (SitePk.BIT_LENGTH + UrlPathHash.BIT_LENGTH)) & BIT_MASK));
	}

	// ---

	public Path path(final Path base) {
		final String dirName1 = Integer.toHexString((value & 0x000f0000) >> 16);
		String dirName2 = Integer.toHexString((value & 0x0000ff00) >> 8);
		if (dirName2.length() == 1)
			dirName2 = "0" + dirName2;
		String dirName3 = Integer.toHexString(value & 0x000000ff);
		if (dirName3.length() == 1)
			dirName3 = "0" + dirName3;
		return base.resolve(dirName1).resolve(dirName2).resolve(dirName3);
	}

	// ---

	private static final int INTERVAL_IN_HOURS = 6;
	//private static final int INTERVAL_IN_SECONDS = 60 * 60 * INTERVAL_IN_HOURS;
	//private static final long INTERVAL_IN_MILLIS = 1000 * INTERVAL_IN_SECONDS;

	private static final ZonedDateTime EPOCH = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC.normalized());

	private static final long CACHE_INTERVAL_IN_MILLIS = 60 * 1000;
	private static long millisLast;
	private static Time nowLast;

	public static Time now() {
		if (System.currentTimeMillis() - millisLast > CACHE_INTERVAL_IN_MILLIS) {
			millisLast = System.currentTimeMillis();
			final ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC.normalized());
			nowLast = new Time((int) (Duration.between(EPOCH, zdt).toHours() / INTERVAL_IN_HOURS));
		}
		return nowLast;
	}
}
