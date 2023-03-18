package org.schmied.aggror;

import java.net.URL;
import java.nio.file.Path;

import org.schmied.aggror.type.*;
import org.schmied.app.App;

public class Article {

	//private static final Logger LOGGER = LoggerFactory.getLogger(Article.class);

	public final Time time;
	public final Id siteId;
	public final Hash urlHash;
	public final String heading;

	private Path path;

	private Article(final Time time, final Id siteId, final Hash urlHash, final String heading) {
		this.time = time;
		this.siteId = siteId;
		this.urlHash = urlHash;
		this.heading = heading;
		System.out.println(">>>>>>>> ARTICLE " + toString());
	}

	public Article(final Site site, final URL url, final String heading) {
		this(Time.now(), site.id, Hash.valueOf(site, url), heading);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append(time);
		sb.append(' ');
		sb.append(siteId);
		sb.append(' ');
		sb.append(urlHash);
		sb.append(' ');
		sb.append(heading);
		return sb.toString();
	}

	// ---

	public Path path(final String suffix) {
		if (path == null) {
			final Aggror app = App.app();
			final StringBuilder fileName = new StringBuilder(32);
			fileName.append(app.site(siteId).filenamePart());
			fileName.append('_');
			fileName.append(urlHash.filenamePart());
			fileName.append('.');
			fileName.append(suffix);
			path = time.path(app.dir.resolve("a")).resolve(fileName.toString());
		}
		return path;
	}
}
