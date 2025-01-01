package hello.itemservice.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TestDto {
    @NotBlank
    private String itemName;
    @NotNull
    @Range(min = 1000, max = 1000000)
    @JsonDeserialize(using = StringToIntegerValidationDeserializer.class)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
