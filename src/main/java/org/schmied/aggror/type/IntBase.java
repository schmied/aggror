package org.schmied.aggror.type;

public abstract class IntBase {

	public final int value;

	protected IntBase(final int value, final int bitLength) {
		this.value = value;
		if (this.value >> bitLength > 0)
			throw new RuntimeException("Value out of bounds: " + value);
	}

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

	protected static String toString(final int value, final long bitMask) {
		return Long.toHexString(value & bitMask);
	}
}
