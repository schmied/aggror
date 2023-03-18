package org.schmied.aggror.type;

public abstract class IntBase {

	protected final int value;

	protected IntBase(final int value) {
		this.value = value;
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

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
