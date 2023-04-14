package com.example.recipe.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientDTO {
    Long id;
    @NotNull
    String name;
}
