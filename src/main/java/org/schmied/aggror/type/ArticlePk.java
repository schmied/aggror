package org.schmied.aggror.type;

import java.math.BigInteger;

import org.schmied.aggror.Sites;
import org.slf4j.*;

public class ArticlePk extends LongBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticlePk.class);

	private ArticlePk(final long value) {
		super(value);
	}

	// ---

	private static final ArticlePk valueOf(final long value) {
		return new ArticlePk(value);
	}

	public static ArticlePk valueOf(final String articlePk) {
		try {
			return articlePk == null ? null : valueOf(new BigInteger(articlePk, 16).longValue());
		} catch (final Exception e) {
			LOGGER.info("Cannot parse '{}' to long: {}", articlePk, e.getMessage());
			return null;
		}
	}

	// 0x tt tt ts ss uu uu uu uu
	public static ArticlePk valueOf(final Time time, final Location location) {
		if (time == null || location == null)
			return null;
		final long valueTime = (time.value & Time.BIT_MASK) << (SitePk.BIT_LENGTH + UrlPathHash.BIT_LENGTH);
		final long valueSitePk = (location.site.pk.value & SitePk.BIT_MASK) << UrlPathHash.BIT_LENGTH;
		final long valueUrlPathHash = location.urlPathHash.value & UrlPathHash.BIT_MASK;
		return valueOf(valueTime | valueSitePk | valueUrlPathHash);
	}

	// ---

	public final Time time() {
		return Time.valueOf(this);
	}

	public final SitePk sitePk(final Sites sites) {
		return SitePk.valueOf(sites, this);
	}

//	public final Site site() {
//		return Aggror.app().sites.site(sitePk());
//	}

	public final UrlPathHash urlPathHash() {
		return UrlPathHash.valueOf(this);
	}
}
