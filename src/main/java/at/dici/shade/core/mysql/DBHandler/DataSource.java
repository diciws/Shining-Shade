package at.dici.shade.core.mysql.DBHandler;

import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DataSource {
    private static final HikariDataSource ds;

    static {
        //register driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getClassLoader() == cl) {
                    Logger.log(LogLevel.BOT, "Registered MySQL JDBC driver id: "+driver);
                }
            }

        }
        catch (ClassNotFoundException ex) {
            Logger.log(LogLevel.FATAL, "DB Class not found");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( "jdbc:mysql://localhost:3306/shadebot" );
        config.setUsername( "shadebot" );
        config.setPassword( "password" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
