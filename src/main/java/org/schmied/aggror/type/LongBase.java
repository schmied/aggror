package org.schmied.aggror.type;

public abstract class LongBase {

	private static final int HEX_LENGTH = 16;

	public final long value;

	public LongBase(final long value) {
		this.value = value;
	}

	// ---

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof LongBase))
			return false;
		return value == ((LongBase) o).value;
	}

	@Override
	public String toString() {
		final String s = Long.toHexString(value);
		if (s.length() == HEX_LENGTH)
			return s;
		return IntBase.hexPad(s, HEX_LENGTH);
	}
}
