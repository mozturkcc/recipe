package com.example.recipe.dto;

import lombok.Data;

@Data
public class IngredientSearchDTO{
    Long id;
    String name;
    boolean contains;
}
