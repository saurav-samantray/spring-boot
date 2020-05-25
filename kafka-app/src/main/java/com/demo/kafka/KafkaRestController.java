package com.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.kafka.producer.Producer;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaRestController {

	private final Producer producer;

	@Autowired
	KafkaRestController(Producer producer) {
		this.producer = producer;
	}
	
	@GetMapping(value = "/hello")
	public String hello() {
		return "hello";
	}

	@PostMapping(value = "/publish")
	public void sendMessageToKafkaTopic(@RequestParam("message") String message){
		this.producer.sendMessage(message);
	}

}
