package org.schmied.aggror.type;

public abstract class IntBase {

	public final int value;

	protected IntBase(final int value, final int bitLength) {
		this.value = value;
		checkBits(value, bitLength);
	}

	// ---

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof IntBase))
			return false;
		return value == ((IntBase) o).value;
	}

	// ---

	protected static String pad(final String value, final int length) {
		if (value.length() == length)
			return value;
		final int leadingZeroCount = length - value.length();
		final StringBuilder sb = new StringBuilder(length);
		while (sb.length() < leadingZeroCount)
			sb.append('0');
		sb.append(value);
		return sb.toString();
	}

	protected static String toString(final int value, final int hexLength, final long bitMask) {
		final String s = Long.toHexString(value & bitMask);
		if (s.length() == hexLength)
			return s;
		return pad(s, hexLength);
	}

	public static final void checkBits(final int value, final int bitLength) {
		if (bitLength < 32 && value >> bitLength > 0)
			throw new RuntimeException("Value out of bounds for bit length " + bitLength + ": " + value + " / 0x" + Integer.toHexString(value));
	}
}
