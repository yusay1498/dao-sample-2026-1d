package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.EventCategory;
import com.example.event.api.domain.entity.vo.EventCategoryName;
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

// JdbcEventCategoryRepositoryを直接new構築して検証する（@InjectMocksは使用しない）
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
// db/02_data.sqlと同じく表示名（列挙子名ではない）で保持されている状態を再現する
@Sql(statements = "INSERT INTO event_categories (event_category_id, event_category_name) VALUES ('22222222-2222-2222-2222-222222222222', 'ライブ')")
class JdbcEventCategoryRepositoryTest {

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
    @DisplayName("event_category_nameが表示名で保持されている場合、findByIdは対応する列挙子に変換して返す")
    void givenDisplayNameStoredInDatabase_whenFindById_thenReturnEventCategoryWithMatchingEnum() {
        JdbcEventCategoryRepository jdbcEventCategoryRepository = new JdbcEventCategoryRepository(jdbcClient);

        Optional<EventCategory> actual = jdbcEventCategoryRepository.findById("22222222-2222-2222-2222-222222222222");

        assertThat(actual).isPresent();
        assertThat(actual.get().eventCategoryName()).isEqualTo(EventCategoryName.LIVE);
    }

    @Test
    @DisplayName("存在しないevent_category_idを指定した場合、findByIdは空を返す")
    void givenUnknownEventCategoryId_whenFindById_thenReturnEmpty() {
        JdbcEventCategoryRepository jdbcEventCategoryRepository = new JdbcEventCategoryRepository(jdbcClient);

        Optional<EventCategory> actual = jdbcEventCategoryRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }
}
