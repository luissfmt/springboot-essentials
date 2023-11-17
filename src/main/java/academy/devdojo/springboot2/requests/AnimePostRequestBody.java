package academy.devdojo.springboot2.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {
  @NotBlank(message = "The anime name cannot be blank or null")
  @Schema(description = "This is the anime's name", example = "Tensei Shittara Slime Datta Ken")
  private String name;
}
