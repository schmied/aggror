package org.schmied.aggror.type;

import java.nio.file.Path;
import java.time.*;

public class Time extends IntOrderBase {

	private Time(final int value) {
		super(value);
	}

//	public static final Time valueOf(final Short s) {
//		return new Time(s.intValue());
//	}

	public Path path(final Path base) {
		final String dirName1 = Integer.toHexString((value & 0x0000ff00) >> 8);
		final String dirName2 = Integer.toHexString(value & 0x000000ff);
		return base.resolve(dirName1).resolve(dirName2);
	}

	// ---

	private static final int INTERVAL_IN_HOURS = 6;
	//private static final int INTERVAL_IN_SECONDS = 60 * 60 * INTERVAL_IN_HOURS;
	//private static final long INTERVAL_IN_MILLIS = 1000 * INTERVAL_IN_SECONDS;

	private static final ZonedDateTime EPOCH = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC.normalized());

	private static final long CACHE_INTERVAL_IN_MILLIS = 60 * 1000;
	private static long millisLast;
	private static Time nowLast;

	public static Time now() {
		if (System.currentTimeMillis() - millisLast > CACHE_INTERVAL_IN_MILLIS) {
			millisLast = System.currentTimeMillis();
			final ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC.normalized());
			nowLast = new Time((int) (Duration.between(EPOCH, zdt).toHours() / INTERVAL_IN_HOURS));
			if (nowLast.value > Short.MAX_VALUE)
				throw new RuntimeException("Time out of bounds.");
			else if (nowLast.value < Short.MIN_VALUE)
				throw new RuntimeException("Time out of bounds.");
		}
		return nowLast;
	}
}
