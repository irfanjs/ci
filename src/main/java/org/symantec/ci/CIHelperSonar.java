package org.symantec.ci;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.PoolProperties;

public class CIHelperSonar {
	
	static private CIHelperSonar sonarhelper;
	private final static Object cihelperLock = new Object();
	
	
	private DataSource dataSource;
	
	private CIHelperSonar()
	{
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:mysql://10.211.64.231/sonar");
		p.setDriverClassName("com.mysql.jdbc.Driver");
		p.setUsername("sonar");
		p.setPassword("sonar");

		p.setJmxEnabled(true);
		p.setTestWhileIdle(true);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(100);
		p.setInitialSize(10);
		p.setMaxWait(10000);
		p.setRemoveAbandonedTimeout(60);
		p.setMinEvictableIdleTimeMillis(30000);
		p.setMinIdle(10);
		p.setLogAbandoned(true);
		p.setRemoveAbandoned(true);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

		dataSource = new DataSource(p);

	}
	
	
	public static CIHelperSonar getInstance(){
		synchronized (cihelperLock) {
			if(null == sonarhelper){
				sonarhelper = new CIHelperSonar();
			}
		}
		return sonarhelper;
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/*private Connection connect;
	private final Object connlock = new Object();
	public Connection getConnection() throws SQLException{
		synchronized(connlock){
			if(null == connect){
				// Setup the connection with the Sonar DB
				connect = DriverManager
						.getConnection("jdbc:mysql://10.211.64.231/sonar?"
								+ "user=sonar&password=sonar&noAccessToProcedureBodies=true&autoReconnect=true");
			}
		}
		return connect;
	}*/
	public Statement createExecuteStatement() throws SQLException,
	ClassNotFoundException {
		// Statements allow to issue SQL queries to the database
		// Connection connect = CIDBHelper.getInstance();
		Connection c = getConnection();
		return c.createStatement();
	}

}