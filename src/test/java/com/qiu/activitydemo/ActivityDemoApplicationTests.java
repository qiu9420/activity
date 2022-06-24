package com.qiu.activitydemo;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ActivityDemoApplicationTests {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    /**
     * 测试获取流程引擎
     */
    @Test
    void contextLoads() {
        // 1、通过ProcessEngines获取默认流程引擎，获取流程引擎时自动创建activity相关数据表
        System.out.println(processEngine);
    }


    /**
     * 测试部署流程
     */
    @Test
    public void processInstanceDeploymentTest() {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("bpmn/myEvection.bpmn")
                .addClasspathResource("bpmn/myEvection.png").key("myEvection").name("出差申请流程").deploy();
        log.info("流程部署Id：{}, 流程部署key：{}", deployment.getId(), deployment.getKey());
    }


    /**
     * 启动出差申请流程
     */
    @Test
    public void startProcessTest() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection");
        log.info("流程实例Id：{},流程实例名称：{}, 流程定义Id：{}", processInstance.getProcessInstanceId(),
                processInstance.getName(), processInstance.getProcessDefinitionId());

    }

    /**
     * 查询个人待办任务，并完成
     */
    @Test
    public void queryTaskAndComplete() {
        Task task = taskService.createTaskQuery().processDefinitionKey("myEvection").taskAssignee("ZhangSan")
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            log.info("{}完成了{}任务,任务Id：{},流程实例Id：", task.getAssignee(), task.getName(), task.getProcessInstanceId());
        }
    }
}
