package hexlet.code;

import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AppTest {

    @BeforeAll
    void setUp() {
        Javalin app = App.getApp();
        app.start(0);
        int port = app.port();
        Unirest.config().defaultBaseUrl("http://localhost:" + port);
    }

    @Test
    void testServerStartup() {
        HttpResponse<String> response = Unirest.get("/").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(
                "Анализатор",
                "Добавленные сайты",
                "Проверить");
    }
}
