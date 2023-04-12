package com.example.recipe.dto;

import com.example.recipe.domain.RecipeType;
import lombok.Data;

import java.util.List;

@Data
public class RecipeSearchDTO {
    String name;
    List<IngredientSearchDTO> ingredients;

    List<InstructionSearchDTO> instructions;

    RecipeType type;
    boolean typeEquals;

    Integer portion;
}
