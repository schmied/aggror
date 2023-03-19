package org.schmied.aggror.type;

public abstract class LongBase {

	public final long value;

	public LongBase(final long value) {
		this.value = value;
	}

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
		return Long.toHexString(value);
	}
}
