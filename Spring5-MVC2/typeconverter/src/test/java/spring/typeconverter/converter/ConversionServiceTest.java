package spring.typeconverter.converter;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import spring.typeconverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;
class ConversionServiceTest {
    @Test
    void conversionService(){
        // 등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        // 사용
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");

        assertThat(conversionService.convert("127.0.0.1:8080", IpPort.class))
                .isEqualTo(new IpPort("127.0.0.1", 8080));

        assertThat(conversionService.convert(new IpPort("127.0.0.1", 8080), String.class))
                .isEqualTo("127.0.0.1:8080");

    }
}
