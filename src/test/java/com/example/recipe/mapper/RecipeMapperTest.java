package com.example.recipe.mapper;

import org.junit.jupiter.api.BeforeEach;

public class RecipeMapperTest {
    private RecipeMapper recipeMapper;

    @BeforeEach
    public void setUp() {
        recipeMapper = new RecipeMapperImpl();
    }
}
