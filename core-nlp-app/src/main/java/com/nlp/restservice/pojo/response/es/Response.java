package com.nlp.restservice.pojo.response.es;

public class Response {

	private ESResponse es_input;
	private boolean success;
	
	public Response() {
		this.es_input = new ESResponse();
		this.success = false;
	}
	
	public ESResponse getEs_input() {
		return es_input;
	}
	public void setEs_input(ESResponse es_input) {
		this.es_input = es_input;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	


}
