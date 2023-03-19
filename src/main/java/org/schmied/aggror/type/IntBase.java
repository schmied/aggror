package org.schmied.aggror.type;

public abstract class IntBase {

	public final int value;

	protected IntBase(final int value, final int bitLength) {
		this.value = value;
		if (bitLength < 32 && this.value >> bitLength > 0)
			throw new RuntimeException("Value out of bounds for bit length " + bitLength + ": " + value + " / 0x" + Integer.toHexString(value));
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

	protected static final String toString(final int value, final long bitMask) {
		return Long.toHexString(value & bitMask);
	}
}
