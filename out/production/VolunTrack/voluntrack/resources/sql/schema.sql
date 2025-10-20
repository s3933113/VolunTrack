-- VolunTrack SQLite schema
-- Keep PRAGMA foreign_keys=ON in code (set by DatabaseManager)

BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS users (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name       TEXT    NOT NULL,
    username        TEXT    NOT NULL UNIQUE,
    email           TEXT    NOT NULL,
    password_hash   TEXT    NOT NULL,
    role            TEXT    NOT NULL CHECK(role IN ('user','admin')),
    created_at      TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

CREATE TABLE IF NOT EXISTS projects (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    title             TEXT    NOT NULL,
    location          TEXT    NOT NULL,
    day               TEXT    NOT NULL CHECK(day IN ('Mon','Tue','Wed','Thu','Fri','Sat','Sun')),
    hourly_value      INTEGER NOT NULL CHECK(hourly_value BETWEEN 1 AND 100),
    total_slots       INTEGER NOT NULL CHECK(total_slots BETWEEN 1 AND 100),
    registered_slots  INTEGER NOT NULL DEFAULT 0 CHECK(registered_slots >= 0 AND registered_slots <= total_slots),
    enabled           INTEGER NOT NULL DEFAULT 1 CHECK(enabled IN (0,1)),
    created_at        TEXT    NOT NULL,
    UNIQUE(title, location, day)
);

CREATE INDEX IF NOT EXISTS idx_projects_title ON projects(title);
CREATE INDEX IF NOT EXISTS idx_projects_enabled ON projects(enabled);

CREATE TABLE IF NOT EXISTS registrations (
    id               TEXT    PRIMARY KEY,              -- 4-digit zero padded (e.g., 0001)
    user_id          INTEGER NOT NULL,
    project_id       INTEGER NOT NULL,
    slots            INTEGER NOT NULL CHECK(slots BETWEEN 1 AND 3),
    hours_per_slot   INTEGER NOT NULL CHECK(hours_per_slot BETWEEN 1 AND 3),
    confirmed_at     TEXT    NOT NULL,
    total_value      INTEGER NOT NULL CHECK(total_value >= 0),
    FOREIGN KEY(user_id)   REFERENCES users(id)    ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY(project_id) REFERENCES projects(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (length(id) = 4)
);

CREATE INDEX IF NOT EXISTS idx_reg_user ON registrations(user_id);
CREATE INDEX IF NOT EXISTS idx_reg_project ON registrations(project_id);
CREATE INDEX IF NOT EXISTS idx_reg_confirmed_at ON registrations(confirmed_at);

COMMIT;