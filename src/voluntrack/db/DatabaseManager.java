package voluntrack.db;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static volatile DatabaseManager instance;
    private Connection connection;
    private String dbPath;

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
        this.dbPath = dbPath;
        // folders
        new java.io.File("data/exports").mkdirs();
        // driver
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignored) {}
        // open if not open
        ensureOpen();
    }

    public Connection getConnection() {
        ensureOpen();
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

    private synchronized void ensureOpen() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                String url = "jdbc:sqlite:" + this.dbPath;
                this.connection = DriverManager.getConnection(url);
                try (Statement st = this.connection.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                    st.execute("PRAGMA journal_mode = WAL");
                    st.execute("PRAGMA synchronous = NORMAL");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB open failed: " + e.getMessage(), e);
        }
    }


}