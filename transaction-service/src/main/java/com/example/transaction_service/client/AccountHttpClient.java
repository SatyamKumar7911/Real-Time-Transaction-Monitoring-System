package com.example.transaction_service.client;
import com.example.transaction_service.dto.*;
import com.example.transaction_service.exception.DownstreamException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
@Component
@RequiredArgsConstructor
public class AccountHttpClient {
  private final RestTemplate restTemplate;
  @Value("${account.base-url}")
  private String baseUrl;
  public BalanceResponse credit(CreditDebitRequest req) {
    var url = baseUrl + "/api/v1/accounts/credit";
    return postForBalance(url, req);
  }
  public BalanceResponse debit(CreditDebitRequest req) {
    var url = baseUrl + "/api/v1/accounts/debit";
    return postForBalance(url, req);
  }

  public BalanceResponse balance(String accountNumber) {
    var url = baseUrl + "/api/v1/accounts/" + accountNumber + "/balance";
    try {
      return restTemplate.getForObject(url, BalanceResponse.class);
    } catch (HttpStatusCodeException e) {
      throw new DownstreamException("Account Service error: " + e.getResponseBodyAsString(),
        e.getStatusCode());
    }
  }
  public void transfer(TransferRequest req) {
    var url = baseUrl + "/api/v1/accounts/transfer";
    try {
      restTemplate.postForEntity(url, req, Void.class);
    } catch (HttpStatusCodeException e) {
      throw new DownstreamException("Account Service error: " + e.getResponseBodyAsString(),
        e.getStatusCode());
    }
  }
  private BalanceResponse postForBalance(String url, Object body) {
    try {
      var headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      var entity = new HttpEntity<>(body, headers);
      var res = restTemplate.postForEntity(url, entity, BalanceResponse.class);
      return res.getBody();
    } catch (HttpStatusCodeException e) {
      throw new DownstreamException("Account Service error: " + e.getResponseBodyAsString(),
        e.getStatusCode());
    }
  }
}