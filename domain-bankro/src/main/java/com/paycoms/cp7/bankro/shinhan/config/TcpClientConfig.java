package com.paycoms.cp7.bankro.shinhan.config;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableIntegration
public class TcpClientConfig {
  @Value("${bankro.shinhan.socket-server-host}")
  private String SERVER_IP;
  @Value("${bankro.shinhan.socket-server-port}")
  private int SERVER_PORT;

  private static final int TIMEOUT = 15000; // 15초 타임아웃

  @PostConstruct
  public void init() {
    System.out.println("SERVER_IP: " + SERVER_IP);
    System.out.println("SERVER_PORT: " + SERVER_PORT);
  }

  @Bean
  public TcpClient tcpClient() {
    return new TcpClient(SERVER_IP, SERVER_PORT);
  }

  public static class TcpClient {
    private String serverIp;
    private int serverPort;

    public TcpClient(String serverIp, int serverPort) {
      this.serverIp = serverIp;
      this.serverPort = serverPort;
    }

    public byte[] sendAndReceive(String message) {
      // EUC-KR 인코딩 설정
      Charset eucKr = Charset.forName("EUC-KR");
      byte[] requestBytes = (message + "\r\n").getBytes(eucKr); // CRLF 추가
      byte[] responseBytes = new byte[804]; // 응답 규격이 800바이트라고 가정

      try (Socket socket = new Socket()) {
        // 1. 서버 연결
        socket.connect(new InetSocketAddress(this.serverIp, this.serverPort), TIMEOUT);
        socket.setSoTimeout(TIMEOUT); // 읽기 타임아웃 설정

        // 2. 데이터 전송
        OutputStream os = socket.getOutputStream();
        os.write(requestBytes);
        os.flush();

        // 3. 데이터 수신
        InputStream is = socket.getInputStream();
        int readByteCount = is.read(responseBytes);

        if (readByteCount == -1) {
          throw new IOException("서버로부터 응답을 받지 못했습니다.");
        }

        return responseBytes;

      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
}
