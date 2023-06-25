package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class UrlControllerTest {
    private final String firstPageExistingUrl = "http://yandex.ru";
    private final String secondPageExistingUrl = "http://secondpage.org";

    @BeforeAll
    void beforeAll() {
        Javalin app = App.getApp();
        app.start(0);
        int port = app.port();
        Unirest.config().defaultBaseUrl("http://localhost:" + port);
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
        String userInput = "http://www.uniquesite.com/path";
        HttpResponse<String> response1 = Unirest.post("/urls")
                .field("url", userInput)
                .asString();
        Url actual = new QUrl().name.eq("http://uniquesite.com").findOne();

        assertThat(actual).isNotNull();
        assertThat(response1.getStatus()).isEqualTo(HttpStatus.FOUND.getCode());

        String redirectedPath = response1.getHeaders().getFirst("Location");
        HttpResponse<String> afterRedirect = Unirest.get(redirectedPath).asString();
        assertThat(afterRedirect.getBody()).contains("Страница успешно добавлена");

        HttpResponse<String> response2 = Unirest.get("/urls?page=2").asString();
        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getBody()).contains(
                String.valueOf(actual.getId()),
                actual.getName());

        actual.delete();
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
        List<Url> listBefore = new QUrl().findList();
        HttpResponse<String> response = Unirest.post("/urls")
                .field("url", secondPageExistingUrl)
                .asString();
        List<Url> listAfter = new QUrl().findList();

        assertThat(listBefore).hasSize(listAfter.size());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.getBody()).contains("Страница уже существует");
    }

    @Test
    void testDeleteCorrect() {
        List<Url> expectedList = new QUrl().findList();

        Url deleteCandidate = new Url();
        deleteCandidate.save();
        long deleteCandidateId = deleteCandidate.getId();
        deleteCandidate.setName("http://gonnadelete.com");
        deleteCandidate.save();

        HttpResponse<String> response = Unirest.delete("/urls/" + deleteCandidateId).asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.getCode());

        List<Url> actualList = new QUrl().findList();
        assertThat(actualList).hasSameSizeAs(expectedList);
    }

    @Test
    void testDeleteIncorrect() {
        String expected = "String ID must be numeric";
        HttpResponse<String> response = Unirest.delete("/urls/badreq").asString();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    void testUpdateCorrect() {
        Url oldUrl = new Url("http://oldname.com");
        oldUrl.save();

        String updatedAddress = "http://updated.com";

        HttpResponse<String> response1 = Unirest.patch("/urls/" + oldUrl.getId())
                .body("{\"name\": \"" + updatedAddress + "\"}")
                .asString();


        Url actual = new QUrl().id.eq(oldUrl.getId()).findOne();

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.FOUND.getCode());
        assertThat(new QUrl().name.eq("http://oldname.com").findOne()).isNull();
        assertThat(actual.getName()).isEqualTo(updatedAddress);

        String redirectedPath = response1.getHeaders().getFirst("Location");
        HttpResponse<String> afterRedirect = Unirest.get(redirectedPath).asString();
        assertThat(afterRedirect.getBody()).contains("Страница успешно обновлена");

        oldUrl.delete();
    }
}
