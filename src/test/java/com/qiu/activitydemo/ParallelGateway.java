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

@SpringBootTest
@Slf4j
public class ParallelGateway {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 流程部署
     */

    @Test
    public void processDeploymentTest() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/myEvection-parallelGateway.bpmn")
                .addClasspathResource("bpmn/myEvection-parallelGateway.png")
                .name("出差申请流程-并行网关")
                .key("myEvection-parallelGateway").deploy();
        log.info("部署Id：{}", deployment.getId());
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcessTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee0", "七七");
        params.put("assignee1", "凌华");
        params.put("assignee2", "夜兰");
        params.put("assignee3", "迪奥娜");
        Evection evection = new Evection();
        evection.setNum(4);
        params.put("evection", evection);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection-parallelGateway", params);
        log.info("流程Id：{}, 流程定义Id：{}, 流程名称：{}, 流程key：{}", processInstance.getId(),
                processInstance.getProcessDefinitionId(), processInstance.getName(), processInstance.getProcessDefinitionKey());
    }

    /**
     * 完成提交任务
     */
    @Test
    public void applyCompleteTest() {
        // 查询任务
        Task task = taskService.createTaskQuery().taskAssignee("七七").processDefinitionKey("myEvection-parallelGateway").taskId("2512").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成技术经理审批任务
     */
    @Test
    public void technicalManagerCompleteTask() {
        Task task = taskService.createTaskQuery().taskAssignee("凌华").processDefinitionKey("myEvection-parallelGateway").taskId("5004").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成项目经理审批任务
     */
    @Test
    public void pmCompleteTask() {
        Task task = taskService.createTaskQuery().taskAssignee("夜兰").processDefinitionKey("myEvection-parallelGateway").taskId("5007").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }
    /**
     * 完成总经理审批任务
     */
    @Test
    public void gmCompleteTask() {
        Task task = taskService.createTaskQuery().taskAssignee("迪奥娜").processDefinitionKey("myEvection-parallelGateway").taskId("10004").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }




}