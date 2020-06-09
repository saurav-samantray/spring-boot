package com.corenlp.training.pojo.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.util.ArraySet;

public class DataDetails {
	
	public DataDetails() {
		this.labels = new ArraySet<String>();
		this.labelCounts = new ArrayList<Long>();
	}
	
	Map<String,Long> labelCountMap;
	
	Set<String> labels;

	List<Long> labelCounts;

	public Map<String, Long> getLabelCountMap() {
		return labelCountMap;
	}

	public void setLabelCountMap(Map<String, Long> labelCountMap) {
		this.labelCountMap = labelCountMap;
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}

	public List<Long> getLabelCounts() {
		return labelCounts;
	}

	public void setLabelCounts(List<Long> labelCounts) {
		this.labelCounts = labelCounts;
	}
	
	
	
	
}
