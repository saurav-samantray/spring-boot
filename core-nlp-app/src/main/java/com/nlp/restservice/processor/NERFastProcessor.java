package com.nlp.restservice.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nlp.restservice.pojo.response.Entity;
import com.nlp.restservice.pojo.response.Response;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


@Service
public class NERFastProcessor {

	
	public Response processRequest(CRFClassifier<CoreMap> classifier, CRFClassifier<CoreMap> defaultClassifier,String text) {
		Response res = new Response();
		List<Entity> rawCustomEntities = null;
		List<Entity> rawDefaultEntities = null;
		List<Entity> groupedEntities = new ArrayList<>();
		boolean productExists = false;
		

		String dNerString = defaultClassifier.classifyToString(text,"slashTags",false);

		String nerString = classifier.classifyToString(text);

		rawCustomEntities =  Arrays.stream(nerString.split(" "))
				.map(x -> new Entity(x.split("/")[0],x.split("/")[1]))
				.collect(Collectors.toList());

		
		
		Entity aggregator = null;
		String prevType = null;
		rawDefaultEntities =  Arrays.stream(dNerString.trim().split(" "))
				.map(x -> new Entity(x.split("/")[0],x.split("/")[1]))
				.collect(Collectors.toList());


		
		for(Entity ent : rawCustomEntities) {
			if("product".equalsIgnoreCase(ent.getType())) {
				productExists = true;
			}
			if(!ent.getType().equalsIgnoreCase("O")) {
				if(aggregator == null) {
					aggregator = new Entity();
					aggregator.setText(ent.getText());
					aggregator.setType(ent.getType());
					prevType = ent.getType();
				}else if(prevType.equalsIgnoreCase(ent.getType())) {
					aggregator.setText(aggregator.getText()+" "+ent.getText());
					aggregator.setType(ent.getType());
				}else {
					groupedEntities.add(aggregator);
					aggregator = new Entity();
					aggregator.setText(ent.getText());
					aggregator.setType(ent.getType());
					prevType = ent.getType();
				}
			}
		}

		aggregator = null;
		prevType = null;
		for(Entity ent : rawDefaultEntities) {
			if(!Arrays.asList("O ORGANIZATION".split(" ")).contains(ent.getType())) {
				if(aggregator == null) {
					aggregator = new Entity();
					aggregator.setText(ent.getText());
					aggregator.setType(ent.getType());
					prevType = ent.getType();
				}else if(prevType.equalsIgnoreCase(ent.getType())) {
					aggregator.setText(aggregator.getText()+" "+ent.getText());
					aggregator.setType(ent.getType());
				}else {
					groupedEntities.add(aggregator);
					aggregator = new Entity();
					aggregator.setText(ent.getText());
					aggregator.setType(ent.getType());
					prevType = ent.getType();
				}
			}
		}
		//System.out.println(classifier.classifyToString(text));
		res.setEntities(groupedEntities);
		//res.setEntities(rawEntities);
		
		//if(map.containsKey(keyToBeChecked)) 

		return res;
	}
}
