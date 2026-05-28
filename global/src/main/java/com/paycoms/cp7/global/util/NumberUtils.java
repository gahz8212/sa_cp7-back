package com.paycoms.cp7.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 숫자 파싱·포맷팅·계산 유틸리티 클래스.
 *
 * <p>안전한 문자열→숫자 변환, 금액·퍼센트 포맷팅,
 * BigDecimal 기반 반올림/나눗셈 등 금융 도메인에서 자주 쓰이는 연산을 제공합니다.</p>
 *
 * <p>{@link DecimalFormat}은 thread-safe하지 않으므로 메소드 호출마다 새 인스턴스를 생성합니다.</p>
 */
public class NumberUtils {

    /** 천 단위 구분자 포맷 (예: 1,234,567) */
    private static final String AMOUNT_PATTERN = "#,###";

    /** 원화 기호 */
    private static final String WON_SIGN = "₩";

    // DecimalFormat은 thread-safe하지 않으므로 공유 인스턴스를 두지 않고 호출마다 생성합니다.
    private static DecimalFormat amountFormat() {
        return new DecimalFormat(AMOUNT_PATTERN);
    }

    /**
     * 문자열을 {@code int}로 변환하고, 실패하면 기본값을 반환합니다.
     *
     * <p>null·공백·숫자 이외 문자가 포함된 경우 defaultValue를 반환합니다.</p>
     *
     * @param value        변환할 문자열
     * @param defaultValue 변환 실패 시 반환할 기본값
     * @return 변환된 int 값 또는 defaultValue
     */
    public static int parseInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 문자열을 {@code long}으로 변환하고, 실패하면 기본값을 반환합니다.
     *
     * <p>null·공백·숫자 이외 문자가 포함된 경우 defaultValue를 반환합니다.</p>
     *
     * @param value        변환할 문자열
     * @param defaultValue 변환 실패 시 반환할 기본값
     * @return 변환된 long 값 또는 defaultValue
     */
    public static long parseLong(String value, long defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 문자열을 {@code double}로 변환하고, 실패하면 기본값을 반환합니다.
     *
     * <p>null·공백·숫자 이외 문자가 포함된 경우 defaultValue를 반환합니다.</p>
     *
     * @param value        변환할 문자열
     * @param defaultValue 변환 실패 시 반환할 기본값
     * @return 변환된 double 값 또는 defaultValue
     */
    public static double parseDouble(String value, double defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 문자열을 {@link BigDecimal}로 변환하고, 실패하면 기본값을 반환합니다.
     *
     * <p>금액처럼 정밀도가 중요한 경우 double 대신 이 메소드를 사용하세요.</p>
     *
     * @param value        변환할 문자열
     * @param defaultValue 변환 실패 시 반환할 기본값 (null 허용)
     * @return 변환된 {@link BigDecimal} 또는 defaultValue
     */
    public static BigDecimal parseBigDecimal(String value, BigDecimal defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * {@code long} 금액을 천 단위 구분자 형식으로 포맷합니다.
     *
     * <p>예: 1234567 → "1,234,567"</p>
     *
     * @param amount 포맷할 금액
     * @return 천 단위 구분자가 포함된 문자열
     */
    public static String formatAmount(long amount) {
        return amountFormat().format(amount);
    }

    /**
     * {@link BigDecimal} 금액을 천 단위 구분자 형식으로 포맷합니다.
     *
     * <p>예: 1234567 → "1,234,567". null이면 "0"을 반환합니다.</p>
     *
     * @param amount 포맷할 금액 (null 허용)
     * @return 천 단위 구분자가 포함된 문자열
     */
    public static String formatAmount(BigDecimal amount) {
        if (amount == null) return "0";
        return amountFormat().format(amount);
    }

    /**
     * {@code long} 금액을 원화 기호와 천 단위 구분자 형식으로 포맷합니다.
     *
     * <p>예: 1234567 → "₩1,234,567"</p>
     *
     * @param amount 포맷할 금액
     * @return 원화 기호가 포함된 문자열
     */
    public static String formatCurrency(long amount) {
        return WON_SIGN + amountFormat().format(amount);
    }

    /**
     * {@link BigDecimal} 금액을 원화 기호와 천 단위 구분자 형식으로 포맷합니다.
     *
     * <p>예: 1234567 → "₩1,234,567". null이면 "₩0"을 반환합니다.</p>
     *
     * @param amount 포맷할 금액 (null 허용)
     * @return 원화 기호가 포함된 문자열
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return WON_SIGN + "0";
        return WON_SIGN + amountFormat().format(amount);
    }

    /**
     * 비율(0.0~1.0)을 퍼센트 문자열로 포맷합니다.
     *
     * <p>예: 0.125 → "12.5%", 1.0 → "100.0%"</p>
     *
     * @param ratio 0.0~1.0 사이의 비율 값
     * @return 소수점 1자리 퍼센트 문자열
     */
    public static String formatPercent(double ratio) {
        return String.format("%.1f%%", ratio * 100);
    }

    /**
     * {@code double}을 지정한 소수점 자릿수로 반올림합니다.
     *
     * <p>HALF_UP 반올림(일반 반올림) 방식을 사용합니다.
     * 예: roundTo(1.2345, 2) → 1.23</p>
     *
     * @param value 반올림할 값
     * @param scale 소수점 자릿수
     * @return 반올림된 double 값
     */
    public static double roundTo(double value, int scale) {
        return BigDecimal.valueOf(value)
            .setScale(scale, RoundingMode.HALF_UP)
            .doubleValue();
    }

    /**
     * {@link BigDecimal}을 지정한 소수점 자릿수로 반올림합니다.
     *
     * <p>HALF_UP 반올림 방식을 사용하며, null이면 {@link BigDecimal#ZERO}를 반환합니다.</p>
     *
     * @param value 반올림할 값 (null 허용)
     * @param scale 소수점 자릿수
     * @return 반올림된 {@link BigDecimal}
     */
    public static BigDecimal roundTo(BigDecimal value, int scale) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 정수 값을 [min, max] 범위 안으로 제한합니다.
     *
     * <p>예: clamp(150, 0, 100) → 100, clamp(-5, 0, 100) → 0</p>
     *
     * @param value 제한할 값
     * @param min   최솟값 (포함)
     * @param max   최댓값 (포함)
     * @return 범위 내로 제한된 값
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * {@code long} 값을 [min, max] 범위 안으로 제한합니다.
     *
     * @param value 제한할 값
     * @param min   최솟값 (포함)
     * @param max   최댓값 (포함)
     * @return 범위 내로 제한된 값
     */
    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * {@code long} 값이 0보다 큰지 확인합니다.
     *
     * @param value 확인할 값
     * @return 양수이면 true
     */
    public static boolean isPositive(long value) {
        return value > 0;
    }

    /**
     * {@code long} 값이 0보다 작은지 확인합니다.
     *
     * @param value 확인할 값
     * @return 음수이면 true
     */
    public static boolean isNegative(long value) {
        return value < 0;
    }

    /**
     * {@link BigDecimal} 값이 0보다 큰지 확인합니다.
     *
     * <p>null이면 false를 반환합니다.</p>
     *
     * @param value 확인할 값 (null 허용)
     * @return 양수이면 true
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * {@link BigDecimal} 값이 0보다 작은지 확인합니다.
     *
     * <p>null이면 false를 반환합니다.</p>
     *
     * @param value 확인할 값 (null 허용)
     * @return 음수이면 true
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * {@link BigDecimal} 값이 0 이하(0 또는 음수)인지 확인합니다.
     *
     * <p>결제 금액이 유효한지 체크할 때 활용합니다. null이면 true를 반환합니다.</p>
     *
     * @param value 확인할 값 (null 허용)
     * @return 0 이하이거나 null이면 true
     */
    public static boolean isZeroOrNegative(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * 0으로 나누는 경우를 방어하여 안전하게 나눗셈을 수행합니다.
     *
     * <p>divisor가 null이거나 0이면 {@link BigDecimal#ZERO}를 반환합니다.
     * HALF_UP 반올림을 적용합니다.</p>
     *
     * @param dividend 나누어지는 수
     * @param divisor  나누는 수 (null·0 허용)
     * @param scale    소수점 자릿수
     * @return 나눗셈 결과, divisor가 0이면 {@link BigDecimal#ZERO}
     */
    public static BigDecimal safeDivide(BigDecimal dividend, BigDecimal divisor, int scale) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
    }

    /**
     * part / total × 100 으로 퍼센트를 계산합니다.
     *
     * <p>total이 null이거나 0이면 {@link BigDecimal#ZERO}를 반환합니다.
     * HALF_UP 반올림을 적용합니다.</p>
     *
     * <p>예: calcPercent(25, 200, 1) → 12.5</p>
     *
     * @param part  분자 (부분 값)
     * @param total 분모 (전체 값), null·0 허용
     * @param scale 소수점 자릿수
     * @return 퍼센트 값 (0~100 범위), total이 0이면 0
     */
    public static BigDecimal calcPercent(BigDecimal part, BigDecimal total, int scale) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return part.multiply(BigDecimal.valueOf(100))
            .divide(total, scale, RoundingMode.HALF_UP);
    }
}
