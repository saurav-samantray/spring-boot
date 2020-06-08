package com.corenlp.training.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

import com.corenlp.training.core.L7CRFClassifier;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;



@Service
public class NERTrainingTask implements Tasklet{
	
	Logger logger = LoggerFactory.getLogger(NERTrainingTask.class);

	public CRFClassifier<?> getModel(String modelPath) {
	    return CRFClassifier.getClassifierNoExceptions(modelPath);
	}
	
	public void doTagging(CRFClassifier<?> model, String input) {
		  input = input.trim();
		  logger.info(input + "=>"  +  model.classifyToString(input));
		}
	
	public void trainAndWrite(ChunkContext chunkContext,String prop) {
		   Properties props = StringUtils.propFileToProperties(prop);
		   
		   logger.info("Tolerance before: "+props.getProperty("tolerance"));
		   props.setProperty("tolerance", chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getDouble("tolerance").toString());
		   props.setProperty("serializeTo", chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getString("model_name"));
		   props.setProperty("trainFile", chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getString("train_file"));
		   if(chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getString("test_file") != null) {
			   props.setProperty("testFile", chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getString("test_file"));
		   }
		   
		   //Setting the temp train_file in Execution context for deleting it later
		   chunkContext
		   	.getStepContext()
		   	.getStepExecution()
		   	.getJobExecution()
		   	.getExecutionContext()
		   	.put("train_file", chunkContext.getStepContext().getStepExecution()
				      .getJobParameters().getString("train_file"));
		   
		   long jobId = chunkContext
		   	.getStepContext()
		   	.getStepExecution()
		   	.getJobExecution().getId();
		   
		   logger.info("Tolerance after: "+props.getProperty("tolerance"));
		   String modelOutPath = props.getProperty("serializeTo");
		   SeqClassifierFlags flags = new SeqClassifierFlags(props);
		   L7CRFClassifier<CoreLabel> crf = new L7CRFClassifier<>(flags);
		   crf.setJobId(jobId);
		   crf.train();
		   crf.serializeClassifier(modelOutPath);
		   
		}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("Executing training task");
		trainAndWrite(chunkContext,"ner-training.properties");
		
		CRFClassifier<?> classifier = getModel("ner-model-beauty.ser.gz");
		
		List<String> texts = new ArrayList<>();
		texts.add("brand new Dove conditioner soap 12.3 fl oz for curly hair");
		texts.add("almost used Matrix conditioning shampoo 200ml");
		texts.add("ORs Replenishing Conditioner 1.75 Oz");
		texts.add("Olive Oil For Naturals leave in Smoothie");
		texts.add("Curls Unleashed Coconut and Shea Butter Curly Coil HD Gel Souffle 16 Ounce for dry hair that helps with moisturizing scalp");
		texts.add("ORS Organic Root Stimulator Olive Oil Built-In Protection 150 ml");
		texts.add("Sulphate free shea butter styling cream");
		texts.add("Olive Oil Hues Vitamin & Oil Creme Color Cocoa Brown with instruction sheet 200g for a meeting in Dubai tomorrow");
		
		List<String> output = texts.stream()
			 .map(x -> classifier.classifyToString(x))
			 .collect(Collectors.toList());
		
		logger.info(output.toString());
		//logger.info("Time taken to complete training: "+(System.currentTimeMillis()-stime));
		
		
		//doTagging(classifier, "ORS Olive Oil 12oz for blone hair");
		
		//Thread.sleep(5000);
		return RepeatStatus.FINISHED;
	}
}
