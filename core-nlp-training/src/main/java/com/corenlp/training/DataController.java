package com.corenlp.training;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corenlp.training.pojo.request.DataRequest;
import com.corenlp.training.pojo.response.DataDetails;
import com.corenlp.training.pojo.response.DataVizResponse;
import com.corenlp.training.utils.CustomIOUtils;

@RestController
@RequestMapping("data")
public class DataController {

	Logger logger = LoggerFactory.getLogger(DataController.class);

	private static final String UPLOAD_DIR="tmp";

	@PostMapping("ner")
	public ResponseEntity<DataVizResponse> nerTraining(@ModelAttribute DataRequest requestBody) throws IOException{
		DataVizResponse res = new DataVizResponse();
		DataDetails dd = new DataDetails();

		if(requestBody.getDataFiles() != null && requestBody.getDataFiles().length >0) {
			String dataFilePath = CustomIOUtils.saveUploadedFiles(requestBody.getDataFiles(),UPLOAD_DIR);
			List<?> labels = CustomIOUtils.getStringColumnFromCSV(dataFilePath, 1, "\t");
			List<?> distinctLabels = CustomIOUtils.getDistinctLabelCountFromCSV(dataFilePath, 1, "\t");
			logger.info(labels.toString());
			Map<String, Long> labelCountMaps = (Map<String, Long>)labels.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			Map<String, Long> distinctLabelCountMaps = (Map<String, Long>)distinctLabels.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); 
			dd.setLabelCountMap(labelCountMaps);
			dd.setDistinctLabelCountMap(distinctLabelCountMaps);

			labelCountMaps.forEach((key,value) -> {
				if(!"O".equalsIgnoreCase(key)) {
					dd.getLabelCounts().add((Long)value);
					dd.getLabels().add((String)key);
				}
			});
			distinctLabelCountMaps.forEach((key,value) -> {
				if(!"O".equalsIgnoreCase(key)) {
					dd.getDistinctLabelCounts().add((Long)value);
					//dd.getLabels().add((String)key);
				}
			});
			res.setData(dd);

		}else {
			throw new FileNotFoundException("Data file not found");
		}
		return new ResponseEntity<DataVizResponse>(res,HttpStatus.OK);
	}
}
