package com.nlp.restservice.pojo.response.es;

public class Query {
	
	public Query() {
		this.boolObject = new Bool();
	}
	
	Bool boolObject;


	public Bool getBool() {
		return boolObject;
	}


	public void setBool(Bool boolObject) {
		this.boolObject = boolObject;
	}
}