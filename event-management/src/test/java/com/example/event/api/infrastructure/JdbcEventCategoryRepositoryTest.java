package com.example.event.api.infrastructure;

import com.example.event.api.TestcontainersConfiguration;
import com.example.event.api.domain.entity.EventCategory;
import com.example.event.api.domain.entity.vo.EventCategoryName;
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
// db/02_data.sqlと同じく表示名（列挙子名ではない）で保持されている状態を再現する
@Sql(statements = "INSERT INTO event_categories (event_category_id, event_category_name) VALUES ('22222222-2222-2222-2222-222222222222', 'ライブ')")
class JdbcEventCategoryRepositoryTest {

    @Autowired
    JdbcEventCategoryRepository jdbcEventCategoryRepository;

    @Test
    @DisplayName("event_category_nameが表示名で保持されている場合、findByIdは対応する列挙子に変換して返す")
    void givenDisplayNameStoredInDatabase_whenFindById_thenReturnEventCategoryWithMatchingEnum() {
        Optional<EventCategory> actual = jdbcEventCategoryRepository.findById("22222222-2222-2222-2222-222222222222");

        assertThat(actual).isPresent();
        assertThat(actual.get().eventCategoryName()).isEqualTo(EventCategoryName.LIVE);
    }

    @Test
    @DisplayName("存在しないevent_category_idを指定した場合、findByIdは空を返す")
    void givenUnknownEventCategoryId_whenFindById_thenReturnEmpty() {
        Optional<EventCategory> actual = jdbcEventCategoryRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }
}
