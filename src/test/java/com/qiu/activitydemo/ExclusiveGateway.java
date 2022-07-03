package com.qiu.activitydemo;

import com.qiu.activitydemo.bean.Evection;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
public class ExclusiveGateway {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 流程部署
     */
    @Test
    public void processDeploymentTest() {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("bpmn/myEvection-exclusiveGateway.bpmn")
                .addClasspathResource("bpmn/myEvection-exclusiveGateway.png").name("出差申请流程-排他网关")
                .key("myEvection-exclusiveGateway").deploy();
        log.info("流程部署Id：{}", deployment.getId());
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcess() {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee0", "甲2");
        params.put("assignee1", "乙2");
        params.put("assignee2", "丙2");
        params.put("assignee3", "丁2");
        Evection evection = new Evection();
        evection.setNum(2);
        params.put("evection", evection);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection-exclusiveGateway", params);
        log.info("流程实例Id：{}, 流程定义时的Id：{}, 流程定义时的key：{}, 流程名称：{}", processInstance.getId(), processInstance.getProcessDefinitionId(),
                processInstance.getProcessDefinitionKey(), processInstance.getName());
    }

    /**
     * 完成出差申请任务
     */
    @Test
    public void submitApplyTest() {
        Task task = taskService.createTaskQuery().taskAssignee("甲2").processDefinitionKey("myEvection-exclusiveGateway").taskId("12512").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成部门经理审批
     */
    @Test
    public void bmCompleteTaskTest() {
        Task task = taskService.createTaskQuery().taskAssignee("乙2").processDefinitionKey("myEvection-exclusiveGateway").taskId("15002").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }
}
