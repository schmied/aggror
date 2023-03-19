package org.schmied.aggror.type;

import org.schmied.aggror.*;
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

	public static ArticlePk valueOf(final String pk) {
		try {
			return pk == null ? null : valueOf(Long.parseLong(pk, 16));
		} catch (final Exception e) {
			LOGGER.info("Cannot parse '{}' to long.", pk);
			return null;
		}
	}

	// 0x tt tt ts ss uu uu uu uu
	public static ArticlePk valueOf(final Time time, final Location location) {
		if (time == null || location == null)
			return null;
		final long valueTime = (time.value & Time.BIT_MASK) << (SitePk.BIT_LENGTH + UrlPathHash.BIT_LENGTH);
		final long valueSite = (location.site.pk.value & SitePk.BIT_MASK) << UrlPathHash.BIT_LENGTH;
		final long valueHash = location.urlPathHash.value & UrlPathHash.BIT_MASK;
		return valueOf(valueTime | valueSite | valueHash);
	}

	// ---

	public final Time time() {
		return Time.valueOf(this);
	}

	public final Site site() {
		return Aggror.app().site(SitePk.valueOf(this));
	}

	public final UrlPathHash urlPathHash() {
		return UrlPathHash.valueOf(this);
	}
}
