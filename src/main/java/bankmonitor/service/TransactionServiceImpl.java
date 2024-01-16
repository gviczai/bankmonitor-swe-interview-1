package bankmonitor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankmonitor.api.model.TransactionDto;
import bankmonitor.model.Transaction;
import bankmonitor.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TransactionServiceImpl implements TransactionService {

  private TransactionRepository transactionRepository;

  @Autowired
  public void setTransactionRepository(TransactionRepository transactionRepository) {
      this.transactionRepository = transactionRepository;
  }

  @Override
  public TransactionDto create(String jsonData) {
      Transaction transaction = new Transaction();
      
      transaction.setTimestamp(LocalDateTime.now()); //See comment in Transaction.java
      transaction.setData(jsonData);
      
      return convert(transactionRepository.save(transaction));
  }

  @Override
  public List<TransactionDto> read() {
      return transactionRepository.findAll()
          .stream()
          .map(TransactionServiceImpl::convert)
          .toList();
  }

  @Override
  public TransactionDto update(Long id, String jsonData) {
      return transactionRepository.findById(id)
          .map(tr -> tr.data(jsonData))
          .map(transactionRepository::save)
          .map(TransactionServiceImpl::convert)
          .orElseThrow(EntityNotFoundException::new); //TODO: a custom bankmonitor exception would be nice
  }
  
  public static TransactionDto convert(Transaction transaction) {

      JSONObject jsonData = new JSONObject(transaction.getData());

      int amount = jsonData.has("amount") ? jsonData.getInt("amount") : -1;
      String reference = jsonData.has(REFERENCE_KEY) ? jsonData.getString(REFERENCE_KEY) : "";

      return new TransactionDto(transaction.getData(), amount, reference);
  }
}
