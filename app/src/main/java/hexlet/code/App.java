package hexlet.code;

import io.javalin.Javalin;

public class App {
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8093");
        return Integer.parseInt(port);
    }

    public static Javalin getApp() {
        return Javalin.create(config -> config.plugins.enableDevLogging())
                .get("/", ctx -> ctx.result("Hello World"));
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
