package me.ruslanys.vkmusic.config;

import javafx.fxml.FXMLLoader;
import me.ruslanys.vkmusic.annotation.FxmlController;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 * @author http://mruslan.com
 */
@Configuration
public class FxmlConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor() {
        return new RegistryPostProcessor();
    }

    public static class RegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

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

                    Object view = loader.getRoot();
                    Object controller = loader.getController();

                    beanFactory.registerSingleton(viewName, view);
                    beanFactory.registerSingleton(controllerName, controller);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(FxmlController.class);
        AutowireCapableBeanFactory factory = event.getApplicationContext().getAutowireCapableBeanFactory();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            factory.autowireBean(entry.getValue());
            factory.initializeBean(entry.getValue(), entry.getKey());
        }
    }

}
