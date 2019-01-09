package com.rayleigh.activiti;

import com.rayleigh.activiti.Impl.ActivitiImageService;
import com.rayleigh.activiti.Impl.ActivitiService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ActivitiImageService activitiImageService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;

    //部属流程
    @Test
    public void deploy(){

        String id = activitiService.deployProcess("claimCompanyActiviti.xml","first_last","测试认领企业");
        System.out.println(id);

    }
    @Test
    public void start(){


        String processId = activitiService.startProcess("claimCompanyActiviti","2");
        System.out.println( processId );
    }

    @Test
    public void querytaskList(){
        String assignee = "5";
        List list =  activitiService.queryTask(assignee);
        System.out.println(list);
    }

    /**
     * 转发
     */
    @Test
    public void claim(){
        String taskId = "27506";
//        taskService.setVariable(taskId,"type",1);
        taskService.claim(taskId, "11");

    }

    @Test
    public void resolveTask(){
        Map<String, Object> variables = new HashMap<>();
        variables.put("status", "ok");
        String taskId = "182506";
        taskService.resolveTask(taskId,variables);
    }

    //加签
    @Test
    public void jiaqian(){
        String taskId = "182506";
        taskService.setVariable(taskId,"type","jiaqian");
        taskService.setVariable(taskId,"befor","12");
        taskService.delegateTask(taskId, "22");
    }

    @Test
    public void queryVariable(){
        String taslId = "182506";
        String id = (String) taskService.getVariable(taslId,"befor");
        System.out.println(id);
    }

    @Test
    public void setProcessVariable(){
        String taskId = "2505";
        Map<String, Object> variables = new HashMap<>();
        variables.put("user1", "001");//指定发起的审批人
        taskService.setVariable(taskId,"leave",variables);
        System.out.println("设置流程变量成功!");
    }

    @Test
    public void compile(){
        String taskId = "330006";
        String assignees = "4,5";
        String assign = "张三";
        activitiService.compileTask(taskId,assignees,assign);
    }
    @Test
    public void queryTaskId(){
        String assignees = "12";
        String processInstanceId = "215001";
        String id = activitiService.queryTaskId(assignees,processInstanceId);
        System.out.println(id);
    }
    @Test
    public void queryTaskId2(){
        String assignees = "12";
        String processInstanceId = "215001";
        String taskId = activitiService.queryTaskIdByAssignees(assignees,processInstanceId);
        System.out.println(taskId);
    }
    @Test
    public void queryTaskIdByAssignees(){
        String assignees = "3333";
        String processInstanceId = "40032";
        String id = activitiService.queryTaskIdByAssignees(assignees,processInstanceId);
        System.out.println(id);
    }
    @Test
    public void reject(){
        String taskId = "207506";
        String assign = "13";
        activitiService.rejectTask(taskId,assign);
    }
    @Test
    public void rejectToStart(){
        String taskId = "10006";
        String assign = "李四";
        activitiService.rejectTaskToStart(taskId,assign);
    }
    @Test
    public void queryHisDetail(){

    }
    @Test
    public void suspendProcess(){
        String processInstanceId = "97505";
        Boolean state = activitiService.suspendProcess(processInstanceId);
        System.out.println(state);
    }

    @Test
    public void getProcessInstanceId(){
        String assignee = "thunder";
        List list = activitiService.getProcessInstanceId(assignee,"company");
        System.out.println(list);
    }

    @Test
    public void queryImg() throws Exception {
        String processInstanceid = "17501";
        InputStream is = activitiImageService.getResourceDiagramInputStream(processInstanceid);
        BufferedImage bi = ImageIO.read(is);
        File file = new File("C:\\Users\\winshe03\\Desktop\\io\\"+processInstanceid+".png");
        if(!file.exists()) file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ImageIO.write(bi, "png", fos);
        fos.close();
        is.close();
        System.out.println(is);
        System.out.println("图片生成成功");
    }
    @Test
    public void getProcessInstanceDetail(){
        String pid = "180001";
        System.out.println(activitiService.getNextNode(pid));
////        String key = null;
////        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pid).list();
////        for (Task task : tasks){
////            key= task.getTaskDefinitionKey();
////            System.out.println(key);
////        }
//        // 获取历史流程实例
//        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(pid).singleResult();
//
//        BpmnModel model = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
//
//        List list = new ArrayList();
//        if(model != null) {
//            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
//            for(FlowElement e : flowElements) {
//                if (e.getClass().toString().contains("UserTask")){
//                    list.add(e.getId());
//                }
//            }
//            System.out.println(list.toString());
//            if (list.contains("firstCheck")){
//                System.out.println(list.indexOf("firstCheck"));
//                System.out.println(list.get(list.indexOf("firstCheck")+1));
//            }
//        }
    }
}

