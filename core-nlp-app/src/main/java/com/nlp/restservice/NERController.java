package com.nlp.restservice;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nlp.restservice.pojo.request.Request;
import com.nlp.restservice.pojo.response.Response;
import com.nlp.restservice.processor.NERProcessor;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

@RestController
public class NERController {

	private CRFClassifier classifier = CRFClassifier.getClassifierNoExceptions("ner-model-beauty.ser.gz");
	
	//private NERClassifierCombiner defaultClassifier = NERClassifierCombiner.createNERClassifierCombiner("ner",null, StringUtils.propFileToProperties("D:/workspace/java/nlp/corenlp-springboot/target/classes/com/nlp/restservice/english.properties"));
	
	private CRFClassifier<CoreMap> defaultClassifier = CRFClassifier.getClassifierNoExceptions("english.muc.7class.distsim.crf.ser.gz");
	
	@Autowired
	NERProcessor nerp;

	
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "text", defaultValue = "NER") String text) {
		return  "Hello! Welcome to NLP";
	}

	
	@PostMapping("/ner")
	Response newEmployee(@RequestBody Request request) {
		
	    return nerp.processRequest(classifier, defaultClassifier, request.getText());
	  }
}
