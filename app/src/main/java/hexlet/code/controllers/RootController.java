package hexlet.code.controllers;

import io.javalin.http.Handler;

public class RootController {
    public static Handler getIndex = ctx -> {
        ctx.render("index.html");
    };
}
