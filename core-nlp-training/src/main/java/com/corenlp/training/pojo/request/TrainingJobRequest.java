package com.corenlp.training.pojo.request;

public class TrainingJobRequest {

	private double tolerance;
	
	private String model_name;
	
	private String train_file;

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getTrain_file() {
		return train_file;
	}

	public void setTrain_file(String train_file) {
		this.train_file = train_file;
	}
	
	
}
