package com.rayleigh.activiti.config;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * 使用Java类完成配置文件
 * @author zc 2018-06-04
 */
@Configuration
public class ActivitiConfig {

	@Resource
    private DataSource dataSource;
    @Resource
    private ResourcePatternResolver resourceLoader;
    
    /**
     * 初始化配置，将创建28张表
     * @return
     */
    @Bean
    public StandaloneProcessEngineConfiguration processEngineConfiguration() {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setAsyncExecutorActivate(false);
        return configuration;
    }
    
    @Bean
    public ProcessEngine processEngine() {
        return processEngineConfiguration().buildProcessEngine();
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }
    @Bean
    public HistoryService historyService() {
        return processEngine().getHistoryService();
    }
    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        return processEngine().getTaskService();
    }
    
    /**
     * 部署流程
     * @throws IOException 
     */
    @PostConstruct
    public void initProcess() throws IOException {
        DeploymentBuilder deploymentBuilder= repositoryService().createDeployment();
//        Resource resource = resourceLoader.getResource("classpath:/processes/EceProvinceProcess.bpmn");
//        deploymentBuilder .enableDuplicateFiltering().addInputStream(resource.getFilename(), resource.getInputStream()).name("deploymentTest").deploy();
//        deploymentBuilder .enableDuplicateFiltering().addClasspathResource("claimCompany.bpmn").name("认领企业").deploy();
//        deploymentBuilder .enableDuplicateFiltering().addClasspathResource("company.bpmn").name("企业百科").deploy();
//        deploymentBuilder .enableDuplicateFiltering().addClasspathResource("project.bpmn").name("项目百科").deploy();
//        deploymentBuilder .enableDuplicateFiltering().addClasspathResource("test.bpmn").name("测试").deploy();
    }
}
