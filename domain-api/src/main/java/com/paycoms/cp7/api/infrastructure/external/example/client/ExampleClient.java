package com.paycoms.cp7.api.infrastructure.external.example.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.paycoms.cp7.api.infrastructure.external.example.dto.ExamplePostRequest;
import com.paycoms.cp7.api.infrastructure.external.example.dto.ExamplePostResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

// FeignClient 인터페이스 정의
// 비동기 방식은 WebClient 사용 권장
@FeignClient(name = "ExampleClient", url = "https://jsonplaceholder.typicode.com")
public interface ExampleClient {
  @GetMapping("/posts/{userId}")
  Map<String, Object> getExample(@PathVariable("userId") Long userId, @RequestParam("authKey") String authKey);

  @PostMapping("/posts/{userId}")
  ExamplePostResponse postExample(@RequestBody ExamplePostRequest request);
}
