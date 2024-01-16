package bankmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import bankmonitor.api.model.TransactionDto;

/*
 * Note: Usually it is not a good idea to create full-scale integration tests that initializes the full Spring context.
 * It slows down compile time drastically.
 * A good practice is to put automated integration tests into a separate application that is run by the CI at a regular interval against the 
 * test environment for example.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = TransactionApplication.class,
  properties = {"spring.profiles.active=memdb"})
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class TransactionIT {

  @Autowired
  MockMvc mvc;
  
  static ObjectMapper objectMapper;
  static ResourceLoader resourceLoader;

  @BeforeAll
  public static void setUp() {
     objectMapper = new ObjectMapper();
     resourceLoader = new DefaultResourceLoader();
  }
  
  @Test
  @Order(1)
  void get_transactions_happyPath() throws Exception {
    
    Resource resource = resourceLoader.getResource("classpath:transactions_response.json");
    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    String expectedResponse = FileCopyUtils.copyToString(reader);

    mvc.perform(
        get("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
        )
        //TODO: checking response json would be nice, but it requires a lot of effort:
        //.andExpect(jsonPath("$[0].name", is("bob")));
        // ...so checking response as is instead:
        //(prone to errors due to item order change in list and even a pretty printed response could fail the test)
        .andExpect(content()
            .string(expectedResponse)
        );
  }
  
  @Test
  @Order(2)
  void put_transactions_happyPath() throws Exception {
    
    String transactionData="{reference: \"REF-02\", amount: 5333}";
    
    String response = mvc.perform(
        put("/transactions/-5")
            .contentType(MediaType.APPLICATION_JSON)
            .content(transactionData)
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
          .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    
    TransactionDto respObj = objectMapper.readValue(response,TransactionDto.class);
    
    assertEquals(5333,respObj.getAmount());
    assertEquals("REF-02",respObj.getReference());
    assertEquals(transactionData,respObj.getData());
  }

  @Test
  void put_transactions_badRequest() throws Exception {
    
    String transactionData="{reference: \"REF-03\", amount: 5333}";
    
    mvc.perform(
        put("/transactions/-1000")
            .contentType(MediaType.APPLICATION_JSON)
            .content(transactionData)
    )
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @Order(3)
  void get_transactions_happyPath_after_update() throws Exception {
    
    Resource resource = resourceLoader.getResource("classpath:transactions_response_2.json");
    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    String expectedResponse = FileCopyUtils.copyToString(reader);

    mvc.perform(
        get("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
        )
        .andExpect(content()
            .string(expectedResponse)
        );
  }

  @Test
  @Order(4)
  void post_transactions_happyPathWithDefaults() throws Exception {

    String transactionData = "{}";
    
    String response = mvc.perform(
        post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(transactionData)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
          .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    
    TransactionDto respObj = objectMapper.readValue(response,TransactionDto.class);
    
    assertEquals(-1,respObj.getAmount());
    assertEquals("",respObj.getReference());
    assertEquals(transactionData,respObj.getData());
  }

  @Test
  @Order(5)
  void get_transactions_happyPath_after_create() throws Exception {
    
    Resource resource = resourceLoader.getResource("classpath:transactions_response_3.json");
    Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    String expectedResponse = FileCopyUtils.copyToString(reader);

    mvc.perform(
        get("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
        )
        .andExpect(content()
            .string(expectedResponse)
        );
  }

  @Test
  void post_transactions_happyPathWithValues() throws Exception {
    
    String transactionData="{reference: \"REF-01\", amount: 12}";
    
    String response = mvc.perform(
        post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(transactionData)
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
          .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);
    
    TransactionDto respObj = objectMapper.readValue(response,TransactionDto.class);
    
    assertEquals(12,respObj.getAmount());
    assertEquals("REF-01",respObj.getReference());
    assertEquals(transactionData,respObj.getData());
  }

}