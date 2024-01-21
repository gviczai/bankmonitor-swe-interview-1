package bankmonitor;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bankmonitor.api.model.TransactionDto;
import bankmonitor.model.Transaction;
import bankmonitor.repository.TransactionRepository;
import bankmonitor.service.TransactionService;
import bankmonitor.service.TransactionServiceImpl;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TransactionTest {

    @Mock
    TransactionRepository repository;
    
    @InjectMocks
    TransactionServiceImpl service;
    
    @Test
    void transactionConverter_withDefaults() {
        //given
        Transaction transaction = new Transaction()
          .data("{}");
        
        TransactionDto transactionDto;
        
        //when
        transactionDto = TransactionServiceImpl.convert(transaction);
        
        //then
        assertEquals(-1, transactionDto.getAmount());
        assertEquals("", transactionDto.getReference());
    }

    @Test
    void transactionConverter_withValues() {
        //given
        String trData = "{ \""+TransactionService.REFERENCE_KEY+"\": \"REF-001\", \"amount\": 123}";
        
        Transaction transaction = new Transaction()
          .data(trData);
        
        TransactionDto transactionDto;
        
        //when
        transactionDto = TransactionServiceImpl.convert(transaction);
        
        //then
        assertEquals(123, transactionDto.getAmount());
        assertEquals("REF-001", transactionDto.getReference());
    }

    @Test
    void read_transactions_happyPath() {
        //given
        String trData = "{ \""+TransactionService.REFERENCE_KEY+"\": \"REF-001\", \"amount\": 123}";
        
        when(repository.findAll())
            .thenReturn(Collections.singletonList(new Transaction().data(trData)));
        
        List<TransactionDto> transactionDtoList;
        
        //when
        transactionDtoList = service.read();
        
        //then
        assertNotNull(transactionDtoList);
        assertThat(transactionDtoList, hasSize(1));
        assertEquals(123, transactionDtoList.get(0).getAmount());
        assertEquals("REF-001", transactionDtoList.get(0).getReference());
    }

    @Test
    void update_transaction_idNotFound() {
        //given
        String trData = "{ \""+TransactionService.REFERENCE_KEY+"\": \"REF-001\", \"amount\": 123}";
        
        when(repository.findById(anyLong()))
            .thenReturn(Optional.empty());
        
        //when
        Throwable thrown = catchThrowable(() -> service.update(1L, trData));
        
        //then
        assertThat(thrown)
            .isInstanceOf(EntityNotFoundException.class);
    }
    
}
