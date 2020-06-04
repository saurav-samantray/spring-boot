package com.corenlp.training.pojo.response;

import java.io.Serializable;

public class JobDetail implements Serializable{
	
	
	private long execution_id;
	
	private String job_status;
	

	public long getExecution_id() {
		return execution_id;
	}

	public void setExecution_id(long execution_id) {
		this.execution_id = execution_id;
	}

	public String getJob_status() {
		return job_status;
	}

	public void setJob_status(String job_status) {
		this.job_status = job_status;
	}
	
	

}
