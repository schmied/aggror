package org.schmied.aggror;

import java.net.URL;
import java.nio.file.Path;

import org.schmied.aggror.type.*;
import org.schmied.app.App;

public class Article {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	public final Time time;
	public final SiteId siteId;
	public final UrlPathHash urlPathHash;
	public final Long key;
	public final String heading;

	private Path path;

	private Article(final Time time, final SiteId siteId, final UrlPathHash urlPathHash, final String heading) {
		this.time = time;
		this.siteId = siteId;
		this.urlPathHash = urlPathHash;
		this.heading = heading;
		this.key = key(this.time, this.siteId, this.urlPathHash);
		System.out.println(">>>>>>>> ARTICLE " + toString() + " " + Long.toHexString(key.longValue()));
	}

	public Article(final Site site, final URL url, final String heading) {
		this(Time.now(), site.id, UrlPathHash.valueOf(url), heading);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append(time.toHexString());
		sb.append(' ');
		sb.append(siteId.toString());
		sb.append(' ');
		sb.append(urlPathHash.toHexString());
		sb.append(' ');
		sb.append(heading);
		return sb.toString();
	}

	public static Long key(final Time time, final SiteId siteId, final UrlPathHash urlPathHash) {
		return Long.valueOf((time.value << 16) | siteId.value);
	}

	// ---

	public Path path(final String suffix) {
		if (path == null) {
			final Aggror app = App.app();
			final StringBuilder fileName = new StringBuilder(32);
			fileName.append(app.site(siteId).toString());
			fileName.append('_');
			fileName.append(urlPathHash.toString());
			fileName.append('.');
			fileName.append(suffix);
			path = time.path(app.dir.resolve("a")).resolve(fileName.toString());
		}
		return path;
	}
}
