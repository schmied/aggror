package org.schmied.aggror.type;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.schmied.aggror.Site;

public class Hash extends IntBase {

	private Hash(final int value) {
		super(value);
	}

	public static final Hash valueOf(final Site site, final URL url) {
		final String urlNormalized = url.toString().toLowerCase().replaceAll("^.*://.*" + site.name + "/", "").replaceAll("\\?.*", "");
		System.out.println(">>>>>> " + url + " -> " + urlNormalized);
		return new Hash(fletcher16OptOrig(urlNormalized.getBytes(StandardCharsets.UTF_8)));
	}

	public String filenamePart() {
		return Integer.toHexString(value & 0x0000ffff);
	}

//	public static Hash urlHash(final Site site, final URL url) {
//		final String urlNormalized = url.toString().toLowerCase().replaceAll("^.*://.*" + site.name + "/", "").replaceAll("\\?.*", "");
//		System.out.println(">>>>>> " + url + " -> " + urlNormalized);
//		return valueOf(fletcher16OptOrig(urlNormalized.getBytes(StandardCharsets.UTF_8)));
//	}

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
}
