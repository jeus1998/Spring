package hello.config;

import hello.datasource.MyDataSource;
import hello.datasource.MyDataSourcePropertiesV4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @EnableConfigurationProperties-> EnableConfigurationPropertiesRegistrar -> ConfigurationPropertiesBindingPostProcessor
 * ConfigurationPropertiesBindingPostProcessor 여기에서 binder를 주입받고 바인딩 및 validator 검사를 함
 * 에러 발생!!!
 * @ConfigurationPropertiesScan or @EnableConfigurationProperties 무조건 사용해야함
 */
@Slf4j
@Import(MyDataSourcePropertiesV4.class)
@Configuration
public class MyDataSourceConfigV4 {
    private final MyDataSourcePropertiesV4 properties;
    public MyDataSourceConfigV4(MyDataSourcePropertiesV4 properties) {
        this.properties = properties;
    }
    @Bean
    public MyDataSource dataSource(){
        return new MyDataSource(
                properties.getUrl(),
                properties.getUsername(),
                properties.getPassword(),
                properties.getEtc().getMaxConnection(),
                properties.getEtc().getTimeout(),
                properties.getEtc().getOptions());
    }

}
