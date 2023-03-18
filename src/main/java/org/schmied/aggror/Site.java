package org.schmied.aggror;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.schmied.aggror.type.Id;
import org.schmied.app.*;
import org.slf4j.*;

public class Site implements Comparable<Site> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Site.class);

	private static final String SUBPROP_LANG = "lang";
	private static final String SUBPROP_REGEX_MATCH = "regexMatch";
	private static final String SUBPROP_REGEX_NOMATCH = "regexNoMatch";

	private static final int MIN_ARTICLE_SIZE = 1000;

	// ---

	public final Id id;
	public final String name, language;
	public final SortedMap<String, Pattern> regexMatchs, regexNoMatchs;
	public final SortedSet<String> startPages;

	private Site(final String name, final SortedMap<String, Object> subProps) throws Exception {
		this.name = name;
		this.id = id(this.name);
		this.language = subProps.get(SUBPROP_LANG).toString().trim();
		this.startPages = new TreeSet<>();
		this.startPages.add("https://" + this.name + "/");
		this.regexMatchs = new TreeMap<>();
		final Object remsVal = subProps.get(SUBPROP_REGEX_MATCH);
		final String[] rems = remsVal == null ? new String[0] : remsVal.toString().split("\\s+");
		for (String rem : rems) {
			rem = rem.trim();
			if (rem.isEmpty())
				continue;
			try {
				this.regexMatchs.put(rem, Pattern.compile(rem));
			} catch (final Exception e) {
				LOGGER.warn(rem + ": " + e.getMessage());
				continue;
			}
			if (rem.startsWith("^") && rem.endsWith("/.*"))
				startPages.add("https://" + name + "/" + rem.replace("^", "").replace("/.*", "/"));
		}
		this.regexNoMatchs = new TreeMap<>();
		final Object renmsVal = subProps.get(SUBPROP_REGEX_NOMATCH);
		final String[] renms = renmsVal == null ? new String[0] : renmsVal.toString().split("\\s+");
		for (String renm : renms) {
			renm = renm.trim();
			if (renm.isEmpty())
				continue;
			try {
				this.regexNoMatchs.put(renm, Pattern.compile(renm));
			} catch (final Exception e) {
				LOGGER.warn(renm + ": " + e.getMessage());
			}
		}

		LOGGER.info("site:{} id:{} language:{} startPages:{} regexMath:{} regexNomatch:{}", name, id, language, startPages.toString(), regexMatchs.keySet().toString(),
				regexNoMatchs.keySet().toArray());
	}

	private static Id id(final String name) throws Exception {
		final Db db = App.app().db;
		final String query = "SELECT site_id FROM site WHERE name = '" + name + "'";
		Id id = Id.valueOf(db.queryObject(query, Integer.class));
		if (id == null) {
			db.update("INSERT INTO site (name) VALUES ('" + name + "')");
			id = Id.valueOf(db.queryObject(query, Integer.class));
		}
		if (id == null)
			throw new Exception("Cannot insert site.");
		return id;
	}

	public static SortedSet<Site> sites(final SortedMap<String, String> props) throws Exception {
		final SortedMap<String, SortedMap<String, Object>> subPropsBySite = new TreeMap<>();
		for (final String key : props.keySet()) {
			final int idx = key.lastIndexOf('.');
			final String site = key.substring(0, idx);
			SortedMap<String, Object> subProps = subPropsBySite.get(site);
			if (subProps == null) {
				subProps = new TreeMap<>();
				subPropsBySite.put(site, subProps);
			}
			final String subPropKey = key.substring(idx + 1);
			subProps.put(subPropKey, props.get(key));
		}
		final SortedSet<Site> sites = new TreeSet<>();
		for (final String site : subPropsBySite.keySet())
			sites.add(new Site(site, subPropsBySite.get(site)));
		return sites;
	}

	// ---

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o == null ? false : hashCode() == o.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(final Site o) {
		return o == null ? -1 : name.compareTo(o.name);
	}

	public String filenamePart() {
		int idx = name.lastIndexOf('.');
		final String s = name.substring(0, idx);
		idx = s.lastIndexOf('.');
		return idx < 0 ? s : s.substring(idx);
	}

	// ---

/*
	private String fileName(final URL url, final String suffix) {
		String s = url.toString().toLowerCase().replaceAll("^.*://.*" + name + "/", "").replace('/', '_').replaceAll("[^a-z_-]", "-").replaceAll("-[^-]-", "-")
				.replaceAll("-[^-]-", "-").replaceAll("-[^-]-", "-").replaceAll("[-]+", "-").replaceAll("_.?[-_]+", "").replaceAll(suffix + "$", "")
				.replaceAll("[-_]+.?$", "");
		if (s.isEmpty())
			s = "_index";
		if (s.length() > 100)
			s = s.substring(0, 100).replaceAll("-+.?$", "");
		s = s + "." + suffix;
		return s;
	}

	private Path path() {
		return App.app().dir.resolve(language).resolve(name);
	}

	private Path path(final URL url, final String dir, final String suffix) {
		return path().resolve(dir).resolve(fileName(url, suffix));
	}
*/

	// ---

	private static Node cleanUp(final Node node) {
		node.clearAttributes();
		for (final Node child : node.childNodes()) {
			boolean isRemove = false;
			if (child instanceof Element) {
				final Element el = (Element) child;
				final String name = el.normalName();
				if (el.childNodeSize() == 0 && el.text().isBlank()) {
					isRemove = true;
				} else if ("title".equals(name) && "head".equals(el.parent().normalName())) {
					el.tagName("h1");
				} else if ("h1".equals(name)) {
					el.tagName("h2");
				} else if ("h2".equals(name)) {
					el.tagName("h3");
				} else if ("h3".equals(name)) {
					el.tagName("h4");
				} else if ("h4".equals(name)) {
					isRemove = true;
				} else if ("h5".equals(name)) {
					isRemove = true;
				} else if ("h6".equals(name)) {
					isRemove = true;
				} else if ("button".equals(name) && el.ownText().isBlank()) {
					isRemove = true;
				}
			} else if (child instanceof TextNode) {
				final TextNode tn = (TextNode) child;
				if (tn.text().isBlank())
					isRemove = true;
			} else {
				isRemove = true;
			}
			if (isRemove) {
				child.remove();
				continue;
			}
			cleanUp(child);
		}

		final Aggror app = App.app();
		boolean isRemove = false;
		boolean isRemovePotentially = false;
		final String log = String.format("%-6s %-10s %d ", node.nodeName(), node.getClass().getSimpleName(), Integer.valueOf(node.childNodeSize()));
		if (node instanceof Element) {
			final Element el = (Element) node;
			if ("article".equals(el.normalName()) || "body".equals(el.normalName()))
				el.tagName("div");
			final String name = el.normalName();
			System.out.println(log + String.format("%-6s %d %s", name, Integer.valueOf(el.childrenSize()), el.ownText()));
			final int childrenSize = el.childrenSize();
			boolean isElUnwrap = false;
			isRemovePotentially = true;
			final Integer elPrioObj = app.tagPriorities.get(name);
			final int elPrio = elPrioObj == null ? Integer.MAX_VALUE : elPrioObj.intValue();
			if (elPrio == 0)
				isRemovePotentially = false;
			if (childrenSize == 0) {
				if (el.ownText().isBlank())
					isRemove = true;
				else if ("span".equals(name) || "a".equals(name) || "time".equals(name)) {
					el.text(" " + el.text() + " ");
					isElUnwrap = true;
				}
			} else if (childrenSize == 1 && el.ownText().isBlank()) {
				final Element fc = el.firstElementChild();
				final String fcName = fc.normalName();
				if (name.equals(fcName)) {
					isElUnwrap = true;
				} else {
					final Integer fcPrioObj = app.tagPriorities.get(fcName);
					final int fcPrio = fcPrioObj == null ? Integer.MAX_VALUE : fcPrioObj.intValue();
					if (fcPrio < elPrio) {
						System.out.println("--> el " + name + " " + elPrio + " > " + fcName + " " + fcPrio);
						isElUnwrap = true;
					} else if (elPrio < fcPrio) {
						System.out.println("--> el " + name + " " + elPrio + " < " + fcName + " " + fcPrio);
						fc.unwrap();
						isRemovePotentially = false;
					}
				}
			}
			if (!isRemove && !isElUnwrap && childrenSize == 0 && elPrio > 0) {
				if (childrenSize == 0 && !"p".equals(name))
					el.tagName("p");
			}

			if (isElUnwrap) {
				LOGGER.warn("UNWRAP " + name);
				isRemovePotentially = false;
				el.unwrap();
			}

			if (isRemovePotentially) {
				if (el.ownText().length() > 200)
					isRemovePotentially = false;
				if (isRemovePotentially && childrenSize > 0) {
					for (final Element child : el.children()) {
						final String n = child.normalName();
						if ("h1".equals(n) || "h2".equals(n)) {
							isRemovePotentially = false;
							break;
						}
						if (child.childrenSize() > 0) {
							isRemovePotentially = false;
							break;
						}
						if (child.text().length() > 200) {
							isRemovePotentially = false;
							break;
						}
					}
				}
			}

		} else if (node instanceof TextNode) {
			final TextNode tn = (TextNode) node;
			if (tn.childNodeSize() == 0) {
				tn.text(tn.text().replaceAll("\\s+", " ").trim());
			} else {
				LOGGER.warn("TEXTNODE: MORE THAN ZERO CHILDREN " + tn.childNodes().toString().replaceAll("\\s+", " "));
			}
			System.out.println(log + "       " + tn.text());
		} else {
			isRemove = true;
		}

		if (!isRemove && isRemovePotentially) {
			LOGGER.warn("REMOVE POT " + (node instanceof Element ? ((Element) node).normalName() : node.nodeName()));
			isRemove = true;
		}
		if (isRemove) {
			LOGGER.warn("REMOVE " + (node instanceof Element ? ((Element) node).normalName() : node.nodeName()));
			node.remove();
		}

		return node;
	}

	private static Node cleanUp(final Document doc, final URL url) {
		final Document clone = doc.clone();
		final List<Element> toRemoves = new ArrayList<>();
		final Elements articles = clone.getElementsByTag("article");
		Element article = null;
		if (!articles.isEmpty()) {
			for (final Element a : articles) {
				if (a.text().length() > MIN_ARTICLE_SIZE) {
					if (article == null)
						article = a;
					else
						toRemoves.add(a);
				} else {
					toRemoves.add(a);
				}
			}
			if (article == null)
				toRemoves.clear();
			else
				LOGGER.info("doc has article: " + url.toString());
		}
		for (final Element toRemove : toRemoves) {
			LOGGER.info("preclean remove " + toRemove.normalName());
			toRemove.remove();
		}
		return cleanUp(clone);
	}

	private boolean matches(final URL url) {
		if (url == null)
			return true;
		final String s = url.toString();
		if (!s.matches("^http.*" + name + "/.*"))
			return false;
		if (s.endsWith(name + "/"))
			return true;
		final String t = s.replaceAll("^http.*" + name + "/", "");
		for (final Pattern pattern : regexNoMatchs.values()) {
			if (pattern.matcher(t).matches())
				return false;
		}
		for (final Pattern pattern : regexMatchs.values()) {
			if (pattern.matcher(t).matches())
				return true;
		}
		return false;
	}

	private void articleUrls(final SortedSet<String> articleUrls, final String url, final SortedSet<String> visited) throws Exception {
		final String urlNotNull = url != null ? url : "https://" + name + "/";
		if (visited.contains(urlNotNull))
			return;
		final URL urlTyped = new URL(urlNotNull);
		if (!matches(urlTyped)) {
			LOGGER.info(urlTyped.toString() + " does not match regex");
			return;
		}
		LOGGER.info("article urls from: " + urlNotNull);
		final Document doc = Jsoup.parse(urlTyped, 10000);
		final Elements els = doc.getElementsByAttribute("href");
		final SortedSet<String> urlsNew = new TreeSet<>();
		for (final Element el : els) {
			try {
				String s = el.attr("href");
				int idx = s.indexOf('?');
				if (idx >= 0)
					s = s.substring(0, idx);
				idx = s.indexOf('#');
				if (idx >= 0)
					s = s.substring(0, idx);
				if (s.endsWith(".css"))
					continue;
				if (s.endsWith(".ico"))
					continue;
				if (s.endsWith(".jpeg") || s.endsWith(".jpg"))
					continue;
				if (s.endsWith(".js"))
					continue;
				if (s.endsWith(".png"))
					continue;
				if (s.endsWith(".woff2"))
					continue;
				if (s.startsWith("/"))
					s = "https://" + urlTyped.getHost() + s;
				else if (!s.startsWith("http"))
					continue;

				final URL articleUrl = new URL(s);
				if (matches(articleUrl)) {
					final String a = articleUrl.toString();
					if (!articleUrls.contains(a))
						urlsNew.add(a);
				}
			} catch (final Exception e) {
				LOGGER.warn(url + ": " + e.getMessage());
			}
		}
		//download(urlTyped, doc, true);
		visited.add(urlNotNull);
		articleUrls.addAll(urlsNew);
		LOGGER.info("add article urls: " + urlsNew.toString());
	}

	private void articleUrls(final SortedSet<String> articleUrls, final SortedSet<String> urls, final SortedSet<String> visited) throws Exception {
		for (final String url : new TreeSet<>(urls)) {
			try {
				articleUrls(articleUrls, url, visited);
			} catch (final Exception e) {
				LOGGER.warn(url + ": " + e.getMessage());
			}
		}
	}

	/*
	private void download(final URL url, Document doc, final boolean forceRedownload) {
		final Path pathHtml = path(url, "html_orig", "html");
		if (!Files.exists(pathHtml) || forceRedownload) {
			try {
				if (doc == null)
					doc = Jsoup.parse(url, 10000);
				Files.write(pathHtml, doc.toString().getBytes(StandardCharsets.UTF_8));
			} catch (final Exception e) {
				if (!Files.exists(pathHtml.getParent())) {
					pathHtml.getParent().toFile().mkdirs();
					download(url, doc, forceRedownload);
					return;
				}
				LOGGER.warn(url.toString() + ": " + e.getMessage());
			}
		}
		final Path pathClean = path(url, "html_clean", "html");
		try {
			if (!Files.exists(pathClean) || Files.getLastModifiedTime(pathClean).compareTo(Files.getLastModifiedTime(pathHtml)) < 0) {
				try {
					if (doc == null) {
						if (Files.exists(pathHtml))
							doc = Jsoup.parse(pathHtml.toFile());
						else
							doc = Jsoup.parse(url, 10000);
					}
					Files.write(pathClean, cleanUp(doc, url).toString().getBytes(StandardCharsets.UTF_8));
					LOGGER.info("write " + pathClean.getFileName().toString());
				} catch (final Exception e) {
					if (!Files.exists(pathClean.getParent())) {
						pathClean.getParent().toFile().mkdirs();
						download(url, doc, forceRedownload);
						return;
					}
					LOGGER.warn(url.toString() + ": " + e.getMessage());
				}
			}
		} catch (final Exception e) {
			LOGGER.warn(url.toString() + ": " + e.getMessage());
		}
	}

	public List<URL> download() throws Exception {
		final SortedSet<String> articleUrls = new TreeSet<>();
		final SortedSet<String> visited = new TreeSet<>();
		articleUrls(articleUrls, startPages, visited);
		final boolean isNewSite = !Files.exists(path());
		if (isNewSite) {
			path().toFile().mkdirs();
			articleUrls(articleUrls, articleUrls, visited);
		}
		LOGGER.info("DOWNLOAD CONTENT START");
		final List<URL> links = new ArrayList<>();
		for (final String articleUrl : articleUrls) {
			try {
				final URL url = new URL(articleUrl);
				download(url, null, false);
				links.add(url);
			} catch (final Exception e) {
				LOGGER.warn(articleUrl + ": " + e.getMessage());
			}
		}
		LOGGER.info("DOWNLOAD CONTENT END");
		return links;
	}
	*/
}
