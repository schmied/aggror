package org.schmied.aggror;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.schmied.app.Log;
import org.slf4j.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class ServerHandler extends AbstractHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

	private static final char CMD_ARTICLE = 'a';

	private static final String PARAM_KEY_ARTICLE_PK = "i";
	private static final String PARAM_KEY_ARTICLE_URL = "u";

	private static String paramValue(final Map<String, String[]> params, final String key) {
		final String[] values = params.get(key);
		if (values == null)
			return null;
		if (values.length != 1) {
			LOGGER.info(values.toString() + " must have exactly 1 element for key '" + key + "'.");
			return null;
		}
		return values[0];
	}

	private static void handleArticle(final Map<String, String[]> params, final HttpServletResponse response) throws Exception {
		Article article = null;

		String value = paramValue(params, PARAM_KEY_ARTICLE_PK);
		if (value != null)
			article = Article.valueOfPk(value);
		value = paramValue(params, PARAM_KEY_ARTICLE_URL);
		if (value != null)
			article = Article.valueOfUrl(value);

		if (article != null)
			response.getWriter().write(article.toString());
		LOGGER.info("XX " + article);
	}

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {
		try {
			LOGGER.info("target {}  baseRequest {}  request {}  response {}", target, baseRequest, request, response);
			baseRequest.setHandled(true);
			response.setStatus(HttpServletResponse.SC_OK);
			if (target.length() != 2) {
				return;
			}
			final Map<String, String[]> params = baseRequest.getParameterMap();
			switch (target.charAt(1)) {
			case CMD_ARTICLE:
				handleArticle(params, response);
				break;
			default:
				break;
			}
		} catch (final Exception e) {
			Log.warn(LOGGER, e);
		}
	}

	public static void startServer() throws Exception {
		final HttpConfiguration config = new HttpConfiguration();
		config.setSendDateHeader(false);
		config.setSendServerVersion(false);
		config.setSendXPoweredBy(false);
		final Server server = new Server();
		final ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(config));
		connector.setPort(8080);
		server.addConnector(connector);
		server.setHandler(new ServerHandler());
		server.start();
	}
}
