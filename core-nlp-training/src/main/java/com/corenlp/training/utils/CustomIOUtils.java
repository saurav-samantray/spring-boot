package com.corenlp.training.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.stanford.nlp.io.IOUtils;

public class CustomIOUtils {


	/**
	 *
	 */
	public static  List<Double> getDoubleColumnFromCSV(String path, int position,String split) throws IOException {
		List<Double> results = new ArrayList<>();

		for (String line : IOUtils.readLines(path)) {
			String[] values = line.split(split);
			results.add(Double.valueOf(values[position]));
		}
		return results;
	}
	
	public static  List<String> getStringColumnFromCSV(String path, int position,String split) throws IOException {
		List<String> results = new ArrayList<>();

		for (String line : IOUtils.readLines(path)) {
			String[] values = line.split(split);
			if(values == null || values.length ==1) {
				continue;
			}
			results.add(values[position]);
		}
		return results;
	}
	
	public static  List<String> getDistinctLabelCountFromCSV(String path, int position,String split) throws IOException {
		List<String> results = new ArrayList<>();
		Map<String,List> labelMap = new HashMap<>();
		for (String line : IOUtils.readLines(path)) {
			String[] values = line.split(split);
			if(values == null || values.length ==1) {
				continue;
			}
			
			if(labelMap.get(values[position]) == null || !labelMap.get(values[position]).contains(values[position-1])) {
				if(labelMap.get(values[position-1]) == null) {
					labelMap.put(values[position], Arrays.asList(new String[] {values[position-1] }));
				}
				results.add(values[position]);
			}
			
		}
		return results;
	}


	// Save Files
	public static String saveUploadedFiles(MultipartFile[] files, String uploadDirPath) throws FileNotFoundException, IOException {

		// Make sure directory exists!
		File uploadDir = new File(uploadDirPath);
		uploadDir.mkdirs();


		String uploadFilePath = null;

		for (MultipartFile file : files) {

			if (file.isEmpty()) {
				throw new FileNotFoundException();
			}
			uploadFilePath = uploadDirPath + "/" + file.getOriginalFilename();

			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadFilePath);
			Files.write(path, bytes);


		}
		return uploadFilePath;
	}
}
