package com.example.product.dto.request;
 
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class ReqProduct {
    @NotBlank(message = "Name could not be empty or null.")
	@Size(min=2, max=30)
    private String name;

    @NotNull(message = "Price cannot be empty or null.")
	@Min(10)
    private Integer price;
     
    @NotNull(message = "Qty cannot be empty or null.")
	@Min(1)
    private Integer qty;
}
