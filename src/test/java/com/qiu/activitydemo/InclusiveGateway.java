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
public class InclusiveGateway {

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
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/myEvection-inclusiveGateway.bpmn")
                .addClasspathResource("bpmn/myEvection-inclusiveGateway.png")
                .name("出差申请流程-包含网关")
                .key("myEvection-inclusiveGateway").deploy();
        log.info("流程部署Id：{}", deployment.getId());
    }

    /**
     * 流程启动
     */
    @Test
    public void startProcessTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("assignee0", "温迪");
        params.put("assignee1", "钟离");
        params.put("assignee2", "影");
        params.put("assignee3", "刻晴");
        params.put("assignee4", "胡桃");
        Evection evection = new Evection();
        evection.setNum(6);
        params.put("evection", evection);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection-inclusiveGateway", params);
        log.info("流程Id：{}, 流程名称：{}, 流程定义key：{}, 流程定义Id：{}", processInstance.getId(),
                processInstance.getName(), processInstance.getProcessDefinitionKey(), processInstance.getProcessDefinitionId());
    }

    /**
     * 完成提交任务
     */
    @Test
    public void applyCompleteTaskTest() {
        Task task = taskService.createTaskQuery().taskAssignee("温迪").processDefinitionKey("myEvection-inclusiveGateway")
                .taskId("17513").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成人事经理审批任务
     */
    @Test
    public void hrManagerCompleteTask() {
        Task task = taskService.createTaskQuery().taskAssignee("影").processDefinitionKey("myEvection-inclusiveGateway")
                .taskId("20004").singleResult();
        if (task != null) {
            taskService.complete(task.getId());
        }
    }
}
