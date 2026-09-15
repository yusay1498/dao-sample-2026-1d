CREATE TABLE IF NOT EXISTS venues (
    venue_id VARCHAR(36) PRIMARY KEY,
    venue_name VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    address VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_categories (
    event_category_id VARCHAR(36) PRIMARY KEY,
    event_category_name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(36) PRIMARY KEY,
    venue_id VARCHAR(36) NOT NULL REFERENCES venues (venue_id),
    event_category_id VARCHAR(36) NOT NULL REFERENCES event_categories (event_category_id),
    event_name VARCHAR(100) NOT NULL,
    performer VARCHAR(100) NOT NULL,
    description VARCHAR(1500) NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    available_seats INTEGER NOT NULL,
    reserved_seats INTEGER NOT NULL
);
