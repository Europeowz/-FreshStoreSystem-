package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBUtil {
    private static final String URL = "jdbc:sqlite:freshstore.db";
    private static boolean driverLoaded = false;

    public static Connection getConnection() throws Exception {
        if (!driverLoaded) {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
        }
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
}
