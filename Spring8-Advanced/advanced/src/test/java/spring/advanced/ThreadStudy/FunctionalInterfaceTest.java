package spring.advanced.ThreadStudy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.*;
/**
 * 함수형 인터페이스 공부
 */
@Slf4j
public class FunctionalInterfaceTest {
    /**
     * Function<T, R>: 입력 타입 T를 받아 출력 타입 R을 반환하는 함수.
     */
    @Test
    void functionTest(){
        Function<String, Integer> function = (str) -> {
            return str.length();
        };
        Integer strLength = function.apply("길이가4");
        assertThat(strLength).isEqualTo(4);
    }
    /**
     * Predicate<T>: 입력 타입 T를 받아 boolean 값을 반환하는 함수.
     * StringUtils hasText() 반대 동작 만들기
     */
    @Test
    void predicateTest(){
        Predicate<String> predicate = (str)->{
            if(str == null || str.length() == 0){
                return true;
            }
            for(int i = 0; i < str.length(); i++){
                if(str.charAt(i) != ' ') return false;
            }
            return true;
        };

        boolean test1 = predicate.test("12");        // false
        boolean test2 = predicate.test("");          // true
        boolean test3 = predicate.test("    1");     // false
        boolean test4 = predicate.test("         "); // true

        assertThat(test1).isFalse();
        assertThat(test2).isTrue();
        assertThat(test3).isFalse();
        assertThat(test4).isTrue();
    }
}
