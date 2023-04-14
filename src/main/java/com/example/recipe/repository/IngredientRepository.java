package com.example.recipe.repository;

import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
    Boolean existsByName(String name);

}
