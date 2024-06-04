package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;

/**
 * MessageCodesResolver 동작 방식 이해
 */
class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /*
        messageCode = required.item
        messageCode = required
         */
        assertThat(messageCodes).containsExactly("required.item", "required");

       // BindingResult.reject() -> new ObjectError("item", new String[]{"required.item", "required"});
    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /*
        messageCode = required.item.itemName
        messageCode = required.itemName
        messageCode = required.java.lang.String
        messageCode = required
         */

        /*
        BindingResult.rejectValue -> newFiledError("itemName", new String []{"required.item.itemName, required.itemName",
            "required.java.lang.String", "required" });
         */
        assertThat(messageCodes).containsExactly(
"required.item.itemName",
        "required.itemName",
        "required.java.lang.String",
        "required");
    }
}
