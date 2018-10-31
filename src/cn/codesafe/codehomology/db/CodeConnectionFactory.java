package cn.codesafe.codehomology.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;


import cn.codesafe.codehomology.CodeHomology;
import cn.codesafe.codehomology.utils.Settings;


public final class CodeConnectionFactory {
	public static String url;
	public static String user;
	public static String pwd;
	public static final String DB_DRIVER_NAME_KEY = "db.h2.driver.name";
	public static final String DB_FILE_PATH_KEY = "db.h2.file.path";
	public static final String DB_URL_KEY = "db.h2.url";
	public static final String DB_USER_KEY = "db.h2.user";
	public static final String DB_PWD_KEY = "db.h2.pwd";
	private static final Logger LOGGER = Logger.getLogger(CodeConnectionFactory.class.getName());
	private CodeConnectionFactory() {
    }
	public static Connection getConnection() throws Exception {
		initialize();
		return DriverManager.getConnection(url, user, pwd);
	}
	public static synchronized void initialize() {
		if(url != null) {
			return ;
		}
		
		Connection conn = null;
		String driverName = Settings.getProperty(DB_DRIVER_NAME_KEY);
		try {
			Class.forName(driverName);
		} catch (Exception e) {
			LOGGER.info("No Driver Class Found");
			System.exit(0);
		}
		
		String fileName = Settings.getProperty(DB_FILE_PATH_KEY);
		File file = new File(CodeHomology.CODEHOMELOGY_HOME, fileName);
		url = String.format(Settings.getProperty(DB_URL_KEY), file.getAbsolutePath().split(".h2.db")[0]);
		user = Settings.getProperty(DB_USER_KEY);
		pwd = Settings.getProperty(DB_PWD_KEY);
		try {
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (Exception e) {
			LOGGER.info("DB's user or password WRONG");
			System.exit(0);
		}
		try {
			conn.close();
		} catch (Exception e) {
			LOGGER.info("DB connection close error, but nothing");
			e.printStackTrace();
		}
	}
	
	public static void ensureSchemaVersion(Connection conn) {
		LOGGER.info("[ConnectionFactory] ensureSchemaVersion? do nothing now");
	}
}
