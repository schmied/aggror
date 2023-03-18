package org.schmied.aggror.type;

public abstract class IntOrderBase extends IntBase implements Comparable<IntOrderBase> {

	protected IntOrderBase(final int value, final int bits) {
		super(value, bits);
	}

	@Override
	public int compareTo(final IntOrderBase o) {
		if (o == null)
			return -1;
		return value - o.value;
	}
}
