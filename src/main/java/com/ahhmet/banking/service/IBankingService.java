package com.ahhmet.banking.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.ahhmet.banking.logging.LogModel;
import com.ahhmet.banking.message.AccountCreateFailedResponse;
import com.ahhmet.banking.message.AccountCreateSuccessResponse;
import com.ahhmet.banking.message.AccountTransferResponse;
import com.ahhmet.banking.model.AccountModel;

import com.ahhmet.banking.transferred.AddingBalance;
import com.ahhmet.banking.transferred.TransferredBalance;

public interface IBankingService {
	
	public boolean addToText(AccountModel m, AccountCreateSuccessResponse response, AccountCreateFailedResponse r) throws IOException;
	
    public AccountModel getAccountModel(String id);
    
    public AccountModel getTotalBalance(AddingBalance m,String id) throws IOException;

	public AccountTransferResponse getTransfer(TransferredBalance m, String id) throws IOException;
	
	public List<LogModel>getLogsAccount(String id) throws FileNotFoundException, IOException;
}
