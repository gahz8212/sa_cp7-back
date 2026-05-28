package com.paycoms.cp7.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 날짜/시간 관련 유틸리티 클래스.
 *
 * <p>Java 8+ {@link LocalDate} / {@link LocalDateTime} 기반으로 작성되었으며,
 * 포맷팅, 파싱, 나이 계산, 만료 여부 확인 등 공통 날짜 연산을 제공합니다.</p>
 */
public class DateUtils {

    /** 기본 날짜 포맷 (예: 2025-01-15) */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /** 기본 날짜+시간 포맷 (예: 2025-01-15 09:30:00) */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 압축 날짜 포맷 (예: 20250115) — 배치·파일명 등에 자주 사용 */
    public static final String COMPACT_DATE_FORMAT = "yyyyMMdd";

    /** 압축 날짜+시간 포맷 (예: 20250115093000) */
    public static final String COMPACT_DATETIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 현재 시스템 날짜+시간을 반환합니다.
     *
     * @return 현재 {@link LocalDateTime}
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 현재 시스템 날짜(시간 제외)를 반환합니다.
     *
     * @return 현재 {@link LocalDate}
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * {@link LocalDateTime}을 지정한 패턴의 문자열로 변환합니다.
     *
     * @param dateTime 변환할 날짜+시간 (null 이면 null 반환)
     * @param pattern  {@link DateTimeFormatter} 패턴 (예: "yyyy/MM/dd HH:mm")
     * @return 포맷된 문자열
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * {@link LocalDate}를 지정한 패턴의 문자열로 변환합니다.
     *
     * @param date    변환할 날짜 (null 이면 null 반환)
     * @param pattern {@link DateTimeFormatter} 패턴 (예: "yyyy/MM/dd")
     * @return 포맷된 문자열
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * {@link LocalDateTime}을 기본 포맷(yyyy-MM-dd HH:mm:ss)으로 변환합니다.
     *
     * @param dateTime 변환할 날짜+시간
     * @return "2025-01-15 09:30:00" 형태의 문자열
     */
    public static String formatDefault(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * {@link LocalDate}를 기본 포맷(yyyy-MM-dd)으로 변환합니다.
     *
     * @param date 변환할 날짜
     * @return "2025-01-15" 형태의 문자열
     */
    public static String formatDefault(LocalDate date) {
        return format(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 문자열을 지정한 패턴으로 파싱하여 {@link LocalDateTime}으로 반환합니다.
     *
     * @param value   파싱할 문자열 (null·공백이면 null 반환)
     * @param pattern {@link DateTimeFormatter} 패턴
     * @return 파싱된 {@link LocalDateTime}
     */
    public static LocalDateTime parseDateTime(String value, String pattern) {
        if (value == null || value.isBlank()) return null;
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 문자열을 지정한 패턴으로 파싱하여 {@link LocalDate}로 반환합니다.
     *
     * @param value   파싱할 문자열 (null·공백이면 null 반환)
     * @param pattern {@link DateTimeFormatter} 패턴
     * @return 파싱된 {@link LocalDate}
     */
    public static LocalDate parseDate(String value, String pattern) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * "yyyy-MM-dd" 형식의 문자열을 {@link LocalDate}로 파싱합니다.
     *
     * @param value "2025-01-15" 형태의 날짜 문자열
     * @return 파싱된 {@link LocalDate}
     */
    public static LocalDate parseDate(String value) {
        return parseDate(value, DEFAULT_DATE_FORMAT);
    }

    /**
     * 생년월일을 기준으로 오늘 날짜 기준 만 나이를 계산합니다.
     *
     * <p>생일이 아직 지나지 않았으면 나이를 1 감산합니다.</p>
     *
     * @param birthDate 생년월일 (null 이면 0 반환)
     * @return 만 나이
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    /**
     * 주민등록번호 앞 6자리(YYMMDD)와 성별 코드로 실제 생년월일을 파싱합니다.
     *
     * <ul>
     *   <li>성별 코드 1, 2 → 1900년대 출생</li>
     *   <li>성별 코드 3, 4 → 2000년대 출생</li>
     * </ul>
     *
     * @param yymmdd      주민번호 앞 6자리 (예: "940315")
     * @param genderDigit 주민번호 7번째 자리 (예: "1", "2", "3", "4")
     * @return 파싱된 {@link LocalDate}, 입력이 잘못된 경우 null
     */
    public static LocalDate parseResidentBirthDate(String yymmdd, String genderDigit) {
        if (yymmdd == null || yymmdd.length() != 6) return null;
        int yy = Integer.parseInt(yymmdd.substring(0, 2));
        int mm = Integer.parseInt(yymmdd.substring(2, 4));
        int dd = Integer.parseInt(yymmdd.substring(4, 6));
        int century = (genderDigit != null && (genderDigit.equals("3") || genderDigit.equals("4"))) ? 2000 : 1900;
        return LocalDate.of(century + yy, mm, dd);
    }

    /**
     * 두 날짜 사이의 일수 차이를 반환합니다. (to - from)
     *
     * <p>예: from=2025-01-01, to=2025-01-10 → 9</p>
     *
     * @param from 시작 날짜
     * @param to   종료 날짜
     * @return 일수 차이 (음수 가능)
     */
    public static long diffDays(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    /**
     * 두 날짜 사이의 개월 수 차이를 반환합니다. (to - from)
     *
     * @param from 시작 날짜
     * @param to   종료 날짜
     * @return 개월 수 차이 (음수 가능)
     */
    public static long diffMonths(LocalDate from, LocalDate to) {
        return ChronoUnit.MONTHS.between(from, to);
    }

    /**
     * 날짜에 지정한 일수를 더합니다.
     *
     * @param date 기준 날짜
     * @param days 더할 일수 (음수면 이전 날짜)
     * @return 계산된 {@link LocalDate}
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * 날짜에 지정한 개월 수를 더합니다.
     *
     * @param date   기준 날짜
     * @param months 더할 개월 수 (음수면 이전 날짜)
     * @return 계산된 {@link LocalDate}
     */
    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    /**
     * 특정 날짜의 시작 시각(00:00:00)을 반환합니다.
     *
     * <p>하루 범위 조회 쿼리의 시작 조건에 활용합니다.</p>
     *
     * @param date 기준 날짜
     * @return 해당 날짜의 {@link LocalDateTime} 00:00:00
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 특정 날짜의 종료 시각(23:59:59)을 반환합니다.
     *
     * <p>하루 범위 조회 쿼리의 종료 조건에 활용합니다.</p>
     *
     * @param date 기준 날짜
     * @return 해당 날짜의 {@link LocalDateTime} 23:59:59
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /**
     * 주어진 만료 일시가 현재 시각 이전인지(이미 만료됐는지) 확인합니다.
     *
     * @param expiryDateTime 만료 일시
     * @return 만료됐으면 true
     */
    public static boolean isExpired(LocalDateTime expiryDateTime) {
        return LocalDateTime.now().isAfter(expiryDateTime);
    }

    /**
     * target 일시가 start 이상이고 end 이하인지 확인합니다. (양 끝 포함)
     *
     * @param target 검사 대상 일시
     * @param start  범위 시작 일시
     * @param end    범위 종료 일시
     * @return 범위 내이면 true
     */
    public static boolean isBetween(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

    /**
     * {@link LocalDateTime}을 Unix epoch 밀리초로 변환합니다.
     *
     * <p>시스템 기본 타임존을 기준으로 변환합니다.</p>
     *
     * @param dateTime 변환할 날짜+시간
     * @return epoch 밀리초 값
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Unix epoch 밀리초를 {@link LocalDateTime}으로 변환합니다.
     *
     * <p>시스템 기본 타임존을 기준으로 변환합니다.</p>
     *
     * @param epochMilli epoch 밀리초 값
     * @return 변환된 {@link LocalDateTime}
     */
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    /**
     * {@link LocalDate}를 한국어 형식 문자열로 반환합니다.
     *
     * <p>예: 2025-01-15 → "2025년 1월 15일"</p>
     *
     * @param date 변환할 날짜 (null 이면 null 반환)
     * @return 한국어 날짜 문자열
     */
    public static String toKoreanDate(LocalDate date) {
        if (date == null) return null;
        return date.getYear() + "년 " + date.getMonthValue() + "월 " + date.getDayOfMonth() + "일";
    }

    /**
     * "yyyyMMdd" 형식의 압축 날짜 문자열을 {@link LocalDate}로 파싱합니다.
     *
     * <p>배치 파일명, 전문 날짜 필드 등 압축 형식에 사용합니다. (예: "20250115")</p>
     *
     * @param yyyymmdd 8자리 날짜 문자열
     * @return 파싱된 {@link LocalDate}
     */
    public static LocalDate parseCompact(String yyyymmdd) {
        return parseDate(yyyymmdd, COMPACT_DATE_FORMAT);
    }
}
