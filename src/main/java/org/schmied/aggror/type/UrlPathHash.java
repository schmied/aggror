package org.schmied.aggror.type;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.Adler32;

public class UrlPathHash extends IntBase {

	protected static final int BIT_LENGTH = 32;
	protected static final long BIT_MASK = 0xffffffffL;

	private UrlPathHash(final int value) {
		super(value, BIT_LENGTH);
	}

	// ---

	@Override
	public String toString() {
		return toString(value, 8, BIT_MASK);
	}

	// ---

	private static final UrlPathHash valueOf(final int value) {
		return new UrlPathHash(value);
	}

	public static final UrlPathHash valueOf(final ArticlePk articlePk) {
		return articlePk == null ? null : valueOf((int) (articlePk.value & BIT_MASK));
	}

	public static UrlPathHash valueOf(final URL url) {
		if (url == null)
			return null;
		String path = url.getPath();
		if (path.startsWith("/"))
			path = path.substring(1);
		final Adler32 adler32 = new Adler32();
		adler32.update(path.getBytes(StandardCharsets.UTF_8));
		return valueOf((int) adler32.getValue());
	}
}
