package org.schmied.aggror.type;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.zip.Adler32;

public class UrlPathHash extends IntBase {

	protected static final int BIT_LENGTH = 32;
	protected static final long BIT_MASK = 0xffffffffL;

	private static final Pattern PATTERN_HEAD = Pattern.compile("^.*://[^/]*/");
	private static final Pattern PATTERN_TAIL = Pattern.compile("\\?.*");

	private UrlPathHash(final int value) {
		super(value, BIT_LENGTH);
	}

	@Override
	public String toString() {
		return toString(value, BIT_MASK);
	}

	public static final UrlPathHash valueOf(final URL url) {
		if (url == null)
			return null;
		final String urlNormalized = PATTERN_TAIL.matcher(PATTERN_HEAD.matcher(url.toString().toLowerCase()).replaceAll("")).replaceAll("");
		final Adler32 adler32 = new Adler32();
		adler32.update(urlNormalized.getBytes(StandardCharsets.UTF_8));
		return new UrlPathHash((int) adler32.getValue());
	}

	// ---

//	private static short fletcher16Orig(final byte[] data) {
//		short sum1 = 0;
//		short sum2 = 0;
//		final short modulus = 255;
//
//		for (int i = 0; i < data.length; i++) {
//			sum1 = (short) ((sum1 + data[i]) % modulus);
//			sum2 = (short) ((sum2 + sum1) % modulus);
//		}
//		return (short) ((sum2 << 8) | sum1);
//	}

//	private static short fletcher16OptOrig(final byte[] data) {
//		int length = data.length;
//		short sum1 = 0xff;
//		short sum2 = 0xff;
//		int i = 0;
//		while (length > 0) {
//			int tlen = (length > 20) ? 20 : length;
//			length -= tlen;
//			do {
//				sum2 += sum1 += data[i];
//				i++;
//			} while ((--tlen) > 0);
//			sum1 = (short) ((sum1 & 0xff) + (sum1 >> 8));
//			sum2 = (short) ((sum2 & 0xff) + (sum2 >> 8));
//		}
//		sum1 = (short) ((sum1 & 0xff) + (sum1 >> 8));
//		sum2 = (short) ((sum2 & 0xff) + (sum2 >> 8));
//		return (short) (sum2 << 8 | sum1);
//	}
}
