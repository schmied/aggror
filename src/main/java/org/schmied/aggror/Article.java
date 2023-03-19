package org.schmied.aggror;

import java.net.URL;
import java.nio.file.Path;

import org.schmied.aggror.type.*;
import org.schmied.app.*;
import org.slf4j.*;

public class Article {

	private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	private String heading;

	public final ArticlePk pk;
	public final Location location;
	public final Time time;

	private Path path;

	private Article(final ArticlePk pk) {
		this.pk = pk;
		this.time = this.pk.time();
		this.location = Location.valueOf(this.pk);
	}

	private Article(final Time time, final Location location) {
		this.time = time;
		this.location = location;
		this.pk = ArticlePk.valueOf(this.time, this.location);
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

	public static Article valueOfPk(final String pk) {
		final ArticlePk apk = ArticlePk.valueOf(pk);
		return pk == null ? null : new Article(apk);

	}

	public static final Article valueOf(final URL url) {
		final Location location = Location.valueOf(url);
		return location == null ? null : new Article(Time.now(), location);
	}

	public static Article valueOfUrl(final String url) {
		try {
			return valueOf(new URL(url));
		} catch (final Exception e) {
			Log.warn(LOGGER, e);
			return null;
		}
	}

	// ---

	public Path path(final String suffix) {
		if (path == null) {
			final Aggror app = App.app();
			final StringBuilder fileName = new StringBuilder(32);
			fileName.append(location.site.toString());
			fileName.append('_');
			fileName.append(location.urlPathHash.toString());
			fileName.append('.');
			fileName.append(suffix);
			path = time.path(app.dir.resolve("a")).resolve(fileName.toString());
		}
		return path;
	}
}
