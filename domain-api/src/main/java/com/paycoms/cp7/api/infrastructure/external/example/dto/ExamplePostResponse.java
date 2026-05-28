package com.paycoms.cp7.api.infrastructure.external.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "샘플 POST 응답 DTO")
public class ExamplePostResponse {
  private int id;
  private String title;
  private String body;
  private int userId;
}