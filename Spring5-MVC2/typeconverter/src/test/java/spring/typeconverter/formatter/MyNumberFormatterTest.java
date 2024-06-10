package spring.typeconverter.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.text.ParseException;
import java.util.Locale;
import static org.assertj.core.api.Assertions.*;

class MyNumberFormatterTest {
    MyNumberFormatter formatter = new MyNumberFormatter();
    @Test
    @DisplayName("String -> Integer (parse)")
    void parse() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L); // Long 타입 주의
    }

    @Test
    @DisplayName("Integer -> String (print)")
    void print() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }
}