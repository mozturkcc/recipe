package com.example.recipe.repository;

import com.example.recipe.domain.Recipe;
import com.example.recipe.dto.RecipeSearchDTO;

import java.util.List;

public interface RecipeSpecification {

    //filer recipes by {RecipeSearchDTO}
    List<Recipe> filterRecipes(RecipeSearchDTO recipeSearchDTO);


}
