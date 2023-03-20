package org.schmied.aggror.type;

import java.nio.file.Path;

import org.schmied.aggror.Aggror;

public class Q extends LongBase {

	private Q(final long value) {
		super(value);
	}

	// ---

	@Override
	public String toString() {
		return Long.toString(value);
	}

	// ---

	private static final Q valueOf(final long value) {
		return new Q(value);
	}

	public static final Q valueOf(final String q) {
		return q == null ? null : valueOf(Long.parseLong(q));
	}

	// ---

	private Path path;

	public Path path(final Aggror app) {
		if (path == null) {
//			//final String dirName1 = Long.toHexString(((value & 0xffff000000000000L) >> 40) & 0x000000000000ffffL);
//			String dirName2 = Long.toHexString(((value & 0x0000ffff00000000L) >> 32) & 0x000000000000ffffL);
//			if (dirName2.length() != 2)
//				dirName2 = IntBase.pad(dirName2, 2);
//			String dirName3 = Long.toHexString(((value & 0x00000000ffff0000L) >> 16) & 0x000000000000ffffL);
//			if (dirName3.length() != 4)
//				dirName3 = IntBase.pad(dirName3, 4);
//			//String dirName4 = Long.toHexString(((value & 0x000000000000ffffL) >> 0) & 0x000000000000ffffL);
//			//if (dirName4.length() != 4)
//			//	dirName4 = IntBase.pad(dirName4, 4);
			String dirName2 = Long.toHexString(((value & 0x0000000fff000000L) >> 24) & 0x0000000000000fffL);
			if (dirName2.length() != 3)
				dirName2 = IntBase.pad(dirName2, 3);
			String dirName3 = Long.toHexString(((value & 0x0000000000fff000L) >> 12) & 0x0000000000000fffL);
			if (dirName3.length() != 3)
				dirName3 = IntBase.pad(dirName3, 3);
			final StringBuilder fileName = new StringBuilder();
			fileName.append(IntBase.pad(Long.toString(value), 11));
			path = app.dir.resolve("q").resolve(dirName2).resolve(dirName3).resolve(fileName.toString());
		}
		return path;
	}
}
