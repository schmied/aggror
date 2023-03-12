package org.schmied.app;

import java.sql.*;
import java.util.*;

import org.slf4j.*;

public class Db {

	private static final Logger LOGGER = LoggerFactory.getLogger(Db.class);

	public final Connection connection;
	//private final SortedMap<String, PreparedStatement> statements;

	public Db(final String url, final String username, final String password) throws Exception {
		connection = DriverManager.getConnection(url, username, password);
		//statements = new TreeMap<>();
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

	public void update(final String... sqls) throws Exception {
		try (final Statement st = connection.createStatement()) {
			for (final String sql : sqls)
				st.executeUpdate(sql);
		} catch (final Exception e) {
			throw e;
		}
	}

	public void update(final Collection<String> sqls) throws Exception {
		try (final Statement st = connection.createStatement()) {
			for (final String sql : sqls)
				st.execute(sql);
		} catch (final Exception e) {
			throw e;
		}
	}

/*
	public ResultSet query(final String sql) throws Exception {
		try (final Statement st = connection.createStatement()) {
			return st.executeQuery(sql);
		} catch (final Exception e) {
			throw e;
		}
	}
*/

	public <T> T queryObject(final String sql, final Class<T> c) throws Exception {
		try (final Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next())
				return rs.getObject(1, c);
			return null;
		} catch (final Exception e) {
			throw e;
		}
	}

	public String queryString(final String sql) throws Exception {
		try (final Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			if (rs.next())
				return rs.getString(1);
			return null;
		} catch (final Exception e) {
			throw e;
		}
	}

	public String[] queryStrings(final String sql) throws Exception {
		final List<String> result = new ArrayList<>();
		try (final Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				result.add(rs.getString(1));
		} catch (final Exception e) {
			throw e;
		}
		return result.toArray(new String[result.size()]);
	}

	public void close() {
		try {
			connection.close();
		} catch (final Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}
}
