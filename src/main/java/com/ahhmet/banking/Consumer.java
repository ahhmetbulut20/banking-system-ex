package com.ahhmet.banking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

	@KafkaListener(topics = "logs", groupId = "logs_consumer_group")
	public void listenTransfer(
			  @Payload String message, 
			  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
	) throws IOException {
		System.out.println(message + ", Partition : " + partition);
		BufferedWriter wr= new BufferedWriter(new FileWriter(new File("logs.txt"),true));
		wr.write(message);
		wr.newLine();
		wr.close();
	}
}
