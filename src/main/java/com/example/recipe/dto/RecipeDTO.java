package com.example.recipe.dto;

import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Instruction;
import com.example.recipe.domain.RecipeType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {
    Long id;
    String name;

    List<IngredientDTO> ingredients;

    List<InstructionDTO> instructions;

    RecipeType type;

    int portion;
}
