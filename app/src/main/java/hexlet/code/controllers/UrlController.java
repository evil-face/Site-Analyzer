package hexlet.code.controllers;

import hexlet.code.Url;
import hexlet.code.query.QUrl;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class UrlController implements CrudHandler {

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    @Override
    public void create(@NotNull Context ctx) {
        String userInput = ctx.formParam("url");
        try {
            URL url = new URL(userInput);
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL. Не забудьте про http или https");
            ctx.render("index.html");
        }
        ctx.consumeSessionAttribute("flash");
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String urlID) {

    }

    @Override
    public void getAll(@NotNull Context ctx) {
        List<Url> list = new QUrl().findList();
        ctx.attribute("urls", list);
        ctx.render("urls.html");
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String urlID) {

    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String urlID) {

    }
}
