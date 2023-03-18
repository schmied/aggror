package org.schmied.aggror.type;

public class Id extends IntBase {

	private Id(final int value) {
		super(value);
	}

	public static final Id valueOf(final Integer i) {
		if (i == null)
			return null;
		return new Id(i.intValue());
	}
}
