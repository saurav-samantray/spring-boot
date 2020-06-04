package com.corenlp.training;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corenlp.training.pojo.request.TrainingJobRequest;
import com.corenlp.training.pojo.response.DetailResponse;
import com.corenlp.training.pojo.response.JobDetail;
import com.corenlp.training.pojo.response.ListResponse;


@RestController
@RequestMapping("training")
public class TrainingController {
	
	Logger logger = LoggerFactory.getLogger(TrainingController.class);
	
	@Autowired
    Job nerTrainingJob;
	
	@Autowired
	@Qualifier("trainingJobLauncher")
    JobLauncher jobLauncher;
	
	@Autowired
	JobExplorer jobExplorer;

	@PostMapping("ner")
	public DetailResponse nerTraining(@RequestBody TrainingJobRequest requestBody) {
		JobDetail jobDetail = new JobDetail();
		DetailResponse dr = new DetailResponse();
		
		JobParameters jobParameters = new JobParametersBuilder()
											.addLong("time", System.currentTimeMillis())
											.addDouble("tolerance", requestBody.getTolerance())
											.addString("train_file", requestBody.getTrain_file())
											.addString("model_name", requestBody.getModel_name())
											.toJobParameters();
        try {
			JobExecution je = jobLauncher.run(nerTrainingJob, jobParameters);
			
			jobDetail.setExecution_id(je.getId());
			jobDetail.setJob_status(je.getStatus().toString());
			dr.setJobDetail(jobDetail);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			logger.error(e.getMessage());
			dr.setSuccess(false);
		}
		return dr;
	}
	
	@GetMapping("ner/{id}")
	public DetailResponse nerTrainingDetails(@PathVariable("id") long executionId) {
		JobDetail jobDetail = new JobDetail();
		DetailResponse dr = new DetailResponse();
		JobExecution je = jobExplorer.getJobExecution(executionId);
		
		if(null != je) {
			jobDetail.setExecution_id(je.getId());
			jobDetail.setJob_status(je.getStatus().toString());
			dr.setJobDetail(jobDetail);
		}else {
			dr.setErrorMessage("Job not found");
		}
		return dr;
	}
	
	@GetMapping("ner/{id}/download")
	public String download(@PathVariable("id") long executionId,HttpServletResponse response) {
		
		/*
		//setting headers
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"ner-model-beauty.ser.gz\"");
        
        //creating byteArray stream, make it bufforable and passing this buffor to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
        
        File file = new File("ner-model-beauty.ser.gz");
        try {
        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fileInputStream = new FileInputStream(file);

        StreamUtils.copy(fileInputStream, zipOutputStream);

        fileInputStream.close();
        zipOutputStream.closeEntry();
        }catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		return "http://192.168.1.199/ner/1/ner-model-beauty.ser.gz";
		
        //return byteArrayOutputStream.toByteArray();
	}
	
	@GetMapping("ner")
	public ListResponse nerTrainings() {
		JobDetail item = null;
		List<JobDetail> items= new ArrayList<>();
		ListResponse response = new ListResponse();
		List<JobInstance> jobInstances = jobExplorer.getJobInstances("nerTrainingJob", 0, 5);
		
		logger.info(jobExplorer.getJobNames().toString());
		
		for(JobInstance ji : jobInstances) {
			item = new JobDetail();
			item.setExecution_id(ji.getInstanceId());
			item.setJob_status(jobExplorer.getJobExecution(ji.getInstanceId()).getStatus().toString());
			items.add(item);
		}
		response.setJobDetails(items);
		/*
		 * response.setSuccess(true); response.setExecution_id(je.getId());
		 * response.setJob_status(je.getStatus().toString());
		 */
		return response;
	}
	
}
