package com.ahhmet.banking.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ahhmet.banking.logging.LogModel;
import com.ahhmet.banking.message.AccountCreateFailedResponse;
import com.ahhmet.banking.message.AccountCreateSuccessResponse;
import com.ahhmet.banking.message.AccountTransferResponse;
import com.ahhmet.banking.model.AccountModel;

import com.ahhmet.banking.service.IBankingService;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.ahhmet.banking.transferred.AddingBalance;
import com.ahhmet.banking.transferred.TransferredBalance;

@RestController
public class BankingController {
	
	@Autowired
	private IBankingService service;
	@Autowired
	private AccountCreateSuccessResponse createResponse;
	@Autowired
	private AccountCreateFailedResponse failedResponse;
	
	@PostMapping(path = "aBank")
	public ResponseEntity<?> createAccount(@RequestBody AccountModel request) throws IOException{
		boolean result=service.addToText(request,createResponse,failedResponse);
		if(result) {
			return new ResponseEntity<>(createResponse,HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(failedResponse,HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
	
	@GetMapping(path="aBank/{id}")
	public ResponseEntity<AccountModel> detailAccount(@PathVariable String id){
		return new ResponseEntity<>(service.getAccountModel(id),HttpStatus.OK);
	}
	
	@PatchMapping(path="aBank/account/{accountId}")
	public ResponseEntity<AccountModel> balance(@RequestBody AddingBalance request, @PathVariable String accountId) throws IOException{
		return new ResponseEntity<>(service.getTotalBalance(request,accountId),HttpStatus.OK);
	}

	@PatchMapping(path="aBank/{id}")
	public ResponseEntity<AccountTransferResponse> transfer(@RequestBody TransferredBalance request, @PathVariable String id ) throws IOException{
		return new ResponseEntity<>(service.getTransfer(request, id),HttpStatus.OK);
	}
	
	@GetMapping(path="aBank/logs/{id}")
	public ResponseEntity<List<LogModel>> logsAccount(@PathVariable String id) throws FileNotFoundException, IOException{
		return new ResponseEntity<>(service.getLogsAccount(id),HttpStatus.OK);
	}
	
}
