package com.paycoms.cp7.bankro.shinhan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.paycoms.cp7.bankro.shinhan.service.ServerService;

@Configuration
@EnableIntegration
public class TcpServerConfig {
  @Value("${bankro.cp7.socket-server-port}")
  private int port;

  @Autowired
  private ServerService serverService;

  @Bean
  public TcpNioServerConnectionFactory connectionFactory() {
    TcpNioServerConnectionFactory factory = new TcpNioServerConnectionFactory(port);
    factory.setSerializer(new ByteArrayCrLfSerializer());
    factory.setDeserializer(new ByteArrayCrLfSerializer());
    factory.setUsingDirectBuffers(true);
    return factory;
  }

  @Bean
  public DirectChannel tcpInputChannel() {
    return new DirectChannel();
  }

  @Bean
  public TcpInboundGateway inboundGateway(TcpNioServerConnectionFactory connectionFactory,
      DirectChannel tcpInputChannel) {
    TcpInboundGateway gateway = new TcpInboundGateway();
    gateway.setConnectionFactory(connectionFactory);
    gateway.setRequestChannel(tcpInputChannel); // 핸들러로 보낼 채널 연결
    return gateway;
  }

  @ServiceActivator(inputChannel = "tcpInputChannel")
  public byte[] handleMessage(byte[] message) {
    String response = serverService.processData(message);
    return response.getBytes(); // 바이트 배열로 리턴
  }
}
