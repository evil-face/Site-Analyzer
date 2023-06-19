package hexlet.code;

import hexlet.code.controllers.UrlChecksController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.post;

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

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine engine = new TemplateEngine();
        engine.addDialect(new LayoutDialect());
        engine.addDialect(new Java8TimeDialect());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        engine.addTemplateResolver(templateResolver);

        return engine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.routes(() -> {
            get("urls/{url-id}/update", UrlController.updateUrl);
            post("urls/{url-id}/checks", UrlChecksController.createCheck);
            crud("urls/{url-id}", new UrlController());
        });
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.plugins.enableDevLogging();
            }
            JavalinThymeleaf.init(getTemplateEngine());
            config.staticFiles.enableWebjars();
            config.staticFiles.add("/static");
        });

        addRoutes(app);
        app.before(ctx -> ctx.attribute("ctx", ctx));

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
