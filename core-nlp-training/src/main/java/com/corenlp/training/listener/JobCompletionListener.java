package com.corenlp.training.listener;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import edu.stanford.nlp.io.IOUtils;

public class JobCompletionListener extends JobExecutionListenerSupport {
	
	Logger logger = LoggerFactory.getLogger(JobCompletionListener.class);

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			//System.out.println(jobExecution.getExecutionContext().get("train_file"));
			IOUtils.deleteRecursively(new File((String)(jobExecution.getExecutionContext().get("train_file"))));
			logger.info("Successfully deleted {} on completion", jobExecution.getExecutionContext().get("train_file"));
			
			//logger.info("Job {} completed in {} seconds",jobExecution.getJobId(),(jobExecution.getEndTime().getTime()-jobExecution.getStartTime().getTime())/1000);
		}
	}

}