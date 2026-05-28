package com.paycoms.cp7.global.util;

import java.util.List;
import java.util.Map;

/**
 * 특정 도메인에 속하지 않는 범용 유틸리티 클래스.
 *
 * <ul>
 *   <li>비밀번호 검증 → {@link ValidationUtils}</li>
 *   <li>숫자 파싱·포맷 → {@link NumberUtils}</li>
 *   <li>날짜 처리 → {@link DateUtils}</li>
 *   <li>마스킹 → {@link MaskUtils}</li>
 * </ul>
 */
public class Utils {

    /**
     * 객체가 비어있는지 확인합니다.
     *
     * <p>지원 타입별 동작:</p>
     * <ul>
     *   <li>{@code null} → true</li>
     *   <li>{@link String} → trim 후 빈 문자열이면 true</li>
     *   <li>{@link List} → 요소가 없으면 true</li>
     *   <li>{@link Map} → 항목이 없으면 true</li>
     *   <li>배열({@code Object[]}) → 길이가 0이면 true</li>
     *   <li>그 외 → false</li>
     * </ul>
     *
     * @param obj 검사할 객체 (null 허용)
     * @return 비어있으면 true
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;

        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        if (obj instanceof List) {
            return ((List<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length == 0;
        }

        return false;
    }

    /**
     * 객체가 비어있지 않은지 확인합니다.
     *
     * <p>{@link #isEmpty(Object)}의 반대 결과를 반환합니다.</p>
     *
     * @param obj 검사할 객체 (null 허용)
     * @return 값이 있으면 true
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
