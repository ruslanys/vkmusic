package me.ruslanys.vkmusic.config;

import javafx.fxml.FXMLLoader;
import me.ruslanys.vkmusic.annotation.FxmlController;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 * @author http://mruslan.com
 */
@Configuration
public class FxmlConfiguration implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Reflections reflections = new Reflections("me.ruslanys.vkmusic");
        Set<Class<?>> fxmlControllers = reflections.getTypesAnnotatedWith(FxmlController.class);
        Pattern pattern = Pattern.compile("(\\w+)\\.fxml$");

        for (Class<?> fxmlController : fxmlControllers) {
            FxmlController annotation = fxmlController.getAnnotation(FxmlController.class);
            String viewPath = annotation.view();

            Matcher matcher = pattern.matcher(viewPath);
            if (!matcher.find()) {
                throw new RuntimeException("Incorrect view path.");
            }
            String prefix = matcher.group(1);

            try (InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream(viewPath)) {
                FXMLLoader loader = new FXMLLoader();
                loader.load(fxmlStream);

                String viewName = prefix + "View";
                String controllerName = prefix + "FxmlController";

                beanFactory.registerSingleton(viewName, loader.getRoot());
                beanFactory.registerSingleton(controllerName, loader.getController());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
