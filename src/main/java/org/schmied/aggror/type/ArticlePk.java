package org.schmied.aggror.type;

import org.schmied.aggror.Site;

public class ArticlePk extends LongBase {

	private ArticlePk(final long value) {
		super(value);
	}

	// 0x tt tt ts ss uu uu uu uu
	public static ArticlePk valueOf(final Time time, final Site site, final UrlPathHash urlPathHash) {
		final long valueTime = (time.value & Time.BIT_MASK) << (SitePk.BIT_LENGTH + UrlPathHash.BIT_LENGTH);
		final long valueSite = (site.pk.value & SitePk.BIT_MASK) << UrlPathHash.BIT_LENGTH;
		final long valueHash = (urlPathHash.value & UrlPathHash.BIT_MASK);
		return new ArticlePk(valueTime | valueSite | valueHash);
	}
}
