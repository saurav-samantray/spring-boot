package com.nlp.restservice.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nlp.restservice.pojo.response.es.Response;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@Service
public class NERProcessor {
	
	private static List ENTITY_FILTER = Arrays.asList("MONEY".split(" "));
	
	private static String ES_ENTITY_MAPPER = "{'CAPACITY':'size'}";

	public Response processRequest(StanfordCoreNLP pipeline,String text) {
		boolean productExists = false;
		
		Response esResponse = new Response();

		
		CoreDocument document = new CoreDocument(text);

	    pipeline.annotate(document);
	    CoreSentence sentence = document.sentences().get(0);
	    
	    
	    List<CoreEntityMention> entityMentions = sentence.entityMentions();
	    //System.out.println("Example: entity mentions");
	    for(CoreEntityMention e : entityMentions) {
	    	//System.out.println(e.text()+"/"+e.entityType()+" , ");
	    	if(ENTITY_FILTER.contains(e.entityType())) {
	    		continue;
	    	}
	    	else if("PRODUCT".equalsIgnoreCase(e.entityType())) {
	    		Map<String,Map<String,String>> queryString = new HashMap<String, Map<String,String>>();
	    		Map<String,String> query = new HashMap<String, String>();
	    		query.put("query", e.text());
	    		queryString.put("query_string", query);
	    		esResponse.getEs_input().getQuery().getBool().getMust().add(queryString);
	    	}else {
	    		Map<String,Map<String,String>> term = new HashMap<String, Map<String,String>>();
	    		Map<String,String> field = new HashMap<String, String>();
	    		field.put(e.entityType().toLowerCase(), e.text());
	    		term.put("term", field);
	    		esResponse.getEs_input().getQuery().getBool().getFilter().add(term);
	    	}
	    }
	    //System.out.println(entityMentions);
	   // System.out.println();
	    
	    
	    esResponse.setSuccess(true);
		return esResponse;
	}

}
