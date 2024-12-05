package ru.itmo.marketplace.service.review;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:data/users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DealReviewApiTest extends IntegrationEnvironment {

    @Autowired
    TestUtils testUtils;

    @Test
    void startUpTest() {
        assertTrue(true);
    }

}
