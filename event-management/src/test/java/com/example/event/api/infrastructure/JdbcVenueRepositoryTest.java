package com.example.event.api.infrastructure;

import com.example.event.api.TestcontainersConfiguration;
import com.example.event.api.domain.entity.Venue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=always")
@Sql(statements = "INSERT INTO venues (venue_id, venue_name, capacity, address) VALUES ('11111111-1111-1111-1111-111111111111', 'テスト会場', 300, '東京都渋谷区')")
class JdbcVenueRepositoryTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    JdbcVenueRepository jdbcVenueRepository;

    @Test
    @DisplayName("存在するvenueIdを指定した場合、findByIdはその会場を返す")
    void givenExistingVenueId_whenFindById_thenReturnVenue() {
        Optional<Venue> actual = jdbcVenueRepository.findById(VENUE_ID);

        assertThat(actual).contains(new Venue(VENUE_ID, "テスト会場", 300, "東京都渋谷区"));
    }

    @Test
    @DisplayName("存在しないvenueIdを指定した場合、findByIdは空を返す")
    void givenUnknownVenueId_whenFindById_thenReturnEmpty() {
        Optional<Venue> actual = jdbcVenueRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }
}
