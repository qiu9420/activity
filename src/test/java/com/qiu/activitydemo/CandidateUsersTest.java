package com.qiu.activitydemo;

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
public class CandidateUsersTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    public void deploymentTest() {
        // 部署流程
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("bpmn/myEvection-candidateUsers.bpmn")
                .addClasspathResource("bpmn/myEvection-candidateUsers.png")
                .name("出差申请流程-组任务").key("myEvection-candidateUsers").deploy();
        log.info("流程部署Id：{}", deployment.getId());
    }

    @Test
    public void startProcessByKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee0", "甲");
        params.put("assignee1", "乙");
        params.put("assignee2", "丙");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection-candidateUsers", params);
        log.info("流程实例Id：{}, 流程名称：{}, 流程Key：{}, 流程定义Id：{}", processInstance.getId(), processInstance.getName(),
                processInstance.getProcessDefinitionKey(), processInstance.getProcessDefinitionId());
    }

    /**
     * 查询待办任务并完成
     */
    @Test
    public void queryTaskAndFinished() {
        Task task = taskService.createTaskQuery().taskAssignee("甲").processDefinitionKey("myEvection-candidateUsers").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            log.info("{} 完成了 {} 任务", task.getAssignee(), task.getName());
        }

    }

    /**
     * 组任务的候选人拾取任务
     */
    @Test
    public void pickupTaskTest() {
        // 根据任务Id和候选人名称查询代办任务
        String userName = "王五";
        Task task = taskService.createTaskQuery().taskId("5002").taskCandidateUser(userName).singleResult();
        log.info("组任务候选人：{}的代办任务：{}", userName, task);
        // 拾取任务
        if (task != null) {
            taskService.claim(task.getId(), userName);
            log.info("候选人：{}, 拾取了任务：{}", userName, task);
        }
    }

    /**
     * 拾取任务后归还任务
     */
    @Test
    public void setAssigneeToGroupTask() {
        String userName = "王五";
        Task task = taskService.createTaskQuery().taskId("5002").taskAssignee(userName).singleResult();
        if (task != null) {
            taskService.setAssignee(task.getId(), null);
        }
    }

}
