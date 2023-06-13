package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UrlChecksController {

    public static Handler createCheck = ctx -> {
        int urlId = Integer.parseInt(ctx.pathParam("url-id"));

        Url url = new QUrl().id.eq(urlId).findOne();

        if (url == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not Found");
            return;
        }

        HttpResponse<String> response = null;
        try {
            response = Unirest.get(url.getName()).asString();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Сайт не существует");
            ctx.redirect("/urls/" + urlId);
            return;
        }

        String html = response.getBody();
        Document doc = Jsoup.parse(html);

        String title = doc.title();
        Element h1tag =  doc.select("h1").first();
        String h1 = h1tag == null ? "" : h1tag.text();
        Element descriptionTag = doc.select("meta[name='description']").first();
        String description = descriptionTag == null ? "" : descriptionTag.attr("content");

        UrlCheck newCheck = new UrlCheck();
        newCheck.setUrl(url);
        newCheck.setStatusCode((short) response.getStatus());
        newCheck.setTitle(title);
        newCheck.setH1(h1);
        newCheck.setDescription(description);
        newCheck.save();

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.redirect("/urls/" + urlId);
    };
}
