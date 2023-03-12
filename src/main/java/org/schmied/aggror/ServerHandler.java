package org.schmied.aggror;

import java.io.IOException;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class ServerHandler extends AbstractHandler {

	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println(target + " X " + baseRequest + " X " + request + " X " + response + " X");
	}

	public static void startServer() throws Exception {
		final HttpConfiguration config = new HttpConfiguration();
		config.setSendServerVersion(false);
		final Server server = new Server();
		final ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(config));
		connector.setPort(8080);
		server.addConnector(connector);
		final ContextHandler context = new ContextHandler();
		context.setContextPath("/a");
		context.setHandler(new ServerHandler());
		server.setHandler(context);

		final ContextHandlerCollection contextCollection = new ContextHandlerCollection();
		server.setHandler(contextCollection);

		server.start();
	}
}
