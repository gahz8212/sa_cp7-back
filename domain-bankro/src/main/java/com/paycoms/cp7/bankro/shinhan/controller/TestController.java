package com.paycoms.cp7.bankro.shinhan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paycoms.cp7.bankro.shinhan.config.TcpClientConfig.TcpClient;
import com.paycoms.cp7.bankro.shinhan.model.Cts03Entity;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.error.BusinessException;
import com.paycoms.cp7.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/tcp")
@RequiredArgsConstructor
public class TestController {
    @Autowired
    private TcpClient tcpClient;

    @Autowired
    private MessageUtils messageUtils;

    @GetMapping("/test-tcp")
    public ApiResponse<Cts03Entity> testTcp() {
        // 1. 보낼 전문 생성 (이미지 명세에 맞춰서 공백 포함 800바이트 문자열 조립)
        String requestMessage = "0800OWPSPCSCTS0326042425157585RKRCDEP10047[yudinf]해당 계좌가 존재하지 않습니다. 계좌번호[123455667667]                                       20260424181159                                                                                                                                                    088123455667667                                                                                                            09                                                                                                                                                                                                                                                                                                                                                                                       "; // 800바이트
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       // 전문

        // 2. 전송 및 수신
        byte[] response = tcpClient.sendAndReceive(requestMessage);

        if (response == null) {
            throw new BusinessException("SYS_403");
        }

        Cts03Entity result = new Cts03Entity(response);
        return messageUtils.createResponse("SYS_200", result);
    }
}
