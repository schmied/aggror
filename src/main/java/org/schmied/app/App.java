package org.schmied.app;

import java.nio.file.*;
import java.util.SortedMap;

import org.knowm.sundial.SundialJobScheduler;
import org.slf4j.*;

public abstract class App {

	public abstract void initDb() throws Exception;

	// ---

	private static App APP;

	public final Logger log = LoggerFactory.getLogger(getClass());

	public final String name;
	public final Prop prop;
	public final Db db;
	public final Path dir;

	public App() throws Exception {
		if (APP != null)
			throw new Exception("App instance already running.");
		APP = this;
		final String className = getClass().getName();
		final String propKeyPrefix = className.replace("." + getClass().getSimpleName(), "");
		name = propKeyPrefix.replaceAll(".*\\.", "");
		log.info("Application: {}", name);

		Prop initProp = null;
		try {
			initProp = new Prop(propKeyPrefix);
		} catch (final Exception e) {
			Log.warn(log, e);
			exit(1, e.getMessage());
		}
		prop = initProp;
		log.info("Properties:\n{}", prop.toString(2, 32));

		dir = Paths.get(prop.get("dir")).toAbsolutePath();

		final SortedMap<String, String> propDb = prop.getMapOfString("db");
		if (propDb.size() != 3)
			throw new Exception("DB properties must only define exactly: url, username, and password.");

		Db initDb = null;
		try {
			initDb = new Db(propDb);
		} catch (final Exception e) {
			Log.warn(log, e);
			exit(1, e.getMessage());
		}
		db = initDb;
		log.info("DB: {}", db.toString());
		initDb();

		SundialJobScheduler.startScheduler(prop.keyPrefix());
	}

	@SuppressWarnings("unchecked")
	public static <T extends App> T app() {
		return (T) APP;
	}

	public static final Logger log() {
		return APP.log;
	}

	public static void exit(final int status, final String message) {
		System.err.println(message);
		System.err.println("Terminate application.");
		System.exit(status);
	}

	public void close() {
		db.close();
	}

//	public String propGet(final String key) {
//		return prop.get(key);
//	}

/*
	public final void logDebug(final Object... message) {
		Log.debug(log, message);
	}

	public final void logInfo(final Object... message) {
		Log.info(log, message);
	}

	public final void logWarn(final Object... message) {
		Log.warn(log, message);
	}
*/
}
