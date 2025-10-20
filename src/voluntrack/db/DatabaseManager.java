package voluntrack.db;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite connection manager (Singleton).
 */
public final class DatabaseManager {
    private static volatile DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) instance = new DatabaseManager();
            }
        }
        return instance;
    }

    public synchronized void connect(String dbPath) {
        if (this.connection != null) return; // already connected
        try {
            // ensure folders exist
            new java.io.File("data/exports").mkdirs();

            // ensure driver registered
            try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignored) {}

            String url = "jdbc:sqlite:" + dbPath;
            this.connection = DriverManager.getConnection(url);

            try (Statement st = this.connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
                st.execute("PRAGMA journal_mode = WAL");
                st.execute("PRAGMA synchronous = NORMAL");
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB connect failed: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        if (this.connection == null) throw new IllegalStateException("DB not connected");
        return this.connection;
    }

    public void initSchema(InputStream schemaSql) {
        if (schemaSql == null) throw new IllegalArgumentException("schema.sql not found on classpath");
        try {
            String sql = new String(schemaSql.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement st = getConnection().createStatement()) {
                st.executeUpdate(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Init schema failed: " + e.getMessage(), e);
        }
    }

    public synchronized void close() {
        if (this.connection != null) {
            try { this.connection.close(); } catch (SQLException ignored) {}
            this.connection = null;
        }
    }
}