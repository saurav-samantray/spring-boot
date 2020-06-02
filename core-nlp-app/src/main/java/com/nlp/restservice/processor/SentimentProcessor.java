package com.nlp.restservice.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


@Service
public class SentimentProcessor {
	
	public Map processRequest(StanfordCoreNLP pipeline,String text) {
		
		Map response = new HashMap<>();
		
		CoreDocument document = new CoreDocument(text);

	    pipeline.annotate(document);
	    CoreSentence sentence = document.sentences().get(0);
	    
	    response.put("sentiment", sentence.sentiment());
	    response.put("success", true);
	    
		return response;
	}

}
