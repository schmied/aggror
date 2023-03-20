package org.schmied.aggror;

import java.net.URL;
import java.nio.file.Path;

import org.schmied.aggror.type.*;
import org.slf4j.*;

public class Article {

	private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	private String heading;

	public final ArticlePk pk;
	public final Location location;
	public final Time time;

	private Article(final Sites sites, final ArticlePk pk) {
		this.pk = pk;
		this.time = Time.valueOf(this.pk);
		this.location = Location.valueOf(sites, this.pk);
		if (this.time == null || this.location == null)
			throw new RuntimeException("Time and location must not be null.");
		System.out.println(">>>>>>>> ARTICLE " + toString());
	}

	private Article(final Time time, final Location location) {
		this.time = time;
		this.location = location;
		this.pk = ArticlePk.valueOf(this.time, this.location);
		if (this.pk == null)
			throw new RuntimeException("PK must not be null.");
		System.out.println(">>>>>>>> ARTICLE " + toString());
	}

	// ---

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append(pk.toString());
		sb.append(' ');
		sb.append(time.toString());
		sb.append(' ');
		sb.append(location.toString());
		if (heading != null) {
			sb.append(' ');
			sb.append(heading);
		}
		return sb.toString();
	}

	// ---

	public static final Article valueOf(final URL url) {
		final Location location = Location.valueOf(url);
		return location == null ? null : new Article(Time.now(), location);
	}

	public static final Article valueOfPk(final Sites sites, final String articlePk) {
		final ArticlePk apk = ArticlePk.valueOf(articlePk);
		if (apk == null || apk.sitePk(sites) == null)
			return null;
		return new Article(sites, apk);
	}

	public static final Article valueOfUrl(final String url) {
		try {
			return url == null ? null : valueOf(new URL(url));
		} catch (final Exception e) {
			LOGGER.info("Cannot parse URL '{}'.", url);
			return null;
		}
	}

	// ---

	private Path path;

	public Path path(final Aggror app, final String suffix) {
		if (path == null) {
			final StringBuilder fileName = new StringBuilder(32);
			fileName.append(location.site.name);
			fileName.append('_');
			fileName.append(location.urlPathHash.toString());
			fileName.append('.');
			fileName.append(suffix);
			path = time.path(app.dir.resolve("a")).resolve(fileName.toString());
		}
		return path;
	}
}
