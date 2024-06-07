package spring.exception;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import spring.exception.filter.LogFilter;
import spring.exception.interceptor.LogInterceptor;
import spring.exception.resolver.MyHandlerExceptionResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
         resolvers.add(new MyHandlerExceptionResolver());
    }

    /**
     * addInterceptors() 에서는 DispatcherType 관련 수행이 불가능하다.
     * -> excludePatterns 에서 WebServerCustomizer 에서 만들어둔 에러 페이지 경로를 넣어준다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "*.ico");
                // .excludePathPatterns("/css/**", "*.ico", "/error", "/error-page/**");
    }

    /**
     * filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
     * 해당 Filter DispatcherType.REQUEST, ERROR -> 호출
     * default: REQUEST
     */
    // @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterRegistrationBean;
    }
}
