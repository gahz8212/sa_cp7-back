package com.paycoms.cp7.bankro.shinhan.service;

import org.springframework.stereotype.Service;
import com.paycoms.cp7.bankro.shinhan.common.ByteParser;
import com.paycoms.cp7.bankro.shinhan.model.Cts01Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts02Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts03Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts04Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts06Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts07Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts08Entity;
import com.paycoms.cp7.bankro.shinhan.model.Cts09Entity;
import com.paycoms.cp7.bankro.shinhan.model.Hck01Entity;
import com.paycoms.cp7.bankro.shinhan.model.Hck02Entity;
import com.paycoms.cp7.bankro.shinhan.model.Stc01Entity;
import com.paycoms.cp7.bankro.shinhan.model.Stc02Entity;
import com.paycoms.cp7.bankro.shinhan.model.Stc03Entity;
import com.paycoms.cp7.bankro.shinhan.model.Stc04Entity;

@Service
public class ServerService {
  public String processData(byte[] inputData) {
    Boolean isValid = ByteParser.validation(inputData);

    if (!isValid) {
      return "ERROR: Invalid data format";
    }

    String owpsMsgC = ByteParser.readOwpsMsgC(inputData);
    String result = "";

    switch (owpsMsgC) {
      case "HCK01":
        Hck01Entity hck01Entity = new Hck01Entity(inputData);
        break;

      case "HCK02":
        Hck02Entity hck02Entity = new Hck02Entity(inputData);
        break;

      case "CTS01":
        Cts01Entity cts01Entity = new Cts01Entity(inputData);
        break;

      case "CTS02":
        Cts02Entity cts02Entity = new Cts02Entity(inputData);
        break;

      case "CTS03":
        Cts03Entity cts03Entity = new Cts03Entity(inputData);
        break;

      case "CTS04":
        Cts04Entity cts04Entity = new Cts04Entity(inputData);
        break;

      case "CTS06":
        Cts06Entity cts06Entity = new Cts06Entity(inputData);
        break;

      case "CTS07":
        Cts07Entity cts07Entity = new Cts07Entity(inputData);
        break;

      case "CTS08":
        Cts08Entity cts08Entity = new Cts08Entity(inputData);
        break;

      case "CTS09":
        Cts09Entity cts09Entity = new Cts09Entity(inputData);
        break;

      case "STC01":
        Stc01Entity stc01Entity = new Stc01Entity(inputData);
        break;

      case "STC02":
        Stc02Entity stc02Entity = new Stc02Entity(inputData);
        break;

      case "STC03":
        Stc03Entity stc03Entity = new Stc03Entity(inputData);
        break;

      case "STC04":
        Stc04Entity stc04Entity = new Stc04Entity(inputData);
        break;

      default:
        result = "ERROR: Unknown owpsMsgC - " + owpsMsgC;
        break;
    }

    return ByteParser.readString(inputData, 0, 804); // 입력 데이터를 그대로 반환 (테스트용)
  }
}
