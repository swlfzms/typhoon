package database;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

//单例模式，享元模式
public class ConnectionPool {
	
	private static Logger debugLogger = Logger.getLogger("DEBUG." + ConnectionPool.class.getName());
	
	private static Logger errorLogger = Logger.getLogger("Error." + ConnectionPool.class.getName());
		
	private static String className = ConnectionPool.class.getName();
	
	private Logger logger = Logger.getLogger(ConnectionPool.className);
	
	private static String path = ConnectionPool.class.getClassLoader().getResource("").getPath().toString();
	// 连接池大小
	private Vector<Connection> pool;
	
	// 路径属性
	private String ACTIONPATH = path + "/configuration/parameter.properties";
	private Properties prop = new Properties();
	
	/* 数据库公有属性 */
	private Connection conn;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String driverClassName = "com.mysql.jdbc.Driver";
	
	private int poolSize = 10;
	private static ConnectionPool instance = null;
	
	// 不可被实例
	private ConnectionPool() {		
		String methodName = "ConenctionPool";
		logger.debug("path: "+ path);
		if (ConnectionPool.debugLogger.isDebugEnabled()) {
			ConnectionPool.debugLogger.debug("MethodName:" + methodName + " ClassName:" + className);
		}
		
		pool = new Vector<Connection>(poolSize);
		
		try {
			
			File file = new File(ACTIONPATH);
			FileInputStream fis = new FileInputStream(file);
			
			prop.load(fis);
			dbUrl = prop.getProperty("mysql_url");
			dbUser = prop.getProperty("mysql_user");
			dbPwd = prop.getProperty("mysql_password");			
			for (int i = 0; i < poolSize; i++) {
				try {					
					Class.forName(driverClassName);					
					conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
					pool.add(conn);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (ConnectionPool.errorLogger.isDebugEnabled()) {
				ConnectionPool.errorLogger.error("error Message:" + e.getMessage() + ", below method:");
				ConnectionPool.errorLogger.error("MethodName:" + methodName + " ClassName:" + className);
			}
		}
		
	}
	
	public static ConnectionPool getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}
	
	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
	}
	
	/* 返回连接到连接池 */
	public synchronized void release(Connection conn) {
		pool.add(conn);
	}
	
	/* 返回连接池中的一个数据库连接 */
	public synchronized Connection getConnection() {
		if (pool.size() > 0) {
			Connection conn = pool.get(0);
			pool.remove(conn);
			return conn;
		} else {
			return null;
		}
	}
	
	public int getSize() {
		return this.pool.size();
	}
}
