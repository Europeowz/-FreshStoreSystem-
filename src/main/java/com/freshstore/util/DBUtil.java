package com.freshstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public final class DBUtil {
    private static final String DEFAULT_URL = "jdbc:sqlite:freshstore.db";
    private static String url = DEFAULT_URL;
    private static boolean driverLoaded;

    private DBUtil() {}

    public static Connection getConnection() throws Exception {
        if (!driverLoaded) {
            Class.forName("org.sqlite.JDBC");
            driverLoaded = true;
        }
        Connection conn = DriverManager.getConnection(url);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void setUrl(String url) {
        DBUtil.url = url;
    }

    public static void resetUrl() {
        DBUtil.url = DEFAULT_URL;
        driverLoaded = false;
    }
}
