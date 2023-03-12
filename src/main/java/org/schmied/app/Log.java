package org.schmied.app;

import java.util.*;

import org.slf4j.*;

public class Log {

	public static final Logger ROOT = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	private static void warn(final StringBuilder sb, final Throwable e, final int idxException, final int idxCause) {
		sb.append('[');
		sb.append(idxException);
		sb.append(':');
		sb.append(idxCause);
		sb.append("] ");
		sb.append(e.getClass().getName());
		sb.append(": ");
		sb.append(e.getMessage());
		sb.append('\n');
		for (final StackTraceElement ste : e.getStackTrace()) {
			sb.append("        at ");
			sb.append(ste.getClassName().replaceAll(".*\\.", ""));
			sb.append('.');
			sb.append(ste.getMethodName());
			sb.append('(');
			sb.append(ste.getFileName());
			sb.append(':');
			sb.append(ste.getLineNumber());
			sb.append(')');
			sb.append('\n');
		}
		if (e.getCause() != null)
			warn(sb, e.getCause(), idxException, idxCause);
	}

	public static void warn(final Logger logger, final Collection<Throwable> exceptions) {
		final StringBuilder sb = new StringBuilder(1024);
		sb.append('\n');
		int idxException = 0;
		for (final Throwable e : exceptions) {
			warn(sb, e, idxException, 0);
			idxException++;
		}
		logger.warn(sb.toString());
	}

	public static void warn(final Logger logger, final Throwable exception) {
		final List<Throwable> l = new ArrayList<>();
		l.add(exception);
		warn(logger, l);
	}

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
