package com.corenlp.training.pojo.request;

import org.springframework.web.multipart.MultipartFile;

public class DataRequest {
	private MultipartFile[] dataFiles;

	public MultipartFile[] getDataFiles() {
		return dataFiles;
	}

	public void setDataFiles(MultipartFile[] dataFiles) {
		this.dataFiles = dataFiles;
	}
	
}
