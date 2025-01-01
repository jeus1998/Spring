package hello.itemservice.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Slf4j
@Component
public class StringToIntegerValidationDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = p.getText();
        log.info("text={}", text);
        try {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException e){
             throw new CustomException("test");
        }
    }
}
