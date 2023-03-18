package org.schmied.aggror;

import java.net.URL;
import java.nio.file.Path;

import org.schmied.aggror.type.*;
import org.schmied.app.App;

public class Article {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	public final ArticlePk pk;
	public final Time time;
	public final Site site;
	public final UrlPathHash urlPathHash;
	public final String heading;

	private Path path;

	private Article(final Time time, final Site site, final UrlPathHash urlPathHash, final String heading) {
		this.time = time;
		this.site = site;
		this.urlPathHash = urlPathHash;
		this.pk = ArticlePk.valueOf(time, site, urlPathHash);
		this.heading = heading;
		System.out.println(">>>>>>>> ARTICLE " + toString());
	}

	public Article(final Site site, final URL url, final String heading) {
		this(Time.now(), site, UrlPathHash.valueOf(url), heading);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append(pk.toString());
		sb.append(' ');
		sb.append(site.toString());
		sb.append(' ');
		sb.append(heading);
		return sb.toString();
	}

	// ---

	public Path path(final String suffix) {
		if (path == null) {
			final Aggror app = App.app();
			final StringBuilder fileName = new StringBuilder(32);
			fileName.append(site.toString());
			fileName.append('_');
			fileName.append(urlPathHash.toString());
			fileName.append('.');
			fileName.append(suffix);
			path = time.path(app.dir.resolve("a")).resolve(fileName.toString());
		}
		return path;
	}
}
