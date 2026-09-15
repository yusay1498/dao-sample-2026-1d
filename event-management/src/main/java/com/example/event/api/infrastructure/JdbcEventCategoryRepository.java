package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.EventCategory;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import com.example.event.api.domain.repository.EventCategoryRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcEventCategoryRepository implements EventCategoryRepository {

    private final JdbcClient jdbcClient;

    public JdbcEventCategoryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<EventCategory> findById(String eventCategoryId) {
        return jdbcClient.sql("""
                SELECT
                    event_category_id,
                    event_category_name
                FROM
                    event_categories
                WHERE
                    event_category_id = :eventCategoryId
                """)
                .param("eventCategoryId", eventCategoryId)
                .query(rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty());
    }

    private static EventCategory mapRow(ResultSet rs) throws SQLException {
        return new EventCategory(
                rs.getString("event_category_id"),
                EventCategoryName.of(rs.getString("event_category_name"))
        );
    }
}
