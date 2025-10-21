package voluntrack.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Initial data loader for first run.
 * - Create default admin if missing
 * - Import projects from CSV if table is empty
 */
public final class SeedData {
    private SeedData() {}

    // SHA-256("Admin654!@")
    private static final String ADMIN_DEFAULT_HASH = "a9be1f48c251d49ee373d8c8cf763e20d406d11af339f1a122bc12703841859b";

    public static void run(String csvPathIfAny) {
        Connection conn = DatabaseManager.getInstance().getConnection();
        seedAdminIfMissing(conn);
        importProjectsIfEmpty(conn, csvPathIfAny);
    }

    private static void seedAdminIfMissing(Connection conn) {
        try (
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
        ) {
            ps.setString(1, "admin");
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                if (count == 0) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO users(full_name, username, email, password_hash, role, created_at) " +
                                    "VALUES(?,?,?,?,?,?)")) {
                        ins.setString(1, "Administrator");
                        ins.setString(2, "admin");
                        ins.setString(3, "admin@example.com");
                        ins.setString(4, ADMIN_DEFAULT_HASH);
                        ins.setString(5, "admin");
                        ins.setString(6, voluntrack.util.TimeUtil.nowIso());
                        ins.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("seedAdminIfMissing failed", e);
        }
    }

    private static void importProjectsIfEmpty(Connection conn, String csvPathIfAny) {
        int count = scalarCount(conn, "SELECT COUNT(*) FROM projects");
        if (count > 0) {
            System.out.println("[Seed] skip import, projects has " + count);
            return;
        }

// 1) ไฟล์บนดิสก์
        if (csvPathIfAny != null && !csvPathIfAny.isBlank()) {
            File f = new File(csvPathIfAny);
            if (f.exists() && f.isFile()) {
                try {
                    importFromCsv(conn, f);
                    System.out.println("[Seed] imported from file: " + f.getAbsolutePath());
                    return;
                } catch (Exception e) {
                    System.out.println("[Seed] file import failed: " + e.getMessage());
                }
            } else {
                System.out.println("[Seed] file not found: " + f.getAbsolutePath());
            }
        }

// 2) classpath /projects.csv
        try (InputStream in = SeedData.class.getResourceAsStream("/projects.csv")) {
            if (in != null) {
                var tmp = java.nio.file.Files.createTempFile("projects", ".csv");
                java.nio.file.Files.copy(in, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                importFromCsv(conn, tmp.toFile());
                java.nio.file.Files.deleteIfExists(tmp);
                System.out.println("[Seed] imported from classpath /projects.csv");
                return;
            } else {
                System.out.println("[Seed] classpath /projects.csv not found");
            }
        } catch (Exception e) {
            System.out.println("[Seed] classpath import failed: " + e.getMessage());
        }

// 3) fallback ค่าเริ่มต้น
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO projects(title,location,day,hourly_value,total_slots,registered_slots,enabled,created_at) " +
                        "VALUES(?,?,?,?,?,?,?,?)")) {

        } catch (SQLException e) {
            throw new RuntimeException("seed default projects failed", e);
        }


    }


    private static void importFromCsv(Connection conn, File csv) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csv, StandardCharsets.UTF_8));
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO projects(title,location,day,hourly_value,total_slots,registered_slots,enabled,created_at) " +
                             "VALUES(?,?,?,?,?,?,?,?)")) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (first) { // skip header when present
                    String low = line.toLowerCase();
                    if (low.contains("title") && low.contains("location")) { first = false; continue; }
                }
                first = false;

                // simple CSV split for 7 parts at most
                String[] parts = line.split(",", 7);
                String title = get(parts, 0);
                String location = get(parts, 1);
                String day = get(parts, 2);
                int hourly = parseIntSafe(get(parts, 3), 1);
                int registered = parseIntSafe(get(parts, 4), 0);
                int totalSlots = parseIntSafe(get(parts, 5), 1);
                int enabled = parseIntSafe(get(parts, 6), 1);

                insertProject(ps, title, location, day, hourly, totalSlots, registered, enabled);
            }
        }
    }

    private static void insertProject(PreparedStatement ps, String title, String location, String day,
                                      int hourly, int total, int registered, int enabled) throws SQLException {
        ps.setString(1, title);
        ps.setString(2, location);
        ps.setString(3, day);
        ps.setInt(4, hourly);
        ps.setInt(5, total);
        ps.setInt(6, registered);
        ps.setInt(7, enabled);
        ps.setString(8, voluntrack.util.TimeUtil.nowIso());
        ps.executeUpdate();
    }

    private static int scalarCount(Connection conn, String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("scalarCount failed", e);
        }
    }

    private static String get(String[] arr, int idx) { return idx < arr.length ? arr[idx].trim() : ""; }
    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }


    }
}
