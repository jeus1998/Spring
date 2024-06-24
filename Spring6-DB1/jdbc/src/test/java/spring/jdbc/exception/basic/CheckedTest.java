package spring.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
@Slf4j
class CheckedTest {

    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }
    @Test
    void checked_throw(){
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     * MyCheckedException super(message) ->  Exception super(message) -> Throwable detailMessage 저장
     * getMessage() -> return detailMessage
     */
    static class MyCheckedException extends Exception{
        public MyCheckedException(String message) {
            super(message);
        }
    }
    static class MyCheckedException2 extends MyCheckedException{
        public MyCheckedException2(String message) {
            super(message);
        }
    }

    /**
     * checked 예외는 예외를 잡아서 처리하거나, 던지거나 둘중 하나를 필수로 선택해야 한다.
     */
    static class Service{
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
           try {
               repository.call();
           }
           catch (MyCheckedException e){
               // 예외 처리 로직
               // e.getMessage() return detailMessage
               log.info("예외처리, message={}", e.getMessage(), e);
           }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던질려면 throws
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException{
            repository.call();
        }
    }

    /**
     * 체크 예외는 잡거나 던지거나 둘중 하나이다.
     * throw : 실질적으로 예외를 던진다.
     * throws : 메서드 시그니처에 컴파일러가 해당 예외를 인지하도록 명시한다. (하위 예외 포함)
     */
    static class Repository {
        public void call() throws MyCheckedException{
            throw new MyCheckedException("ex");
        }
        public void call2() throws MyCheckedException{
            throw new MyCheckedException2("ex2");
        }
    }
}
