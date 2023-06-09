package com.example.recipe.repository;

import com.example.recipe.domain.Recipe;
import com.example.recipe.domain.RecipeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeSpecification {


    List<Recipe> findAllByType(RecipeType type);
}