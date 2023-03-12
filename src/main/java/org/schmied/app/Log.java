package org.schmied.app;

import org.slf4j.*;

public class Log {

	public static final Logger ROOT = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	/*
	public static final void debug(final Logger logger, final Object... message) {
		if (!logger.isDebugEnabled())
			return;
		final StringBuilder sb = new StringBuilder(16 * message.length);
		for (final Object m : message)
			sb.append(m);
		logger.debug(sb.toString());
	}

	public static final void debug(final Object... message) {
		debug(ROOT, message);
	}

	public static final void info(final Logger logger, final Object... message) {
		if (!logger.isInfoEnabled())
			return;
		final StringBuilder sb = new StringBuilder(16 * message.length);
		for (final Object m : message)
			sb.append(m);
		logger.info(sb.toString());
	}

	public static final void info(final Object... message) {
		info(ROOT, message);
	}

	public static final void warn(final Logger logger, final Object... message) {
		if (!logger.isWarnEnabled())
			return;
		final StringBuilder sb = new StringBuilder(16 * message.length);
		for (final Object m : message)
			sb.append(m);
		logger.warn(sb.toString());
	}

	public static final void warn(final Object... message) {
		warn(ROOT, message);
	}
	*/
}
