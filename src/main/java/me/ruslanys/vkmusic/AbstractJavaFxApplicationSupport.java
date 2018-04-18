package me.ruslanys.vkmusic;

import javafx.application.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class AbstractJavaFxApplicationSupport extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(me.ruslanys.vkmusic.Application.class)
                .headless(false)
                .run(savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass, String[] args) {
        AbstractJavaFxApplicationSupport.savedArgs = args;
        Application.launch(appClass, args);
    }
}
