package com.corenlp.training.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.io.IOUtils;

public class CustomIOUtils {

	
	 /**
	   *
	   */
	  public static  List<Double> getDoubleColumnFromCSV(String path, int position,String split) throws IOException {
	    List<Double> results = new ArrayList<Double>();

	    for (String line : IOUtils.readLines(path)) {
	    	String[] values = line.split(split);
	    	results.add(Double.valueOf(values[position]));
	    }
	    return results;
	  }
}
