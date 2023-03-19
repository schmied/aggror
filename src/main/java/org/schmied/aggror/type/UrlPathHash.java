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

	@Override
	public String toString() {
		return toString(value, BIT_MASK);
	}

	public static final UrlPathHash valueOf(final URL url) {
		if (url == null)
			return null;
		String path = url.getPath();
		if (path.startsWith("/"))
			path = path.substring(1);
		final Adler32 adler32 = new Adler32();
		adler32.update(path.getBytes(StandardCharsets.UTF_8));
		System.out.println(">>>> " + url.toString() + " " + path);
		return new UrlPathHash((int) adler32.getValue());
	}
}
