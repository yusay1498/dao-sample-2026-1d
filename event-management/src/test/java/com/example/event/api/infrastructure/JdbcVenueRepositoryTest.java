package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.Venue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// JdbcVenueRepositoryを直接new構築して検証する（@InjectMocksは使用しない）
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Sql(statements = "INSERT INTO venues (venue_id, venue_name, capacity, address) VALUES ('11111111-1111-1111-1111-111111111111', 'テスト会場', 300, '東京都渋谷区')")
class JdbcVenueRepositoryTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    JdbcClient jdbcClient;

    @Container
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));

    @BeforeAll
    static void startContainers() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // schema.sqlを適用するために必要
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Test
    @DisplayName("存在するvenueIdを指定した場合、findByIdはその会場を返す")
    void givenExistingVenueId_whenFindById_thenReturnVenue() {
        JdbcVenueRepository jdbcVenueRepository = new JdbcVenueRepository(jdbcClient);

        Optional<Venue> actual = jdbcVenueRepository.findById(VENUE_ID);

        assertThat(actual).contains(new Venue(VENUE_ID, "テスト会場", 300, "東京都渋谷区"));
    }

    @Test
    @DisplayName("存在しないvenueIdを指定した場合、findByIdは空を返す")
    void givenUnknownVenueId_whenFindById_thenReturnEmpty() {
        JdbcVenueRepository jdbcVenueRepository = new JdbcVenueRepository(jdbcClient);

        Optional<Venue> actual = jdbcVenueRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }
}

