package com.example.recipe.repository;


import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Instruction;
import com.example.recipe.domain.Recipe;
import com.example.recipe.dto.IngredientSearchDTO;
import com.example.recipe.dto.InstructionSearchDTO;
import com.example.recipe.dto.RecipeSearchDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeSpecificationImpl implements RecipeSpecification {

    private final EntityManager entityManager;

    public RecipeSpecificationImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Recipe> filterRecipes(RecipeSearchDTO recipeSearchDTO) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);

        Root<Recipe> recipeRoot = cq.from(Recipe.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Recipe, Ingredient> ingredient = recipeRoot.join("ingredients");
        Join<Recipe, Instruction> instruction = recipeRoot.join("instructions");

        if (StringUtils.isNotEmpty(recipeSearchDTO.getName())) {
            predicates.add(cb.like(recipeRoot.get("name"), recipeSearchDTO.getName()));
        }

        if (recipeSearchDTO.getType() != null) {
            if (recipeSearchDTO.isTypeEquals()) {
                predicates.add(cb.equal(recipeRoot.get("type"), recipeSearchDTO.getType()));
            } else {
                predicates.add(cb.notEqual(recipeRoot.get("type"), recipeSearchDTO.getType()));
            }
        }

        if (recipeSearchDTO.getPortion() != null) {
            predicates.add(cb.equal(recipeRoot.get("portion"), recipeSearchDTO.getPortion()));
        }

        if (recipeSearchDTO.getIngredients() != null && !recipeSearchDTO.getIngredients().isEmpty()) {
            for (IngredientSearchDTO searchDTO : recipeSearchDTO.getIngredients()) {
                String name = "%" + searchDTO.getName() + "%";
                if (searchDTO.isContains()) {
                    predicates.add(cb.like(ingredient.get("name"), name));
                } else {
                    predicates.add(cb.notLike(ingredient.get("name"), name));
                }
            }
        }

        if (recipeSearchDTO.getInstructions() != null && !recipeSearchDTO.getInstructions().isEmpty()) {
            for (InstructionSearchDTO searchDTO : recipeSearchDTO.getInstructions()) {
                String dtoInstruction = "%" + searchDTO.getInstruction() + "%";
                if (searchDTO.isContains()) {
                    predicates.add(cb.like(instruction.get("instruction"), dtoInstruction));
                } else {
                    predicates.add(cb.notLike(instruction.get("instruction"), dtoInstruction));
                }
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.select(recipeRoot);

        TypedQuery<Recipe> query = entityManager.createQuery(cq);
        return query.getResultList();

    }
}
