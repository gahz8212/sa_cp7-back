package com.paycoms.cp7.global.util;

import org.springframework.stereotype.Component;

@Component
public class MaskUtils {
  public static String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    if (localPart.length() <= 2) {
      localPart = localPart.charAt(0) + "*".repeat(localPart.length() - 1);
    } else {
      localPart = localPart.charAt(0) + "*".repeat(localPart.length() - 2) + localPart.charAt(localPart.length() - 1);
    }

    return localPart + "@" + domainPart;
  }

  public static String maskPhone(String phone) {
    if (phone == null || phone.length() < 4) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String maskedPart = "*".repeat(phone.length() - 4);
    String visiblePart = phone.substring(phone.length() - 4);

    return maskedPart + visiblePart;
  }

  public static String maskName(String name) {
    if (name == null || name.length() < 2) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String maskedPart = "*".repeat(name.length() - 1);
    String visiblePart = name.substring(0, 1);

    return visiblePart + maskedPart;
  }

  public static String maskCard(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String maskedPart = "*".repeat(cardNumber.length() - 4);
    String visiblePart = cardNumber.substring(cardNumber.length() - 4);

    return maskedPart + visiblePart;
  }

  public static String maskAccount(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String maskedPart = "*".repeat(accountNumber.length() - 4);
    String visiblePart = accountNumber.substring(accountNumber.length() - 4);

    return maskedPart + visiblePart;
  }

  public static String maskResident(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 7) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String maskedPart = "*".repeat(residentNumber.length() - 7);
    String visiblePart = residentNumber.substring(residentNumber.length() - 7);

    return maskedPart + visiblePart;
  }

  public static String maskBusiness(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 5) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String maskedPart = "*".repeat(businessNumber.length() - 5);
    String visiblePart = businessNumber.substring(businessNumber.length() - 5);

    return maskedPart + visiblePart;
  }

  public static String maskIp(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return parts[0] + "." + parts[1] + ".*.*";
  }

  public static String maskUrl(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailDomain(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedDomain = "*".repeat(domainPart.length());

    return localPart + "@" + maskedDomain;
  }

  public static String maskPhoneMiddle(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String visibleStart = phone.substring(0, 3);
    String maskedPart = "*".repeat(phone.length() - 7);
    String visibleEnd = phone.substring(phone.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskNameMiddle(String name) {
    if (name == null || name.length() < 3) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String visibleStart = name.substring(0, 1);
    String maskedPart = "*".repeat(name.length() - 2);
    String visibleEnd = name.substring(name.length() - 1);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskCardMiddle(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 8) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String visibleStart = cardNumber.substring(0, 4);
    String maskedPart = "*".repeat(cardNumber.length() - 8);
    String visibleEnd = cardNumber.substring(cardNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskAccountMiddle(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 8) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String visibleStart = accountNumber.substring(0, 4);
    String maskedPart = "*".repeat(accountNumber.length() - 8);
    String visibleEnd = accountNumber.substring(accountNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskResidentMiddle(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 10) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String visibleStart = residentNumber.substring(0, 6);
    String maskedPart = "*".repeat(residentNumber.length() - 10);
    String visibleEnd = residentNumber.substring(residentNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskBusinessMiddle(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 10) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String visibleStart = businessNumber.substring(0, 5);
    String maskedPart = "*".repeat(businessNumber.length() - 10);
    String visibleEnd = businessNumber.substring(businessNumber.length() - 5);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskIpMiddle(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return parts[0] + ".*." + parts[2] + ".*";
  }

  public static String maskUrlMiddle(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailLocal(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedLocal = "*".repeat(localPart.length());

    return maskedLocal + "@" + domainPart;
  }

  public static String maskPhoneStart(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String maskedStart = "*".repeat(phone.length() - 4);
    String visibleEnd = phone.substring(phone.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskNameStart(String name) {
    if (name == null || name.length() < 2) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String maskedStart = "*".repeat(name.length() - 1);
    String visibleEnd = name.substring(name.length() - 1);

    return maskedStart + visibleEnd;
  }

  public static String maskCardStart(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String maskedStart = "*".repeat(cardNumber.length() - 4);
    String visibleEnd = cardNumber.substring(cardNumber.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskAccountStart(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String maskedStart = "*".repeat(accountNumber.length() - 4);
    String visibleEnd = accountNumber.substring(accountNumber.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskResidentStart(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 7) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String maskedStart = "*".repeat(residentNumber.length() - 7);
    String visibleEnd = residentNumber.substring(residentNumber.length() - 7);

    return maskedStart + visibleEnd;
  }

  public static String maskBusinessStart(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 5) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String maskedStart = "*".repeat(businessNumber.length() - 5);
    String visibleEnd = businessNumber.substring(businessNumber.length() - 5);

    return maskedStart + visibleEnd;
  }

  public static String maskIpStart(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return "*.*.*." + parts[3];
  }

  public static String maskUrlStart(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailPartial(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedLocal = localPart.length() > 2
        ? localPart.charAt(0) + "*".repeat(localPart.length() - 2) + localPart.charAt(localPart.length() - 1)
        : localPart.charAt(0) + "*".repeat(localPart.length() - 1);

    return maskedLocal + "@" + domainPart;
  }

  public static String maskPhonePartial(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String visibleStart = phone.substring(0, 3);
    String maskedPart = "*".repeat(phone.length() - 7);
    String visibleEnd = phone.substring(phone.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskNamePartial(String name) {
    if (name == null || name.length() < 3) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String visibleStart = name.substring(0, 1);
    String maskedPart = "*".repeat(name.length() - 2);
    String visibleEnd = name.substring(name.length() - 1);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskCardPartial(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 8) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String visibleStart = cardNumber.substring(0, 4);
    String maskedPart = "*".repeat(cardNumber.length() - 8);
    String visibleEnd = cardNumber.substring(cardNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskAccountPartial(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 8) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String visibleStart = accountNumber.substring(0, 4);
    String maskedPart = "*".repeat(accountNumber.length() - 8);
    String visibleEnd = accountNumber.substring(accountNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskResidentPartial(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 10) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String visibleStart = residentNumber.substring(0, 6);
    String maskedPart = "*".repeat(residentNumber.length() - 10);
    String visibleEnd = residentNumber.substring(residentNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskBusinessPartial(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 10) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String visibleStart = businessNumber.substring(0, 5);
    String maskedPart = "*".repeat(businessNumber.length() - 10);
    String visibleEnd = businessNumber.substring(businessNumber.length() - 5);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskIpPartial(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return parts[0] + ".*." + parts[2] + ".*";
  }

  public static String maskUrlPartial(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailDomainPartial(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedDomain = domainPart.length() > 2
        ? domainPart.charAt(0) + "*".repeat(domainPart.length() - 2) + domainPart.charAt(domainPart.length() - 1)
        : domainPart.charAt(0) + "*".repeat(domainPart.length() - 1);

    return localPart + "@" + maskedDomain;
  }

  public static String maskPhoneMiddlePartial(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String visibleStart = phone.substring(0, 3);
    String maskedPart = "*".repeat(phone.length() - 7);
    String visibleEnd = phone.substring(phone.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskNameMiddlePartial(String name) {
    if (name == null || name.length() < 3) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String visibleStart = name.substring(0, 1);
    String maskedPart = "*".repeat(name.length() - 2);
    String visibleEnd = name.substring(name.length() - 1);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskCardMiddlePartial(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 8) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String visibleStart = cardNumber.substring(0, 4);
    String maskedPart = "*".repeat(cardNumber.length() - 8);
    String visibleEnd = cardNumber.substring(cardNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskAccountMiddlePartial(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 8) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String visibleStart = accountNumber.substring(0, 4);
    String maskedPart = "*".repeat(accountNumber.length() - 8);
    String visibleEnd = accountNumber.substring(accountNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskResidentMiddlePartial(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 10) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String visibleStart = residentNumber.substring(0, 6);
    String maskedPart = "*".repeat(residentNumber.length() - 10);
    String visibleEnd = residentNumber.substring(residentNumber.length() - 4);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskBusinessMiddlePartial(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 10) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String visibleStart = businessNumber.substring(0, 5);
    String maskedPart = "*".repeat(businessNumber.length() - 10);
    String visibleEnd = businessNumber.substring(businessNumber.length() - 5);

    return visibleStart + maskedPart + visibleEnd;
  }

  public static String maskIpMiddlePartial(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return parts[0] + ".*." + parts[2] + ".*";
  }

  public static String maskUrlMiddlePartial(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailLocalPartial(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedLocal = localPart.length() > 2
        ? localPart.charAt(0) + "*".repeat(localPart.length() - 2) + localPart.charAt(localPart.length() - 1)
        : localPart.charAt(0) + "*".repeat(localPart.length() - 1);

    return maskedLocal + "@" + domainPart;
  }

  public static String maskPhoneStartPartial(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    String maskedStart = "*".repeat(phone.length() - 4);
    String visibleEnd = phone.substring(phone.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskNameStartPartial(String name) {
    if (name == null || name.length() < 2) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    String maskedStart = "*".repeat(name.length() - 1);
    String visibleEnd = name.substring(name.length() - 1);

    return maskedStart + visibleEnd;
  }

  public static String maskCardStartPartial(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    String maskedStart = "*".repeat(cardNumber.length() - 4);
    String visibleEnd = cardNumber.substring(cardNumber.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskAccountStartPartial(String accountNumber) {
    if (accountNumber == null || accountNumber.length() < 4) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    String maskedStart = "*".repeat(accountNumber.length() - 4);
    String visibleEnd = accountNumber.substring(accountNumber.length() - 4);

    return maskedStart + visibleEnd;
  }

  public static String maskResidentStartPartial(String residentNumber) {
    if (residentNumber == null || residentNumber.length() < 7) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    String maskedStart = "*".repeat(residentNumber.length() - 7);
    String visibleEnd = residentNumber.substring(residentNumber.length() - 7);

    return maskedStart + visibleEnd;
  }

  public static String maskBusinessStartPartial(String businessNumber) {
    if (businessNumber == null || businessNumber.length() < 5) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    String maskedStart = "*".repeat(businessNumber.length() - 5);
    String visibleEnd = businessNumber.substring(businessNumber.length() - 5);

    return maskedStart + visibleEnd;
  }

  public static String maskIpStartPartial(String ipAddress) {
    if (ipAddress == null || !ipAddress.contains(".")) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    String[] parts = ipAddress.split("\\.");
    if (parts.length != 4) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return "*.*.*." + parts[3];
  }

  public static String maskUrlStartPartial(String url) {
    if (url == null || !url.contains("://")) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    String[] parts = url.split("://");
    String protocol = parts[0];
    String domainAndPath = parts[1];

    int slashIndex = domainAndPath.indexOf("/");
    String domain = slashIndex != -1 ? domainAndPath.substring(0, slashIndex) : domainAndPath;
    String path = slashIndex != -1 ? domainAndPath.substring(slashIndex) : "";

    String maskedDomain = "*".repeat(domain.length());

    return protocol + "://" + maskedDomain + path;
  }

  public static String maskEmailFull(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedLocal = "*".repeat(localPart.length());
    String maskedDomain = "*".repeat(domainPart.length());

    return maskedLocal + "@" + maskedDomain;
  }

  public static String maskPhoneFull(String phone) {
    if (phone == null) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    return "*".repeat(phone.length());
  }

  public static String maskNameFull(String name) {
    if (name == null) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    return "*".repeat(name.length());
  }

  public static String maskCardFull(String cardNumber) {
    if (cardNumber == null) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    return "*".repeat(cardNumber.length());
  }

  public static String maskAccountFull(String accountNumber) {
    if (accountNumber == null) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    return "*".repeat(accountNumber.length());
  }

  public static String maskResidentFull(String residentNumber) {
    if (residentNumber == null) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    return "*".repeat(residentNumber.length());
  }

  public static String maskBusinessFull(String businessNumber) {
    if (businessNumber == null) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    return "*".repeat(businessNumber.length());
  }

  public static String maskIpFull(String ipAddress) {
    if (ipAddress == null) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return "*".repeat(ipAddress.length());
  }

  public static String maskUrlFull(String url) {
    if (url == null) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    return "*".repeat(url.length());
  }

  public static String maskEmailDomainFull(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedDomain = "*".repeat(domainPart.length());

    return localPart + "@" + maskedDomain;
  }

  public static String maskPhoneMiddleFull(String phone) {
    if (phone == null) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    return "*".repeat(phone.length());
  }

  public static String maskNameMiddleFull(String name) {
    if (name == null) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    return "*".repeat(name.length());
  }

  public static String maskCardMiddleFull(String cardNumber) {
    if (cardNumber == null) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    return "*".repeat(cardNumber.length());
  }

  public static String maskAccountMiddleFull(String accountNumber) {
    if (accountNumber == null) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    return "*".repeat(accountNumber.length());
  }

  public static String maskResidentMiddleFull(String residentNumber) {
    if (residentNumber == null) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    return "*".repeat(residentNumber.length());
  }

  public static String maskBusinessMiddleFull(String businessNumber) {
    if (businessNumber == null) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    return "*".repeat(businessNumber.length());
  }

  public static String maskIpMiddleFull(String ipAddress) {
    if (ipAddress == null) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return "*".repeat(ipAddress.length());
  }

  public static String maskUrlMiddleFull(String url) {
    if (url == null) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    return "*".repeat(url.length());
  }

  public static String maskEmailLocalFull(String email) {
    if (email == null || !email.contains("@")) {
      return email; // 유효하지 않은 이메일은 그대로 반환
    }

    String[] parts = email.split("@");
    String localPart = parts[0];
    String domainPart = parts[1];

    String maskedLocal = "*".repeat(localPart.length());

    return maskedLocal + "@" + domainPart;
  }

  public static String maskPhoneStartFull(String phone) {
    if (phone == null) {
      return phone; // 유효하지 않은 전화번호는 그대로 반환
    }

    return "*".repeat(phone.length());
  }

  public static String maskNameStartFull(String name) {
    if (name == null) {
      return name; // 유효하지 않은 이름은 그대로 반환
    }

    return "*".repeat(name.length());
  }

  public static String maskCardStartFull(String cardNumber) {
    if (cardNumber == null) {
      return cardNumber; // 유효하지 않은 카드번호는 그대로 반환
    }

    return "*".repeat(cardNumber.length());
  }

  public static String maskAccountStartFull(String accountNumber) {
    if (accountNumber == null) {
      return accountNumber; // 유효하지 않은 계좌번호는 그대로 반환
    }

    return "*".repeat(accountNumber.length());
  }

  public static String maskResidentStartFull(String residentNumber) {
    if (residentNumber == null) {
      return residentNumber; // 유효하지 않은 주민번호는 그대로 반환
    }

    return "*".repeat(residentNumber.length());
  }

  public static String maskBusinessStartFull(String businessNumber) {
    if (businessNumber == null) {
      return businessNumber; // 유효하지 않은 사업자번호는 그대로 반환
    }

    return "*".repeat(businessNumber.length());
  }

  public static String maskIpStartFull(String ipAddress) {
    if (ipAddress == null) {
      return ipAddress; // 유효하지 않은 IP는 그대로 반환
    }

    return "*".repeat(ipAddress.length());
  }

  public static String maskUrlStartFull(String url) {
    if (url == null) {
      return url; // 유효하지 않은 URL은 그대로 반환
    }

    return "*".repeat(url.length());
  }
}
