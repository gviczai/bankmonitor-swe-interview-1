package bankmonitor.service;

import java.util.List;

import bankmonitor.api.model.TransactionDto;

public interface TransactionService {

  String REFERENCE_KEY = "reference";

  TransactionDto create(String jsonData);

  List<TransactionDto> read();

  TransactionDto update(Long id, String jsonData);

}