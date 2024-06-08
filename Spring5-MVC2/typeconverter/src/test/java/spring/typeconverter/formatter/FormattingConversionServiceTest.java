package spring.typeconverter.formatter;

import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;
import spring.typeconverter.converter.IpPortToStringConverter;
import spring.typeconverter.converter.StringToIpPortConverter;
import spring.typeconverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;

public class FormattingConversionServiceTest {
    @Test
    void formattingConversionService(){
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        // 컨버터 등록
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        // 포멧터 등록
        conversionService.addFormatter(new MyNumberFormatter());

        // 컨버터 사용
        assertThat(conversionService.convert(new IpPort("127.0.0.0.1", 8080), String.class))
                .isEqualTo("127.0.0.0.1:8080");

        // 포멧터 사용
        assertThat(conversionService.convert("1,000", Integer.class)).isEqualTo(1000);
        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");

    }

}
