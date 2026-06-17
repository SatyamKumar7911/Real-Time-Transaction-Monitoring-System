package com.example.transaction_service.client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.example.transaction_service.exception.DownstreamException;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class NotificationHttpClient {
  private final RestTemplate restTemplate;
  @Value("${notification.base-url}")
  private String baseUrl;
  public void notify(Map<String, Object> payload) {
    var url = baseUrl + "/api/v1/notify";
    try {
      var headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      restTemplate.postForEntity(url, new HttpEntity<>(payload, headers), Void.class);
    } catch (HttpStatusCodeException e) {
      throw new DownstreamException("Notification Service error: " + e.getResponseBodyAsString(),
        e.getStatusCode());
    }
  }
}