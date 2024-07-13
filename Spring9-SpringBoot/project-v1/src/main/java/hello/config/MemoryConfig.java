package hello.config;

import memory.MemoryController;
import memory.MemoryFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {
    @Bean
    MemoryFinder memoryFinder(){
        return new MemoryFinder();
    }
    @Bean
    MemoryController memoryController(){
        return new MemoryController(memoryFinder());
    }
}
