package com.rayleigh.activiti.Impl;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActivitiService {

    @Autowired
    private  RepositoryService repositoryService;
    @Autowired
    private  RuntimeService runtimeService;
    @Autowired
    private  TaskService taskService;
    @Autowired
    private  ProcessEngine processEngine;
    @Autowired
    private  HistoryService historyService;

    private static final String NEXTASSIGNEES = "assignees";//下一环节审批人
    private static final String OK = "ok";//审批同意操作
    private static final String REJECT = "reject";//审批退回操作
    private static final String REJECTTOSTART = "rejectToStart";//审批退回到开始环节操作
    private static final String ONGOING = "ongoing";//流程正在进行中状态
    private static final String FINISH = "finish";//流程结束状态
    private static final String ADDSIGN = "addSign";//加签
    private static final String RELAY = "relay";//加签


    /**
     * 部属流程
     * @author Thunder
     * @date 2019/1/3 9:35
     * @param bpmn claimCompanyActiviti.bpmn 文件名和后缀
     * @param name 流程名字(随意)
     * @param category 流程的类别(随意)
     * @return java.lang.String
     */
    public String deployProcess(String bpmn,String name,String category) {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(bpmn)
                .name(name)
                .category(category)
                .deploy();
        return deployment.getId();
    }
    /**
     *
     * 开始流程
     * 开始流程结束后还需要提交资料环节,并不是开始之后就到了审批环节
     * @author Thunder
     * @date 2019/1/3 9:35
     * @param processDefikey bpmn流程的id
     * @param userName 开始流程的操作人
     * @return 返回流程id 用于保存到业务的数据库和业务绑定在一起
     */
    public String startProcess(String processDefikey,String userName) {
        Map<String,Object> params = new HashMap<>();
        params.put("user",userName);
        //获取到流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefikey,params);
        String processInstanceId = processInstance.getId();
        return processInstanceId;

    }
    /**
     *
     * 根据审批人id查询个人任务或组任务
     * @author Thunder
     * @date 2018/7/14 17:22
     * @param assignee
     * @return java.util.List
     */
    public  List queryTask(String assignee){
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskCandidateOrAssigned(assignee).list();
        return list;
    }

    /**
     * 根据审批人和流程Id 查询任务Id
     * @author Thunder
     * @date 2018/7/13 9:44
     * @param assignee
     * @return java.util.List
     */
    public  String queryTaskId(String assignee,String processInstanceId){
        //创建一个任务查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskAssignee(assignee).list();
        if (list!=null&&list.size()>0){
            for (Task task : list){
                String id = task.getProcessInstanceId(); // 获取流程定义id
                if (processInstanceId.equals(id)){
                    String taskId = task.getId();
                    return taskId;
                }
            }
        }
        return "";
    }

    /**
     * 根据多审批人和流程Id 查询任务Id
     * @author Thunder
     * @date 2018/7/13 9:44
     * @param assignees
     * @return java.util.List
     */
    public  String queryTaskIdByAssignees(String assignees,String processInstanceId){
        String assignee[] = assignees.split(",");
        //创建一个任务查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        //获取其中一人的任务List
        List<Task> taskList = taskQuery.taskCandidateOrAssigned(assignee[0]).list();
        //如果任务List不为空
        if (taskList!=null && taskList.size()>0){
            for (Task task : taskList){
                //获取流程定义id
                String id = task.getProcessInstanceId();
                if (processInstanceId.equals(id)){
                    //获取任务id
                    String taskId = task.getId();
                    return taskId;
                }
            }
        }
        return "";
    }
    /**
     *
     * 完成任务
     * @author Thunder
     * @date 2018/7/13 9:18
     * @param taskId 任务id
     * @param assignees 下一环节审批人,可指定多个审批人,用逗号隔开.
     * @param assign 当前环节审批人姓名
     */
    public void compileTask(String taskId,String assignees,String assign){
        Map<String,Object> params = new HashMap<>();
        params.put("message",OK);
        //指定下一环节审批人
        params.put(NEXTASSIGNEES,assignees);
        //当前环节审批人
        taskService.setAssignee(taskId,assign);
        taskService.complete(taskId,params);
    }
    /**
     *
     * 退回任务
     * @author Thunder
     * @date 2018/7/13 9:39
     * @param taskId
     */
    public void rejectTask(String taskId,String assign){
        Map<String,Object> params = new HashMap<>();
        params.put("message",REJECT);
        taskService.setAssignee(taskId,assign);//指定当前审批人
        taskService.complete(taskId,params);
    }
    /**
     *
     * 退回到发起节点
     * @author Thunder
     * @date 2018/7/13 9:39
     * @param taskId
     */
    public String rejectTaskToStart(String taskId,String assign){
        Map<String,Object> params = new HashMap<>();
        params.put("message",REJECTTOSTART);
        taskService.setAssignee(taskId,assign);//指定当前审批人
        taskService.complete(taskId,params);
        return REJECTTOSTART;
    }
    /**
     *
     * 指定任务
     * @author Thunder
     * @date 2018/7/13 9:38
     * @param [taskId, assignee]
     */
//    public String specifyTask(String taskId,String assignee){
//        try {
//            Map<String,Object> params = new HashMap<>();
//            params.put("user",assignee);
//            taskService.complete(taskId,params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "OK";
//    }
    /**
     *
     * 删除流程
     * @author Thunder
     * @date 2018/7/13 9:38
     * @param processInstanceId
     */
    public Boolean delProcess(String processInstanceId){
        try {
            runtimeService.deleteProcessInstance(processInstanceId,"");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * 获取单个流程的状态
     * @author Thunder
     * @date 2018/7/14 11:19
     * @param processInstanceId
     * @return java.util.Map
     */
    public String getProcessInstanceState(String processInstanceId){
        //拿到流程实体的ID
        ProcessInstance pi = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String processState;
        if (pi!=null){
            processState= ONGOING;
            System.out.println("该流程实例"+processInstanceId+"正在运行...  "+"当前活动的任务:"+pi.getBusinessKey());
        }else {
            processState= FINISH;
            System.out.println("当前的流程实例"+processInstanceId+" 已经结束！");
        }
        return processState;
    }

    /**
     *
     * 挂起流程
     * @author Thunder
     * @date 2018/7/23 17:23
     * @param processInstanceId
     * @return java.lang.String
     */
    public Boolean suspendProcess(String processInstanceId){
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     *
     * 获取流程id
     * @author Thunder
     * @date 2018/7/30 14:37
     * @param assignee
     * @return java.util.List
     */
    public List getProcessInstanceId(String assignee,String processDefinitionKey){
        TaskQuery taskQuery = taskService.createTaskQuery();
        List<Task> list = taskQuery.taskCandidateOrAssigned(assignee).processDefinitionKey(processDefinitionKey)
                .list();
        List pidList = new ArrayList();
        for (Task t:list) {
            pidList.add(t.getProcessInstanceId());
        }

        return pidList;
    }

    /**
     *
     * 加签
     * @author Thunder
     * @date 2018/8/1 10:46
     * @param taskId, userId
     * @return void
     */
    public void addSign(String taskId,String assign){
        //设置流程变量key为type,value为addSign 为了让领取到这个任务的人晓得是通过加签派来的
        taskService.setVariable(taskId,"type",ADDSIGN);
        taskService.delegateTask(taskId, assign);
    }

    /**
     *
     * 转发
     * @author Thunder
     * @date 2018/8/1 10:46
     * @param taskId, userId
     * @return void
     */
    public void relay(String taskId,String assign){
        //设置流程变量key为type,value为addSign 为了让领取到这个任务的人晓得是通过加签派来的
        taskService.setVariable(taskId,"type",RELAY);
        taskService.delegateTask(taskId, assign);
    }
    /**
     *
     * 加签后回退给发起加签人
     * @author Thunder
     * @date 2018/8/1 11:20
     * @param taskId, userId, result result:agree同意,disagree不同意
     * @return void
     */
    public void returnTask(String taskId,String result){
        Map<String, Object> variables = new HashMap<>();
        variables.put("result", result);
        taskService.resolveTask(taskId,variables);
    }

    /**
     *
     * 查询流程变量
     * @author Thunder
     * @date 2018/8/1 11:43
     * @param
     * @return void
     */
    public String queryVariable(String taskId,String type){
        String result = (String) taskService.getVariable(taskId,type);
        return result;
    }

    /**
     *
     * 获取下一个节点id   节点id相当于权限名称  根据权限名称获取下一节点审批人
     * 这个方法仅限审批节点为UserTask节点
     * @author Thunder
     * @date 2018/8/9 16:27
     * @param processInstanceId
     * @return java.lang.String
     */
    public String getNextNode(String processInstanceId){

        String nowNode = null;
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        //查询当前节点的id
        for (Task task : tasks){
            nowNode = task.getTaskDefinitionKey();
        }
        // 获取历史流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel model = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        String result = null;
        List list = new ArrayList();
        if(model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for(FlowElement e : flowElements) {
                if (e.getClass().toString().contains("UserTask")){
                    list.add(e.getId());
                }
            }
            if (list.contains(nowNode)){
                int index = list.indexOf(nowNode);
                if (list.size()==index+1){
                    result = "没有下一个节点了";
                }else {
                    result = (String) list.get(index+1);
                }

            }
        }
        return result;
    }


    /**
     *
     * 获取当前审批环节
     * @author Thunder
     * @date 2018/8/20 19:41
     * @param processInstanceId
     * @return java.lang.String
     */
    public String getNode(String processInstanceId){
        String key = null;
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for (Task task : tasks){
            key= task.getTaskDefinitionKey();
        }
        return key;
    }
}
