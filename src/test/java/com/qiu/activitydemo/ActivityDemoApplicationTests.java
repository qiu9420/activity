package com.qiu.activitydemo;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

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

    /**
     * 流程定义查询
     */
    @Test
    public void queryProcessDefinitionTest() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.processDefinitionKey("myEvection").
                orderByProcessDefinitionId().desc().list();
        processDefinitions.forEach((processDefinition -> {
            log.info("流程定义Id：{}", processDefinition.getId());
            log.info("流程定义名称：{}", processDefinition.getName());
            log.info("流程定义版本：{}", processDefinition.getVersion());
            log.info("流程部署Id：{}", processDefinition.getDeploymentId());
        }));
    }

    /**
     * 测试通过挂起流程定义，进而挂起全部流程实例
     */
    @Test
    public void SuspendAllProcessInstance() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
                processDefinitionKey("myEvection").singleResult();
        boolean suspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId(); // 流程定义Id
        if (suspended) {    // 若suspended为true，则表示该流程定义为挂起状态，则进行唤醒
            // 通过repositoryService进行唤醒
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            log.info("流程定义：{} 已激活", processDefinitionId);
        } else {    // 若suspended为false，则表示该流程定义处于激活状态，则进行挂起操作
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            log.info("流程定义：{} 已挂起", processDefinitionId);
        }
    }

    /**
     * 通过流程实例对象，挂起或唤醒某个具体流程实例
     */
    @Test
    public void suspendSingleProcessInstance() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("2501").singleResult();
        boolean suspended = processInstance.isSuspended();
        String processInstanceId = processInstance.getId();
        if (suspended) {    // 若suspended为true，表示该流程实例处于挂起状态，则需要激活操作
            runtimeService.activateProcessInstanceById(processInstanceId);
            log.info("流程实例：{} 已激活", processInstance);
        } else {    // 若suspended为false，表示该流程实例处于激活状态，则需要挂起操作
            runtimeService.suspendProcessInstanceById(processInstanceId);
            log.info("流程实例：{} 已挂起", processInstanceId);
        }
    }


}
