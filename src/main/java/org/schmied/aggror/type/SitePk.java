package org.schmied.aggror.type;

public class SitePk extends IntBase {

	protected static final int BIT_LENGTH = 12;
	protected static final long BIT_MASK = 0xfffL;

	private SitePk(final int value) {
		super(value, BIT_LENGTH);
	}

	public static SitePk valueOf(final Integer value) {
		return value == null ? null : new SitePk(value.intValue());
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
