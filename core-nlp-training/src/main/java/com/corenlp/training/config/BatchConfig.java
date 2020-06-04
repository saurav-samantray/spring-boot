package com.corenlp.training.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.corenlp.training.listener.JobCompletionListener;
import com.corenlp.training.service.NERTrainingTask;

@Configuration
public class BatchConfig {
	
	@Bean(name = "trainingJobLauncher")
	public JobLauncher trainingJobLauncher() throws Exception {
	    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	    jobLauncher.setJobRepository(jobRepository);
	    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
	    jobLauncher.afterPropertiesSet();
	    return jobLauncher;
	}

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public JobRepository jobRepository;

	@Bean
	public Job nerTrainingJob() {
		return jobBuilderFactory.get("nerTrainingJob")
				.incrementer(new RunIdIncrementer()).listener(listener())
				.flow(nerTrain()).end().build();
	}
	
	@Bean
    public Step nerTrain(){
        return stepBuilderFactory.get("nerTrain").tasklet(new NERTrainingTask()).build();
    }

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionListener();
	}

}