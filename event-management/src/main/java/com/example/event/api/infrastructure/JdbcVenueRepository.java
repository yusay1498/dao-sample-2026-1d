package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.Venue;
import com.example.event.api.domain.repository.VenueRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcVenueRepository implements VenueRepository {

    private final JdbcClient jdbcClient;

    public JdbcVenueRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Venue> findById(String venueId) {
        return jdbcClient.sql("""
                SELECT
                    venue_id,
                    venue_name,
                    capacity,
                    address
                FROM
                    venues
                WHERE
                    venue_id = :venueId
                """)
                .param("venueId", venueId)
                .query(rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty());
    }

    private static Venue mapRow(ResultSet rs) throws SQLException {
        return new Venue(
                rs.getString("venue_id"),
                rs.getString("venue_name"),
                rs.getInt("capacity"),
                rs.getString("address")
        );
    }
}
