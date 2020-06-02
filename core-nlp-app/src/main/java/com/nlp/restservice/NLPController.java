package com.nlp.restservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nlp.restservice.pojo.request.Request;
import com.nlp.restservice.pojo.response.Response;
import com.nlp.restservice.processor.NERFastProcessor;
import com.nlp.restservice.processor.NERProcessor;
import com.nlp.restservice.processor.SentimentProcessor;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

@RestController
@RequestMapping("nlp")
public class NLPController {

	Logger logger = LoggerFactory.getLogger(NLPController.class);
	
	private CRFClassifier classifier = CRFClassifier.getClassifierNoExceptions("ner-model-beauty.ser.gz");

	//private NERClassifierCombiner defaultClassifier = NERClassifierCombiner.createNERClassifierCombiner("ner",null, StringUtils.propFileToProperties("D:/workspace/java/nlp/corenlp-springboot/target/classes/com/nlp/restservice/english.properties"));

	private CRFClassifier<CoreMap> defaultClassifier = CRFClassifier.getClassifierNoExceptions("english.muc.7class.distsim.crf.ser.gz");

	//private Properties prop = new Properties(new FileReader("db.properties"));

	private StanfordCoreNLP nerPipeline = new StanfordCoreNLP(getProps("english.properties"));
	
	private StanfordCoreNLP sentimentPipeline = new StanfordCoreNLP(getProps("sentiment.properties"));


	@Autowired
	NERProcessor nerp;

	@Autowired
	NERFastProcessor nerfp;
	
	@Autowired
	SentimentProcessor sp;


	@GetMapping("/hello")
	public String hello(@RequestParam(value = "text", defaultValue = "NER") String text) {
		return  "Hello! Welcome to NLP";
	}
	
	@PostMapping("/sentiment")
	public Map sentimentAnalysis(@RequestBody Request request) {
		return sp.processRequest(sentimentPipeline, request.getText());
	}


	@PostMapping("/ner")
	com.nlp.restservice.pojo.response.es.Response processNER(@RequestBody Request request) {

		return nerp.processRequest(nerPipeline, request.getText());
	}

	@PostMapping("/fast-ner")
	Response processFastNER(@RequestBody Request request) {

		return nerfp.processRequest(classifier, defaultClassifier, request.getText());
	}
	

	private Properties getProps(String fileName) {
		Properties prop = new Properties();
		try (InputStream input = this.getClass().getResourceAsStream(fileName);) {
			// load a properties file
			logger.info("Loading english.properties file");
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}
}
