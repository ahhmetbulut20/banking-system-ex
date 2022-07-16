package com.ahhmet.banking.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ahhmet.banking.collectApi.Exchange;
import com.ahhmet.banking.logging.LogModel;
import com.ahhmet.banking.message.AccountCreateFailedResponse;
import com.ahhmet.banking.message.AccountCreateSuccessResponse;
import com.ahhmet.banking.message.AccountTransferResponse;
import com.ahhmet.banking.model.AccountModel;

import com.ahhmet.banking.transferred.AddingBalance;
import com.ahhmet.banking.transferred.TransferredBalance;
import org.apache.kafka.common.serialization.StringSerializer;

@Service
public class BankingService implements IBankingService{
	
	@Autowired
	private KafkaTemplate<String, String>producer;
	
	@Autowired
	private Exchange value;
	
	@Override
	public boolean addToText(AccountModel m, AccountCreateSuccessResponse response,AccountCreateFailedResponse r) throws IOException {
		
		if(control(m.getTc())) {
			int accountNumber=getRandomNumber();
			response.setAccountNumber(accountNumber);
			m.setAccountNumber(accountNumber);
			if(m.getType().equals("TL") || m.getType().equals("Dolar") || m.getType().equals("Altın")) {
				//String fileLine ="Account Number : "+ response.getAccountNumber() +"\nE-mail : "+ m.getEmail()+"\nName : "+m.getName()+"\nSurname : "+m.getSurname()+"\nTc : "+m.getTc()+"\nType : "+m.getType();
				String fileLine =m.getAccountNumber() +","+m.getName()+","+m.getSurname()+","+m.getTc()+","+ m.getEmail()+","+m.getType()+","+m.getBalance();
				BufferedWriter wr= new BufferedWriter(new FileWriter(new File("accounts.txt"),true));
				wr.write(fileLine);
				wr.newLine();
				wr.close();	
				response.setMessage("Account Created");
				return true;
			}
			else {
				r.setMessage("Invalid Account Type: " + m.getType());
				return false;
			}
		}
		else {
			r.setMessage("The User was created");
			return false;
		}
	}
	
	
	@Override
	public AccountModel getAccountModel(String id) {
		AccountModel m=new AccountModel();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("accounts.txt"));
			String line = reader.readLine();
			while (line != null) {
				if(line != null) {
					if(line.contains(id)) {
					String [] movieDetail=line.split(",");
					m.setAccountNumber(Integer.parseInt(movieDetail[0]));
					m.setName(movieDetail[1]);;
					m.setSurname(movieDetail[2]);
					m.setTc(movieDetail[3]);
					m.setEmail(movieDetail[4]);
					m.setType(movieDetail[5]);
					m.setBalance(Double.parseDouble(movieDetail[6]));
					return m;
				}
			}
				line = reader.readLine();
			}
				reader.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountModel getTotalBalance(AddingBalance t,String id) throws IOException {
		AccountModel m=new AccountModel();
		boolean result=control(id);
		String newLine="";
		if(!result) {
			m=getAccountModel(id);
			m.setBalance(m.getBalance()+t.getAmount());
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader("accounts.txt"));
				String line = reader.readLine();
				while (line != null) {
					if(!line.contains(id)) {
						newLine=newLine+line+"\n";
					}
					else {
						String fileLine =m.getAccountNumber() +","+m.getName()+","+m.getSurname()+","+m.getTc()+","+ m.getEmail()+","+m.getType()+","+m.getBalance();
						newLine=newLine+fileLine+"\n";
					}
					line=reader.readLine();
				}
					reader.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String message=m.getAccountNumber()+" deposit amount: "+ m.getBalance()+" "+m.getType();
		producer.send("logs",message);
		
		
		if(newLine!="") {
			BufferedWriter wr= new BufferedWriter(new FileWriter(new File("accounts.txt")));
			wr.write(newLine);
			wr.newLine();
			wr.close();
		}
		return m;
	}

	
	@Override
	public AccountTransferResponse getTransfer(TransferredBalance m,String id) throws IOException {
		AccountTransferResponse transfer= new AccountTransferResponse();
		boolean result=control(id);
		boolean result2=control(m.getTransferredAccountNumber());
		AccountModel model=new AccountModel();
		AccountModel model2=new AccountModel();
		Exchange exchange=new Exchange();
		double kafka = m.getAmount();
		if(!result && !result2) {
			model=getAccountModel(id);
			model2=getAccountModel(m.getTransferredAccountNumber());
			model.setBalance(model.getBalance()-m.getAmount());
			System.out.println(model.getBalance());
			if(model.getBalance()>=0) {
				if(model.getType().contains(model2.getType())) {
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else if(model.getType().contains("TL") && model2.getType().contains("Dolar")) {
					m.setAmount(m.getAmount()/exchange.dollarToTl());
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else if(model.getType().contains("TL") && model2.getType().contains("Altın")) {
					m.setAmount(m.getAmount()/exchange.goldenToTl());
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else if(model.getType().contains("Altın") && model2.getType().contains("Dolar")) {
					double temp=exchange.goldenToTl()*m.getAmount();
					double temp2=exchange.dollarToTl();
					m.setAmount(temp/temp2);
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else if(model.getType().contains("Altın") && model2.getType().contains("TL")) {
					m.setAmount(exchange.goldenToTl()*m.getAmount());
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else if(model.getType().contains("Dolar") && model2.getType().contains("Altın")) {
					double temp=m.getAmount()*exchange.dollarToTl();
					double temp2=temp/exchange.goldenToTl();
					m.setAmount(temp2);
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
				else{
					m.setAmount(m.getAmount()*exchange.dollarToTl());
					model2.setBalance(model2.getBalance()+m.getAmount());
					fileWriter(model);
					fileWriter(model2);
					transfer.setMessage("Transferred Succesfully");
				}
			}
			
			else
			{
				transfer.setMessage("Insufficient Balance");;
			}
			
			}
		else
			transfer.setMessage("Accounts was not found");;
		
		//2341231231 transfer amount:100,transferred_account:4513423423	
		String message=model.getAccountNumber()+" transfer amount: "+kafka+" "+model.getType()+", transferred_account: "+model2.getAccountNumber(); 
		producer.send("logs",message);
			
		return transfer;
	}

	
	public List<LogModel>getLogsAccount(String id) throws IOException{
		List<LogModel>logs=new ArrayList<LogModel>();
		BufferedReader reader;
		reader=new BufferedReader(new FileReader("logs.txt"));
		ArrayList<String> logsAccount=new ArrayList<String>();
		String line=reader.readLine();
		while(line!=null) {
			if(line!=null) {
				if(line.contains(id)) {
					logsAccount.add(line);
				}
			}
			line=reader.readLine();
		}
		
		for(int i=0;i<logsAccount.size();i++) {
			if(logsAccount.get(i).contains("transferred_account")) {
				String [] temp=logsAccount.get(i).split(" ");
				String message=temp[0]+" hesaptan "+temp[6]+" hesaba "+temp[3]+" "+temp[4]+" transfer edilmiştir.";
				LogModel log=new LogModel();
				log.setLog(message);
				logs.add(log);
			}
			else {
				String [] temp=logsAccount.get(i).split(" ");
				String message=temp[0]+" no'lu hesaba "+temp[3]+" "+temp[4]+" yatırılmıştır.";
				LogModel log=new LogModel();
				log.setLog(message);
				logs.add(log);
			}
		}
		
		
		return logs;
	}
	
	
	
	public boolean control(String tc) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new FileReader("accounts.txt"));
		String line = reader.readLine();
		while (line != null) {
			if(line != null) {
				if(line.contains(tc))
					return false;
			}
		
			line = reader.readLine();
		}
		return true;
	}
	
	
	
	public static int getRandomNumber() {

	    Random rnd = new Random();
	    int number = rnd.nextInt(999999999);

	    return 1000000000+number;
	}

	
	public void fileWriter(AccountModel m) {
			
		BufferedReader reader;
		String newLine="";
		try {
			reader = new BufferedReader(new FileReader("accounts.txt"));
			String line = reader.readLine();
			while (line != null) {
				if(!line.contains(m.getTc())) {
					newLine=newLine+line+"\n";
				}
				else {
					String fileLine =m.getAccountNumber() +","+m.getName()+","+m.getSurname()+","+m.getTc()+","+ m.getEmail()+","+m.getType()+","+m.getBalance();
					newLine=newLine+fileLine+"\n";
				}
				line=reader.readLine();
				
			}
				reader.close();
				
				if(newLine!="") {
					BufferedWriter wr= new BufferedWriter(new FileWriter(new File("accounts.txt")));
					wr.write(newLine);
					wr.newLine();
					wr.close();
				}
		}		
		catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
}