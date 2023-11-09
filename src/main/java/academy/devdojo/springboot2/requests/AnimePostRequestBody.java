package academy.devdojo.springboot2.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimePostRequestBody {
  @NotBlank(message = "The anime name cannot be blank or null")
  private String name;
}
