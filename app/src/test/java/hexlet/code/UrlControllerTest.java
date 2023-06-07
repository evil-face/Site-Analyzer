package hexlet.code;

import hexlet.code.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class UrlControllerTest {
    private Transaction transaction;
    private final String firstPageExistingUrl = "http://yandex.ru";
    private final String secondPageExistingUrl = "http://secondpage.org";

    @BeforeAll
    void beforeAll() {
        Javalin app = App.getApp();
        app.start(0);
        int port = app.port();
        Unirest.config().defaultBaseUrl("http://localhost:" + port);
    }

    @BeforeEach
    void setUp() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        transaction.rollback();
    }

    @Test
    void testGetAllDefaultPage() {
        Url expectedOne = new QUrl().id.eq(1).findOne();
        Url expectedTwo = new QUrl().id.eq(2).findOne();

        HttpResponse<String> response = Unirest.get("/urls").asString();
        assertThat(response.getStatus()).isEqualTo(200);

        assertThat(response.getBody()).contains(
                "Сайты",
                "Назад",
                "Вперед");

        assertThat(response.getBody()).contains(
                expectedOne.getName(),
                String.valueOf(expectedOne.getId()));

        assertThat(response.getBody()).contains(
                expectedTwo.getName(),
                String.valueOf(expectedTwo.getId()));

        assertThat(response.getBody()).doesNotContain(secondPageExistingUrl);
    }

    @Test
    void testGetAllSecondPage() {
        HttpResponse<String> response = Unirest.get("/urls")
                .queryString("page", 2)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);

        assertThat(response.getBody()).contains(secondPageExistingUrl);

        assertThat(response.getBody()).doesNotContain(
                firstPageExistingUrl);
    }

    @Test
    void testGetOneCorrect() {
        Url expected = new QUrl().name.eq(secondPageExistingUrl).findOne();

        HttpResponse<String> response = Unirest.get("/urls/" + expected.getId())
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);

        assertThat(response.getBody()).contains(
                "Дата создания",
                "Проверки",
                "Запустить проверку");

        assertThat(response.getBody()).contains(
                String.valueOf(expected.getId()),
                expected.getName());
    }

    @Test
    void testGetOneBadRequest() {
        String expected = "String ID must be numeric";
        HttpResponse<String> response = Unirest.get("/urls/badreq").asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void testGetOneNotFound() {
        String expected = "Not Found";
        HttpResponse<String> response = Unirest.get("/urls/12345").asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void testCreateCorrect() {
        String userInput1 = "http://www.uniquesite.com/path";
        HttpResponse<String> response1 = Unirest.post("/urls")
                .field("url", userInput1)
                .asString();
        Url actual1 = new QUrl().name.eq("http://uniquesite.com").findOne();

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(actual1).isNotNull();

        assertThat(response1.getBody()).contains("Страница успешно добавлена");

        String userInput2 = "https://www.uniquesite.com/path";
        HttpResponse<String> response2 = Unirest.post("/urls")
                .field("url", userInput2)
                .asString();
        Url actual2 = new QUrl().name.eq("http://uniquesite.com").findOne();

        assertThat(response2.getStatus()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(actual2).isNotNull();

        assertThat(response2.getBody()).contains("Страница успешно добавлена");
    }

    @Test
    void testCreateBadRequestBlank() {
        String userInput = " ";
        List<Url> listBefore = new QUrl().findList();
        HttpResponse<String> response = Unirest.post("/urls")
                .field("url", userInput)
                .asString();
        List<Url> listAfter = new QUrl().findList();
        Url actual = new QUrl().name.eq(userInput).findOne();

        assertThat(listBefore).hasSize(listAfter.size());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(actual).isNull();
        assertThat(response.getBody()).contains("Некорректный URL");
    }

    @Test
    void testCreateBadRequestMalformed() {
        String userInput = "www.malformed.com";
        List<Url> listBefore = new QUrl().findList();
        HttpResponse<String> response = Unirest.post("/urls")
                .field("url", userInput)
                .asString();
        List<Url> listAfter = new QUrl().findList();
        Url actual = new QUrl().name.eq(userInput).findOne();

        assertThat(listBefore).hasSize(listAfter.size());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(actual).isNull();
        assertThat(response.getBody()).contains("Некорректный URL");
    }

    @Test
    void testCreateExistingEntity() {
        String userInput = secondPageExistingUrl;
        List<Url> listBefore = new QUrl().findList();
        HttpResponse<String> response = Unirest.post("/urls")
                .field("url", userInput)
                .asString();
        List<Url> listAfter = new QUrl().findList();

        assertThat(listBefore).hasSize(listAfter.size());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.getBody()).contains("Страница уже существует");
    }
}
