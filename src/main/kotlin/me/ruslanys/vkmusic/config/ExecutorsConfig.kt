package me.ruslanys.vkmusic.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executor

@EnableScheduling
@EnableAsync
@Configuration
class ExecutorsConfig : SchedulingConfigurer, AsyncConfigurer {

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.poolSize = 5
        taskScheduler.setThreadNamePrefix("ScheduledExecutor-")
        taskScheduler.initialize()

        taskRegistrar.setTaskScheduler(taskScheduler)
    }

    @Bean(name = ["asyncExecutor"])
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.setThreadNamePrefix("AsyncExecutor-")
        executor.initialize()

        return executor
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler =
            AsyncUncaughtExceptionHandler { throwable, _, _ ->
                LoggerFactory.getLogger("Async").error("Async error", throwable)
            }

}
