package de.unibayreuth.bayceer.bayeos.servlet;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServletInitializer implements ServletContextListener {

	private static final String con = "java:comp/env/jdbc/bayeos";

	private static DataSource dataSource;

	private static Logger log = Logger.getRootLogger();

	public void contextDestroyed(ServletContextEvent event) {
		log.info("Context destroyed");
	}

	public void contextInitialized(ServletContextEvent event) {

		// Logging
		String logPath = event.getServletContext().getRealPath("/WEB-INF/log4j.properties");
		if (logPath != null) {
			PropertyConfigurator.configureAndWatch(logPath, 60 * 1000 * 5);
			// Check changes every 5 minutes
			log.debug("Read out configuration from: " + logPath + ".");
		} else {
			log.warn("File: " + logPath + " not found!");
		}
		log.setAdditivity(false);

		ServletContext sc = event.getServletContext();
		sc.setAttribute("ctx", sc.getContextPath());
		
		log.info("Context initialized");
	}

	public static Connection getConnection() throws SQLException {
		if (dataSource == null) {
			try {
				log.debug("Initialize datasource");
				Context initCtx = new InitialContext();
				dataSource = (DataSource) initCtx.lookup(con);
				log.info("Datasource look up completed.");
			} catch (NamingException e) {
				log.error(e.getMessage());
				throw new SQLException(String.format("Datasource %s not found", con), e);
			}
		}
		return dataSource.getConnection();
	}

}
