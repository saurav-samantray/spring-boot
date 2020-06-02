package com.nlp.restservice.pojo.response.es;

import java.util.ArrayList;

public class Bool {
	
	
	ArrayList < Object > filter = new ArrayList < Object > ();
	ArrayList < Object > must = new ArrayList < Object > ();
	
	public Bool() {
		this.filter = new ArrayList<Object>();
		this.must = new ArrayList<Object>();
	}
	
	public ArrayList<Object> getFilter() {
		return filter;
	}
	public void setFilter(ArrayList<Object> filter) {
		this.filter = filter;
	}
	public ArrayList<Object> getMust() {
		return must;
	}
	public void setMust(ArrayList<Object> must) {
		this.must = must;
	}



}