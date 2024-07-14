package hello;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 스프링 부트는 ApplicationArguments 구현체인 DefaultApplicationArguments 를 스프링 빈으로 제공한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandLineBean {
    private final ApplicationArguments arguments;

    @PostConstruct
    public void init(){
        log.info("arguments {}", arguments.getClass());
        log.info("source {}", List.of(arguments.getSourceArgs()));
        log.info("optionNames {}", arguments.getOptionNames());
        Set<String> optionNames = arguments.getOptionNames();
        for (String optionName : optionNames) {
            log.info("option args {}={}", optionName, arguments.getOptionValues(optionName));
        }
    }

}
