package org.schmied.aggror.type;

import java.net.URL;

import org.schmied.aggror.*;
import org.slf4j.*;

public class Location {

	private static final Logger LOGGER = LoggerFactory.getLogger(Location.class);

	public final Site site;
	public final UrlPathHash urlPathHash;

	private Location(final Site site, final UrlPathHash urlPathHash) {
		this.site = site;
		this.urlPathHash = urlPathHash;
	}

	@Override
	public int hashCode() {
		return site.hashCode() ^ urlPathHash.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Location))
			return false;
		return hashCode() == ((Location) o).hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(64);
		sb.append(site.toString());
		sb.append(' ');
		sb.append(urlPathHash.toString());
		return sb.toString();
	}

	public static Location valueOf(final URL url) {
		final String host = url.getHost();
		final Aggror app = Aggror.app();
		Site site = app.site(host);
		if (site == null) {
			final int idx = host.indexOf('.');
			if (idx > 0)
				site = app.site(host.substring(idx + 1));
		}
		if (site == null) {
			LOGGER.info("No site found for host '" + host + "'.");
			return null;
		}
		Integer.valueOf(0);
		final UrlPathHash urlPathHash = UrlPathHash.valueOf(url);
		return new Location(site, urlPathHash);
	}
}
