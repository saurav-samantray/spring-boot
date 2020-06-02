package com.nlp.restservice.pojo.response.es;

public class ESResponse {
	
	public ESResponse() {
		this.queryObject = new Query();
	}

	 Query queryObject;

	 public Query getQuery() {
	  return queryObject;
	 }

	 public void setQuery(Query queryObject) {
	  this.queryObject = queryObject;
	 }
	
}
