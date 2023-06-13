package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class UrlChecksControllerTest {
    @BeforeAll
    void beforeAll() {
        Javalin app = App.getApp();
        app.start(0);
        int port = app.port();
        Unirest.config().defaultBaseUrl("http://localhost:" + port);
    }

    @Test
    void createCheckCorrect() throws IOException {
        String testHtml = readTestFixture();
        try (MockWebServer server = new MockWebServer()) {
            MockResponse mockedResponse = new MockResponse()
                    .addHeader("Content-Type", "text/html; charset=UTF-8")
                    .setBody(testHtml);
            server.enqueue(mockedResponse);
            server.start();
            String mockedUrl = server.url("/").toString();

            Url expected = new Url();
            expected.save();
            long expectedId = expected.getId();
            expected.setName(mockedUrl);
            expected.save();

            HttpResponse<String> response = Unirest.post("/urls/" + expectedId + "/checks")
                    .asString();
            assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.getCode());

            String redirectedPath = response.getHeaders().getFirst("Location");
            HttpResponse<String> finalResponse = Unirest.get(redirectedPath).asString();

            UrlCheck actual = new QUrlCheck().url.eq(expected).findOne();

            assertThat((int) actual.getStatusCode()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(actual.getTitle()).isEqualTo("Test Title");
            assertThat(actual.getH1()).isEqualTo("Test h1");
            assertThat(actual.getDescription()).isEqualTo("Test description");

            assertThat(finalResponse.getStatus()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(finalResponse.getBody())
                    .contains(actual.getH1(), actual.getTitle(), "Проверка запущена")
                    .doesNotContain("Wrong text", "Test text");

            expected.delete();
        }
    }

    @Test
    void createCheckUrlNotFoundInDB() {
        HttpResponse<String> response = Unirest.post("/urls/" + 12345 + "/checks")
                .asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    private String readTestFixture() throws IOException {
        Path path = Paths.get("src/test/resources/test.html").toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new IOException("File '" + path + "' does not exist");
        }
        return Files.readString(path);
    }
}
