package com.corenlp.training.pojo.response;

public class DataVizResponse {
	boolean success = true;
	
	DataDetails data;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public DataDetails getData() {
		return data;
	}
	public void setData(DataDetails data) {
		this.data = data;
	}
}
