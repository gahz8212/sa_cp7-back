package com.paycoms.cp7.global.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;
import com.paycoms.cp7.global.error.BusinessException;

/**
 * 입력값 형식 검증 유틸리티 클래스.
 *
 * <p>비밀번호·이메일·전화번호·URL 등 일반 형식 검증과
 * 사업자등록번호·주민등록번호·카드번호 체크섬 검증을 제공합니다.</p>
 */
public class ValidationUtils {

    /**
     * 비밀번호 복잡도 정규식.
     * 8자 이상이면서 영문 대문자·소문자·숫자·특수문자(!@#$%^&*) 중 3종류 이상을 혼합해야 합니다.
     */
    private static final String PASSWORD_PATTERN =
        "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])" +
        "|(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*])" +
        "|(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])" +
        "|(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])).{8,}$";

    /**
     * 비밀번호 형식이 유효한지 확인합니다.
     *
     * <p>8자 이상이며 영문 대문자·소문자·숫자·특수문자(!@#$%^&*) 중 3종류 이상을 혼합해야 합니다.
     * null·공백은 false를 반환합니다.</p>
     *
     * @param password 검증할 비밀번호
     * @return 조건을 만족하면 true
     */
    public static boolean isValidPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        Matcher matcher = Pattern.compile(PASSWORD_PATTERN).matcher(password);
        return matcher.matches();
    }

    /**
     * 비밀번호를 검증하고 조건 미충족 시 {@link BusinessException}을 발생시킵니다.
     *
     * <ul>
     *   <li>8자 미만이면 에러코드 {@code COMM_002}</li>
     *   <li>대문자·소문자·숫자·특수문자 중 3종류 미만이면 에러코드 {@code COMM_003}</li>
     * </ul>
     *
     * <p>단순 boolean 체크만 필요하면 {@link #isValidPassword(String)}를 사용하세요.</p>
     *
     * @param password 검증할 비밀번호
     * @throws BusinessException 비밀번호 조건 미충족 시
     */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("COMM_002");
        }

        int count = 0;
        if (password.matches(".*[A-Z].*")) count++;
        if (password.matches(".*[a-z].*")) count++;
        if (password.matches(".*[0-9].*")) count++;
        if (password.matches(".*[!@#$%^&*].*")) count++;

        if (count < 3) {
            throw new BusinessException("COMM_003");
        }
    }

    /** RFC 5322 경량 버전 이메일 패턴 */
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 국내 전화번호 패턴.
     * 지역번호(02, 031...) 또는 휴대폰(010, 011...)을 하이픈 유무 관계없이 허용합니다.
     * 예: 010-1234-5678, 01012345678, 02-1234-5678
     */
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^0\\d{1,2}[-]?\\d{3,4}[-]?\\d{4}$");

    /** http, https, ftp 스킴을 허용하는 URL 패턴 */
    private static final Pattern URL_PATTERN =
        Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);

    /** 숫자(0-9)만으로 구성되어 있는지 확인하는 패턴 */
    private static final Pattern NUMERIC_PATTERN =
        Pattern.compile("^\\d+$");

    /** 영문 대소문자와 숫자만으로 구성되어 있는지 확인하는 패턴 */
    private static final Pattern ALPHANUMERIC_PATTERN =
        Pattern.compile("^[A-Za-z0-9]+$");

    /**
     * 이메일 주소 형식이 유효한지 검증합니다.
     *
     * <p>로컬파트@도메인.TLD 구조를 확인하며, null·공백은 false를 반환합니다.</p>
     *
     * @param email 검증할 이메일 주소
     * @return 유효하면 true
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 국내 전화번호 형식이 유효한지 검증합니다.
     *
     * <p>하이픈 포함·미포함 모두 허용합니다.
     * 유효 예시: "010-1234-5678", "01012345678", "02-1234-5678"</p>
     *
     * @param phone 검증할 전화번호
     * @return 유효하면 true
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * URL 형식이 유효한지 검증합니다.
     *
     * <p>http, https, ftp 스킴만 허용합니다.</p>
     *
     * @param url 검증할 URL 문자열
     * @return 유효하면 true
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) return false;
        return URL_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * 문자열이 숫자(0-9)만으로 구성되어 있는지 확인합니다.
     *
     * <p>null·공백은 false를 반환합니다.</p>
     *
     * @param value 검증할 문자열
     * @return 숫자만 포함하면 true
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.isBlank()) return false;
        return NUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * 문자열이 영문 대소문자와 숫자만으로 구성되어 있는지 확인합니다.
     *
     * <p>null·공백은 false를 반환합니다.</p>
     *
     * @param value 검증할 문자열
     * @return 영숫자만 포함하면 true
     */
    public static boolean isAlphanumeric(String value) {
        if (value == null || value.isBlank()) return false;
        return ALPHANUMERIC_PATTERN.matcher(value).matches();
    }

    /**
     * 사업자등록번호의 형식과 체크섬이 유효한지 검증합니다.
     *
     * <p>하이픈 포함·미포함 모두 허용하며, 숫자만 추출하여 10자리 여부를 확인합니다.
     * 국세청 체크섬 알고리즘(가중치: 1,3,7,1,3,7,1,3,5)을 적용합니다.</p>
     *
     * <p>유효 예시: "123-45-67890", "1234567890"</p>
     *
     * @param businessNumber 검증할 사업자등록번호
     * @return 형식과 체크섬이 모두 유효하면 true
     */
    public static boolean isValidBusinessNumber(String businessNumber) {
        if (businessNumber == null) return false;
        String digits = businessNumber.replaceAll("[^0-9]", "");
        if (digits.length() != 10) return false;

        int[] weights = {1, 3, 7, 1, 3, 7, 1, 3, 5};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * weights[i];
        }
        // 9번째 자리의 가중치(5)와의 곱을 10으로 나눈 몫을 별도 합산
        sum += (int) Math.floor((digits.charAt(8) - '0') * 5 / 10.0);
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == (digits.charAt(9) - '0');
    }

    /**
     * 주민등록번호의 형식과 체크섬이 유효한지 검증합니다.
     *
     * <p>하이픈 포함·미포함 모두 허용하며, 숫자만 추출하여 13자리 여부를 확인합니다.
     * 행정안전부 체크섬 알고리즘(가중치: 2,3,4,5,6,7,8,9,2,3,4,5)을 적용합니다.</p>
     *
     * <p>유효 예시: "940315-1234567", "9403151234567"</p>
     *
     * @param residentNumber 검증할 주민등록번호
     * @return 형식과 체크섬이 모두 유효하면 true
     */
    public static boolean isValidResidentNumber(String residentNumber) {
        if (residentNumber == null) return false;
        String digits = residentNumber.replaceAll("[^0-9]", "");
        if (digits.length() != 13) return false;

        int[] weights = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (digits.charAt(i) - '0') * weights[i];
        }
        int checkDigit = (11 - (sum % 11)) % 10;
        return checkDigit == (digits.charAt(12) - '0');
    }

    /**
     * 신용카드 번호를 Luhn 알고리즘으로 검증합니다.
     *
     * <p>하이픈·공백을 제거한 숫자 13~19자리에 대해 Luhn 검사를 수행합니다.
     * Visa, MasterCard, AMEX 등 국제 표준 카드 번호에 적용 가능합니다.</p>
     *
     * <p>Luhn 알고리즘: 오른쪽에서 짝수 위치 자릿수를 2배 곱한 뒤 모두 합산,
     * 합계가 10의 배수이면 유효.</p>
     *
     * @param cardNumber 검증할 카드 번호 (하이픈 포함 가능)
     * @return Luhn 검사를 통과하면 true
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;
        String digits = cardNumber.replaceAll("[^0-9]", "");
        if (digits.length() < 13 || digits.length() > 19) return false;

        int sum = 0;
        boolean alternate = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    /**
     * 계좌번호 기본 형식을 검증합니다.
     *
     * <p>하이픈을 제거한 순수 숫자가 10~16자리인지만 확인합니다.
     * 은행별 세부 규칙은 은행 API를 통해 별도 검증하세요.</p>
     *
     * @param accountNumber 검증할 계좌번호
     * @return 유효한 자릿수이면 true
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        if (accountNumber == null) return false;
        String digits = accountNumber.replaceAll("[^0-9]", "");
        return digits.length() >= 10 && digits.length() <= 16;
    }

    /**
     * 문자열의 길이가 maxLength 이하인지 확인합니다.
     *
     * <p>null은 빈 문자열로 간주하여 true를 반환합니다.</p>
     *
     * @param value     검증할 문자열
     * @param maxLength 허용 최대 길이 (포함)
     * @return maxLength 이하이면 true
     */
    public static boolean isMaxLength(String value, int maxLength) {
        if (value == null) return true;
        return value.length() <= maxLength;
    }

    /**
     * 문자열의 길이가 minLength 이상인지 확인합니다.
     *
     * <p>null은 길이 0으로 간주하여 false를 반환합니다.</p>
     *
     * @param value     검증할 문자열
     * @param minLength 요구 최소 길이 (포함)
     * @return minLength 이상이면 true
     */
    public static boolean isMinLength(String value, int minLength) {
        if (value == null) return false;
        return value.length() >= minLength;
    }
}
