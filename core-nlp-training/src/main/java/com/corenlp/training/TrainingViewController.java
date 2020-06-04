package com.corenlp.training;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.corenlp.training.pojo.response.JobDetail;

@Controller
public class TrainingViewController {
	
	@Autowired
	JobExplorer jobExplorer;

	@GetMapping("/")
	public String home(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		JobDetail item = null;
		List<JobDetail> items= new ArrayList<>();
		List<JobInstance> jobInstances = jobExplorer.getJobInstances("nerTrainingJob", 0, 5);
		
		
		for(JobInstance ji : jobInstances) {
			item = new JobDetail();
			item.setExecution_id(ji.getInstanceId());
			item.setJob_status(jobExplorer.getJobExecution(ji.getInstanceId()).getStatus().toString());
			items.add(item);
		}
		items.sort(Comparator.comparing(JobDetail::getExecution_id));
		
		model.addAttribute("items", items);
		return "home";
	}
}
