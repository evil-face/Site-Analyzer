package hexlet.code;

import io.ebean.DB;
import io.javalin.Javalin;

public class App {

    private static final String DEFAULT_PORT = "8093";
    private static final String DEV_MODE = "dev";
    private static final String PROD_MODE = "prod";
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", DEV_MODE);
    }

    private static boolean isProduction() {
        return getMode().equals(PROD_MODE);
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.plugins.enableDevLogging();
            }
        });

        app.get("/", ctx -> ctx.result("Hello World"));

        return app;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Javalin app = getApp();
        app.start(getPort());

        Url testUrl = new Url();
        testUrl.setName("testUrl");
        DB.save(testUrl);
    }
}
