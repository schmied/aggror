package org.schmied.app;

import java.sql.*;
import java.util.*;

import org.slf4j.*;

public class Db {

	private static final Logger LOGGER = LoggerFactory.getLogger(Db.class);

	public final Connection connection;
	private final SortedMap<String, PreparedStatement> statements;

	public Db(final String url, final String username, final String password) throws Exception {
		connection = DriverManager.getConnection(url, username, password);
		statements = new TreeMap<>();
		LOGGER.info(toString());
	}

	public Db(final SortedMap<String, String> props) throws Exception {
		this(props.get("url"), props.get("username"), props.get("password"));
	}

	@Override
	public String toString() {
		try {
			final DatabaseMetaData md = connection.getMetaData();
			return md.getDatabaseProductName() + " " + md.getDatabaseProductVersion() + " (" + md.getDriverName() + " " + md.getDriverVersion() + ")";
		} catch (final Exception e) {
			Log.warn(LOGGER, e);
			return "<error receiving database information>";
		}
	}

	public void execute(final String... sqls) throws Exception {
		try (final Statement st = connection.createStatement()) {
			for (final String sql : sqls)
				st.execute(sql);
		} catch (final Exception e) {
			throw e;
		}
	}

	public void execute(final Collection<String> sqls) throws Exception {
		try (final Statement st = connection.createStatement()) {
			for (final String sql : sqls)
				st.execute(sql);
		} catch (final Exception e) {
			throw e;
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (final Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}
}
