package org.schmied.aggror;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.*;

import org.schmied.app.App;

public class Article {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	public final Short time, siteId, urlHash;
	public final String heading;

	private Path path;

	private Article(final Short time, final Short siteId, final Short urlHash, final String heading) {
		this.time = time;
		this.siteId = siteId;
		this.urlHash = urlHash;
		this.heading = heading;
		System.out.println(">>>>>>>> ARTICLE " + toString());
	}

	public Article(final Site site, final URL url, final String heading) {
		this(time(), site.id, urlHash(site, url), heading);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append(time);
		sb.append(' ');
		sb.append(siteId);
		sb.append(' ');
		sb.append(urlHash);
		sb.append(' ');
		sb.append(heading);
		return sb.toString();
	}

	// ---

	private static final ZonedDateTime EPOCH = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC.normalized());

	private static final long CACHE_INTERVAL_IN_MILLIS = 60 * 1000;
	private static long millisLast;
	private static Short timeLast;

	public static Short time() {
		if (System.currentTimeMillis() - millisLast > CACHE_INTERVAL_IN_MILLIS) {
			millisLast = System.currentTimeMillis();
			final ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC.normalized());
			timeLast = Short.valueOf((short) (Duration.between(EPOCH, zdt).toHours() / 3));
		}
		return timeLast;
	}

	// ---

	private static short fletcher16Orig(final byte[] data) {
		short sum1 = 0;
		short sum2 = 0;
		final short modulus = 255;

		for (int i = 0; i < data.length; i++) {
			sum1 = (short) ((sum1 + data[i]) % modulus);
			sum2 = (short) ((sum2 + sum1) % modulus);
		}
		return (short) ((sum2 << 8) | sum1);
	}

	private static short fletcher16OptOrig(final byte[] data) {
		int length = data.length;
		short sum1 = 0xff;
		short sum2 = 0xff;
		int i = 0;

		while (length > 0) {
			int tlen = (length > 20) ? 20 : length;
			length -= tlen;
			do {
				sum2 += sum1 += data[i];
				i++;
			} while ((--tlen) > 0);
			sum1 = (short) ((sum1 & 0xff) + (sum1 >> 8));
			sum2 = (short) ((sum2 & 0xff) + (sum2 >> 8));
		}
		sum1 = (short) ((sum1 & 0xff) + (sum1 >> 8));
		sum2 = (short) ((sum2 & 0xff) + (sum2 >> 8));
		return (short) (sum2 << 8 | sum1);
	}

	public static Short urlHash(final Site site, final URL url) {
		final String urlNormalized = url.toString().toLowerCase().replaceAll("^.*://.*" + site.name + "/", "").replaceAll("", "");
		System.out.println(">>>>>> " + url + " -> " + urlNormalized);
		return Short.valueOf(fletcher16OptOrig(urlNormalized.getBytes(StandardCharsets.UTF_8)));
	}

	// ---

	public Path path() {
		if (path == null) {
			final Aggror app = App.app();
			final int i = time.intValue();
			final String dirName1 = Integer.toHexString((i & 0x0000ff00) >> 8);
			final String dirName2 = Integer.toHexString(i & 0x000000ff);
			final String fileName = app.site(siteId).nameNormalized() + "_" + Integer.toHexString(urlHash.intValue() & 0x0000ffff);
			path = App.app().dir.resolve("a").resolve(dirName1).resolve(dirName2).resolve(fileName);
			System.out.println(">>>>>11>>>>> " + i + " - " + path.toAbsolutePath().toString());
		}
		return path;
	}
}
