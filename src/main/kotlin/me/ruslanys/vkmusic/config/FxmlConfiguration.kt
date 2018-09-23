package me.ruslanys.vkmusic.config

import javafx.fxml.FXMLLoader
import me.ruslanys.vkmusic.annotation.FxmlController
import me.ruslanys.vkmusic.controller.BaseController
import org.reflections.Reflections
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import java.util.regex.Pattern

/**
 * This approach lets initialize controllers by the FXML loader.
 * And then, generates BeansDefinition at Spring context as well as registering and injecting created beans.
 *
 * So, the order of initialization is:
 * 1. Creating controllers by FXML loader.
 * 2. Registering BeansDefinitions.
 * 3. Initializing Spring Context.
 * 4. Injecting controllers dependencies.
 */
@Deprecated("This approach is complex and worse in the order of beans initialization")
class FxmlFirstConfiguration : ApplicationListener<ContextRefreshedEvent> {

    class RegistryPostProcessor : BeanDefinitionRegistryPostProcessor {

        override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
            // No needs to override this behavior
        }

        override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
            val reflections = Reflections("me.ruslanys.vkmusic")
            val fxmlControllers = reflections.getTypesAnnotatedWith(FxmlController::class.java)
            val pattern = Pattern.compile("(\\w+)\\.fxml$")

            for (fxmlController in fxmlControllers) {
                val annotation = fxmlController.getAnnotation(FxmlController::class.java)
                val viewPath = annotation.view

                val matcher = pattern.matcher(viewPath)
                if (!matcher.find()) {
                    throw RuntimeException("Incorrect view path.")
                }
                val prefix = matcher.group(1)

                javaClass.classLoader.getResourceAsStream(viewPath).use { fxmlStream ->
                    val loader = FXMLLoader()
                    loader.load<Any>(fxmlStream)

                    val viewName = prefix + "View"
                    val controllerName = prefix + "FxmlController"

                    val view = loader.getRoot<Any>()
                    val controller = loader.getController<Any>()

                    beanFactory.registerSingleton(viewName, view)
                    beanFactory.registerSingleton(controllerName, controller)
                }
            }

        }

    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val beans = event.applicationContext.getBeansWithAnnotation(FxmlController::class.java)
        val factory = event.applicationContext.autowireCapableBeanFactory

        for ((key, value) in beans) {
            factory.autowireBean(value)
            factory.initializeBean(value, key)
        }
    }

    companion object {
        @Bean
        @JvmStatic
        fun beanDefinitionRegistryPostProcessor(): BeanDefinitionRegistryPostProcessor {
            return RegistryPostProcessor()
        }
    }

}

/**
 * This approach lets initialize controllers by the Spring IoC.
 * And then, initialize them with FXML context, when Spring Context is ready.
 *
 * This approach is recommended by me :)
 *
 * Limitations:
 * 1. Do not setup fx:controller field at FXML markup.
 */
@Configuration
class FxmlConfiguration : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val beans = event.applicationContext.getBeansWithAnnotation(FxmlController::class.java)
        val pattern = Pattern.compile("(\\w+)\\.fxml$")

        for (controller in beans.values) {
            val annotation = controller::class.java.getAnnotation(FxmlController::class.java)
            val viewPath = annotation.view

            val matcher = pattern.matcher(viewPath)
            if (!matcher.find()) {
                throw RuntimeException("Incorrect view path.")
            }

            javaClass.classLoader.getResourceAsStream(viewPath).use { fxmlStream ->
                val loader = FXMLLoader()
                loader.setController(controller)
                loader.load<Any>(fxmlStream)

                if (controller is BaseController) {
                    controller.rootView = loader.getRoot()
                }
            }
        }
    }

}