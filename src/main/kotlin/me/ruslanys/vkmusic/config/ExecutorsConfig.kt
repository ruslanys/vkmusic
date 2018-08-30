package me.ruslanys.vkmusic.config

import me.ruslanys.vkmusic.property.DownloadProperties
import me.ruslanys.vkmusic.service.PropertyService
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class ExecutorsConfig(private val propertyService: PropertyService) : AsyncConfigurer, ApplicationListener<ContextStoppedEvent> {

    @Bean(name = ["asyncExecutor"])
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 3
        executor.setThreadNamePrefix("AsyncExecutor-")
        executor.setAwaitTerminationSeconds(10)
        executor.initialize()

        return executor
    }

    @Bean(name = ["downloadExecutor"])
    fun getDownloadExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = propertyService.get(DownloadProperties::class.java)!!.poolSize
        executor.setThreadNamePrefix("DownloadExecutor-")
        executor.initialize()

        return executor
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler =
            AsyncUncaughtExceptionHandler { throwable, _, _ ->
                LoggerFactory.getLogger("Async").error("Async error", throwable)
            }

    override fun onApplicationEvent(event: ContextStoppedEvent) {
        val executors = event.applicationContext.getBeansOfType(ThreadPoolTaskExecutor::class.java)
        for (entry in executors) {
            entry.value.shutdown()
        }
    }

}
