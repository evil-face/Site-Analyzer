package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;


public final class UrlController implements CrudHandler {
    public static Handler showUrlEditPage = ctx -> {
        String urlID = ctx.pathParam("url-id");

        if (!isIdNumeric(ctx, urlID)) {
            return;
        }

        Url url = getUrlById(urlID);
        if (isUrlNull(ctx, url)) {
            return;
        }

        ctx.attribute("url", url);
        ctx.render("edit.html");
    };

    @Override
    public void create(@NotNull Context ctx) {
        String userInput = ctx.formParam("url");
        URL url = null;

        try {
            url = new URL(userInput);
        } catch (MalformedURLException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.sessionAttribute("flash", "Некорректный URL. Не забудьте указать \"http\" или \"https\"");
            ctx.attribute("url", userInput);
            ctx.render("index.html");
            return;
        }

        Url newUrl = getNormalisedUrl(url);
        Optional<Url> existingUrl = getUrlByName(newUrl);

        if (existingUrl.isPresent()) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.attribute("url", userInput);
            ctx.render("index.html");
            return;
        }

        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls");
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String urlID) {
        if (!isIdNumeric(ctx, urlID)) {
            return;
        }

        Url url = getUrlById(urlID);
        if (isUrlNull(ctx, url)) {
            return;
        }

        url.delete();
        ctx.sessionAttribute("flash", "Страница успешно удалена");
        ctx.redirect("/urls");
    }

    @Override
    public void getAll(@NotNull Context ctx) {
        final int urlsPerPage = 10;
        String pageQuery = ctx.queryParam("page");
        int pageNum = pageQuery == null ? 1 : Integer.parseInt(pageQuery);
        ctx.attribute("pageNum", pageNum);

        PagedList<Url> pagedUrls = getPagedList(urlsPerPage, pageNum);
        List<Url> list = pagedUrls.getList();
        ctx.attribute("urls", list);
        ctx.render("urls.html");
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String urlID) {
        if (!isIdNumeric(ctx, urlID)) {
            return;
        }

        Url url = getUrlById(urlID);
        if (isUrlNull(ctx, url)) {
            return;
        }
        List<UrlCheck> checks = url.getUrlChecks();

        ctx.attribute("checks", checks);
        ctx.attribute("url", url);
        ctx.render("url.html");
    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String urlID) {
        if (!isIdNumeric(ctx, urlID)) {
            return;
        }

        Url oldUrl = getUrlById(urlID);
        if (isUrlNull(ctx, oldUrl)) {
            return;
        }

        Url updatedUrl = ctx.bodyAsClass(Url.class);
        URL updatedRaw = null;

        try {
            updatedRaw = new URL(updatedUrl.getName());
        } catch (MalformedURLException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.sessionAttribute("flash", "Некорректный URL. Не забудьте указать \"http\" или \"https\"");
            ctx.attribute("url", updatedUrl);
            ctx.redirect("/urls/" + urlID + "/edit");
            return;
        }

        oldUrl.setName(getNormalisedUrl(updatedRaw).getName());
        oldUrl.save();

        ctx.sessionAttribute("flash", "Страница успешно обновлена");
        ctx.redirect("/urls");
    }

    @Nullable
    private static Url getUrlById(@NotNull String urlID) {
        return new QUrl().id.eq(Integer.parseInt(urlID)).findOne();
    }

    @NotNull
    private static Optional<Url> getUrlByName(Url newUrl) {
        return new QUrl().name.eq(newUrl.getName()).findOneOrEmpty();
    }

    @NotNull
    private static PagedList<Url> getPagedList(int urlsPerPage, int pageNum) {
        return new QUrl()
                .setFirstRow((pageNum - 1) * urlsPerPage)
                .setMaxRows(urlsPerPage)
                .findPagedList();
    }

    @NotNull
    private static Url getNormalisedUrl(URL url) {
        String normalisedUrl = url.getProtocol() + "://" + url.getAuthority();

        if (normalisedUrl.startsWith("http://www.")
                || normalisedUrl.startsWith("https://www.")) {
            normalisedUrl = normalisedUrl.replace("www.", "");
        }

        return new Url(normalisedUrl);
    }

    private static boolean isIdNumeric(Context ctx, String id) {
        if (id.matches("\\d+")) {
            return true;
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("String ID must be numeric");
            return false;
        }
    }

    private static boolean isUrlNull(Context ctx, Url url) {
        if (url == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Not Found");
            return true;
        } else {
            return false;
        }
    }
}
