package com.corenlp.training;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.corenlp.training.pojo.request.TrainingJobRequest;
import com.corenlp.training.pojo.response.DetailResponse;
import com.corenlp.training.pojo.response.JobDetail;
import com.corenlp.training.pojo.response.ListResponse;
import com.corenlp.training.utils.CustomIOUtils;

import edu.stanford.nlp.io.IOUtils;


@RestController
@RequestMapping("training")
public class TrainingController {

	private static String UPLOAD_DIR="tmp";

	Logger logger = LoggerFactory.getLogger(TrainingController.class);

	@Autowired
	Job nerTrainingJob;

	@Autowired
	@Qualifier("trainingJobLauncher")
	JobLauncher jobLauncher;

	@Autowired
	JobExplorer jobExplorer;

	@PostMapping("ner")
	public DetailResponse nerTraining(@ModelAttribute TrainingJobRequest requestBody) throws FileNotFoundException,
	IOException,
	JobExecutionAlreadyRunningException,
	JobRestartException,
	JobInstanceAlreadyCompleteException,
	JobParametersInvalidException{
		JobDetail jobDetail = new JobDetail();
		DetailResponse dr = new DetailResponse();

		try {
			//upload training file to temp folder
			String trainFilePath = saveUploadedFiles(requestBody.getTrainFiles());
			String testFilePath = saveUploadedFiles(requestBody.getTestFiles());
			if(trainFilePath == null) {
				dr.setErrorMessage("File not found or not provided");
				dr.setSuccess(false);
				return dr;
			}

			JobParameters jobParameters = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.addDouble("tolerance", requestBody.getTolerance())
					.addString("train_file",trainFilePath)
					.addString("test_file",testFilePath)
					.addString("model_name", requestBody.getModel_name())
					.toJobParameters();

			JobExecution je = jobLauncher.run(nerTrainingJob, jobParameters);

			jobDetail.setExecution_id(je.getId());
			jobDetail.setJob_status(je.getStatus().toString());
			dr.setJobDetail(jobDetail);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			logger.error(e.getMessage());
			dr.setErrorMessage(e.getMessage());
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
			try {
				List<Double> results = CustomIOUtils.getDoubleColumnFromCSV("output/"+executionId+"/QN_m25_MINPACK_DIAGONAL.output",2,",");
				jobDetail.setGradNorm(results);
				logger.info(results.toString());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			dr.setJobDetail(jobDetail);
		}else {
			dr.setErrorMessage("Job not found");
		}
		
		
		
		return dr;
	}

	@GetMapping("ner/{id}/download")
	public String download(@PathVariable("id") long executionId,HttpServletResponse response) {

		return "http://192.168.1.199/ner/1/ner-model-beauty.ser.gz";

		//return byteArrayOutputStream.toByteArray();
	}

	@GetMapping("ner")
	public ListResponse nerTrainings() {
		JobDetail item = null;
		List<JobDetail> items= new ArrayList<>();
		ListResponse response = new ListResponse();
		List<JobInstance> jobInstances = jobExplorer.getJobInstances("nerTrainingJob", 0, 5);

		//logger.info(jobExplorer.getJobNames().toString());

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

	// Save Files
	private String saveUploadedFiles(MultipartFile[] files) throws FileNotFoundException, IOException {

		// Make sure directory exists!
		File uploadDir = new File(UPLOAD_DIR);
		uploadDir.mkdirs();


		String uploadFilePath = null;

		for (MultipartFile file : files) {

			if (file.isEmpty()) {
				throw new FileNotFoundException();
			}
			uploadFilePath = UPLOAD_DIR + "/" + file.getOriginalFilename();

			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadFilePath);
			Files.write(path, bytes);


		}
		return uploadFilePath;
	}

}
