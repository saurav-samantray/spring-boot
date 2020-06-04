package com.corenlp.training.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class JobCompletionListener extends JobExecutionListenerSupport {
	
	Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			//logger.info("Job {} completed in {} seconds",jobExecution.getJobId(),(jobExecution.getEndTime().getTime()-jobExecution.getStartTime().getTime())/1000);
		}
	}

}