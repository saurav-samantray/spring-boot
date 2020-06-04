package com.corenlp.training.pojo.response;

import java.util.List;

public class ListResponse {
	
	boolean success = false;
	
	List<JobDetail> jobDetails;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<JobDetail> getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(List<JobDetail> jobDetails) {
		this.jobDetails = jobDetails;
	}
	
}
