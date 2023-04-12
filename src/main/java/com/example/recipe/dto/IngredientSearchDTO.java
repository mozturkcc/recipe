package com.example.recipe.dto;

import lombok.Data;

@Data
public class IngredientSearchDTO extends IngredientDTO{
    boolean contains;
}
