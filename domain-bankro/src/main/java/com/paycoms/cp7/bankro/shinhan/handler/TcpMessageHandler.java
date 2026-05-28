package com.paycoms.cp7.bankro.shinhan.handler;

import org.springframework.stereotype.Component;

import com.paycoms.cp7.bankro.shinhan.service.ServerService;

import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class TcpMessageHandler {
  private final ServerService serverService;

  public TcpMessageHandler(ServerService serverService) {
    this.serverService = serverService;
  }

  @ServiceActivator(inputChannel = "tcpInputChannel")
  public String handleMessage(byte[] payload) {
    String message = new String(payload, Charset.forName("EUC-KR"));

    try {
      int textLength = Integer.parseInt(message.substring(0, 4));

      byte[] utf8Bytes = message.substring(4).getBytes(Charset.forName("EUC-KR"));

      if (textLength != utf8Bytes.length) {
        return "ERROR: Length Mismatch";
      }
    } catch (Exception e) {
      return "ERROR: " + e.getMessage();
    }

    return serverService.processData(payload);
  }
}
