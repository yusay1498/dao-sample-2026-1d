CREATE TABLE IF NOT EXISTS venues (
  venue_id VARCHAR(36) PRIMARY KEY,
  venue_name VARCHAR(50) NOT NULL,
  capacity INTEGER NOT NULL CHECK (capacity >= 0),
  address VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_categories (
  event_category_id VARCHAR(36) PRIMARY KEY,
  event_category_name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
  event_id VARCHAR(36) PRIMARY KEY,
  venue_id VARCHAR(36) NOT NULL,
  event_category_id VARCHAR(36) NOT NULL,
  event_name VARCHAR(100) NOT NULL,
  performer VARCHAR(100) NOT NULL,
  description VARCHAR(1500) NOT NULL,
  start_time TIMESTAMPTZ NOT NULL,
  end_time TIMESTAMPTZ NOT NULL,
  available_seats INTEGER NOT NULL,
  reserved_seats INTEGER NOT NULL,

  CONSTRAINT fk_events_venue FOREIGN KEY (venue_id) REFERENCES venues (venue_id),
  CONSTRAINT fk_events_category FOREIGN KEY (event_category_id) REFERENCES event_categories (event_category_id),

  CONSTRAINT check_end_time_after_start_time CHECK (end_time > start_time)
  );

CREATE INDEX IF NOT EXISTS idx_events_venue_id ON events (venue_id);
CREATE INDEX IF NOT EXISTS idx_event_categories_name ON event_categories (event_category_name);
