package org.schmied.aggror;

public class Config {

	/*
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	public static final Config CONFIG = new Config();

	public static final String PROP_PREFIX = "org.schmied.aggror.";

	private static final String PROP_TAG_PRIORITIES = "tagPriority";

	public Config() {
		final SortedMap<String, Integer> tpMap = new TreeMap<>();
		try {
			final Properties p = new Properties();
			try (final InputStream is = Site.class.getResourceAsStream("/properties")) {
				p.load(is);
			}
			final Object tpVal = p.get(PROP_PREFIX + PROP_TAG_PRIORITIES);
			final String[] tps = tpVal == null ? new String[0] : tpVal.toString().split(">");
			int idx = 0;
			for (final String tp : tps) {
				final String[] tags = tp.split(" ");
				for (final String s : tags) {
					final String tag = s.trim();
					if (!tag.isEmpty())
						tpMap.put(tag, Integer.valueOf(idx));
				}
				idx++;
			}
		} catch (final Exception e) {
			LOGGER.warn(e.getMessage());
		}
		this.tagPriorities = tpMap;
		LOGGER.info("config " + this.tagPriorities.toString());
	}
	*/
}
