package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public final class UrlTest {
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        transaction.rollback();
    }

    @Test
    void testUrlCreation() {
        Url expected = new Url("testName2");
        expected.save();
        Url actual = new QUrl().id.eq(expected.getId()).findOne();

        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getId()).isGreaterThan(0);
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getCreatedAt()).isBefore(Instant.now());
    }
}
