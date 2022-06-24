package com.qiu.activitydemo.config;

import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Configurable
public class ActivityConfig {


    @Bean
    public ProcessEngine processEngine() {
        return ProcessEngines.getDefaultProcessEngine();
    }

    @Bean
    @ConditionalOnBean(ProcessEngine.class)
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    @ConditionalOnBean(ProcessEngine.class)
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    @ConditionalOnBean(ProcessEngine.class)
    public TaskService taskService() {
        return processEngine().getTaskService();
    }

}
