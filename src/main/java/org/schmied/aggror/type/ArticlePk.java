package org.schmied.aggror.type;

public class ArticlePk extends LongBase {

	private ArticlePk(final long value) {
		super(value);
	}

	// 0x tt tt ts ss uu uu uu uu
	public static ArticlePk valueOf(final Time time, final Location location) {
		final long valueTime = (time.value & Time.BIT_MASK) << (SitePk.BIT_LENGTH + UrlPathHash.BIT_LENGTH);
		final long valueSite = (location.site.pk.value & SitePk.BIT_MASK) << UrlPathHash.BIT_LENGTH;
		final long valueHash = (location.urlPathHash.value & UrlPathHash.BIT_MASK);
		return new ArticlePk(valueTime | valueSite | valueHash);
	}
}
