package org.schmied.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import org.slf4j.*;

public class Prop {

	private static final Logger LOGGER = LoggerFactory.getLogger(Prop.class);

	public static final Prop ALL = new Prop();

	private static final String PAT_KEY_REGEX = "[a-z][a-zA-Z0-9._]*";

	private final String keyPrefix;
	private final SortedMap<String, String> props;

	private Prop(final String keyPrefix, final Map<String, String> map) {
		this.keyPrefix = keyPrefix;
		this.props = new TreeMap<>(map);
	}

	private Prop() {
		this(null, load());
	}

	public Prop(final String keyPrefix) throws Exception {
		this(keyPrefix, getMapOfString(ALL.props, keyPrefix));
	}

	public String toString(final int indent, final int width) {
		final StringBuilder sb = new StringBuilder(4096);
		for (final String key : props.keySet()) {
			if (indent > 0)
				sb.append(String.format("%" + indent + "s", " "));
			if (width > 0)
				sb.append(String.format("%-" + width + "s", key));
			else
				sb.append(key);
			sb.append(" = ");
			sb.append(key.toLowerCase().contains("password") ? "***" : props.get(key));
			if (!key.equals(props.lastKey()))
				sb.append('\n');
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString(0, 0);
	}

	public String keyPrefix() {
		return keyPrefix;
	}

	private static void load(final SortedMap<String, String> props, final InputStream is) throws Exception {
		try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8); final BufferedReader br = new BufferedReader(isr, 1024)) {
			int lineNo = 0;
			String line;
			final Pattern patKey = Pattern.compile(PAT_KEY_REGEX);
			final SortedMap<String, Integer> keys = new TreeMap<>();
			while ((line = br.readLine()) != null) {
				lineNo++; // human readable, starts with 1
				line = line.trim();
				if (line.isEmpty())
					continue;
				if (line.startsWith("#"))
					continue;
				final String logPrefix = "Line " + lineNo + ": ";
				final String[] parts = line.split("=", 2);
				if (parts.length != 2)
					throw new Exception(logPrefix + "Cannot find '='.");
				final String key = parts[0].trim();
				if (!patKey.matcher(key).matches())
					throw new Exception(logPrefix + "Key '" + key + "' does not match '" + PAT_KEY_REGEX + "'.");
				if (keys.keySet().contains(key))
					throw new Exception(logPrefix + "Value for key '" + key + "' already defined in line " + keys.get(key) + ".");
				keys.put(key, Integer.valueOf(lineNo));
				final String value = parts[1].trim();
				if (value.isEmpty())
					continue;
				props.put(key, value);
			}
		} catch (final Exception e) {
			throw e;
		}
	}

	private static void load(final SortedMap<String, String> props, final String filenameSuffix) {
		final Path file = Paths.get("properties" + filenameSuffix);
		if (Files.isRegularFile(file)) {
			LOGGER.info("Read property file " + file.toAbsolutePath());
			try (final InputStream is = Files.newInputStream(file)) {
				load(props, is);
			} catch (final Exception e) {
				App.exit(1, new Exception("Cannot read property file " + file.toAbsolutePath().toString(), e));
			}
		} else {
			LOGGER.info("No property file " + file.toAbsolutePath());
		}
	}

	private static SortedMap<String, String> load() {
		final SortedMap<String, String> props = new TreeMap<>();
		try (final InputStream is = Prop.class.getResourceAsStream("/properties")) {
			load(props, is);
		} catch (final Exception e) {
			App.exit(1, new Exception("Cannot read application properties.", e));
		}
		load(props, "." + System.getProperty("user.name"));
		return props;
	}

	private static final SortedMap<String, String> getMapOfString(final SortedMap<String, String> props, final String keyPrefix) throws Exception {
		final String keyFrom = keyPrefix + ".";
		final String keyTo = keyPrefix + "/";
		final SortedMap<String, String> mapSub = props.subMap(keyFrom, keyTo);
		if (mapSub == null || mapSub.isEmpty())
			throw new Exception("No properties with key prefix '" + keyPrefix + "' for: " + props);
		final SortedMap<String, String> map = new TreeMap<>();
		for (final String mapSubKey : mapSub.keySet())
			map.put(mapSubKey.replace(keyFrom, ""), mapSub.get(mapSubKey));
		return map;
	}

	public final SortedSet<String> allKeys() {
		return (SortedSet<String>) props.keySet();
	}

	public final String get(final String key) {
		return props.get(key);
	}

	public final int getInt(final String key, final int defaultValue) {
		return V.getInt(props.get(key), defaultValue);
	}

	public final SortedMap<String, String> getMapOfString(final String prefix) throws Exception {
		return getMapOfString(props, prefix);
	}
}
