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
        List<Predicate> includedIngredients = new ArrayList<>();
        List<Predicate> notIncludedIngredients = new ArrayList<>();


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


        Predicate total = createIngPredicate(recipeSearchDTO, cb, cq, recipeRoot, predicates);
        total = cb.and(cb.and(predicates.toArray(new Predicate[0])), total);

        Predicate ins = createInstructionPredicate(recipeSearchDTO, cb, cq, recipeRoot);
        total = cb.and(total, ins);

        cq.select(recipeRoot);
        cq.where(total);
        TypedQuery<Recipe> query = entityManager.createQuery(cq);
        return query.getResultList();

    }

    private Predicate createInstructionPredicate(RecipeSearchDTO recipeSearchDTO, CriteriaBuilder cb, CriteriaQuery<Recipe> cq, Root<Recipe> recipeRoot) {
        List<InstructionSearchDTO> includedIns = new ArrayList<>();
        List<InstructionSearchDTO> notIncludedIns = new ArrayList<>();

        if (recipeSearchDTO.getInstructions() != null && !recipeSearchDTO.getInstructions().isEmpty()) {
            for (InstructionSearchDTO searchDTO : recipeSearchDTO.getInstructions()) {
                if (searchDTO.isContains()) {
                    includedIns.add(searchDTO);
                } else {
                    notIncludedIns.add(searchDTO);
                }
            }
        }

        Subquery<Recipe> notIncludedInstructionsSubQuery = notIncludedInstructions(notIncludedIns, cb, cq);

        Predicate total = cb.not(cb.in(recipeRoot.get("id")).value(notIncludedInstructionsSubQuery));
        for (InstructionSearchDTO includedIn : includedIns) {
            Subquery<Recipe> includedInstructionSubQuery = includedInstruction(includedIn, cb, cq);
            total = cb.and(total, cb.in(recipeRoot.get("id")).value(includedInstructionSubQuery));
        }
        return total;
    }

    private Predicate createIngPredicate(RecipeSearchDTO recipeSearchDTO, CriteriaBuilder cb, CriteriaQuery<Recipe> cq, Root<Recipe> recipeRoot, List<Predicate> predicates) {
        // find recipes which contains any of not included ingredients

        List<IngredientSearchDTO> includedIng = new ArrayList<>();
        List<IngredientSearchDTO> notIncludedIng = new ArrayList<>();

        if (recipeSearchDTO.getIngredients() != null && !recipeSearchDTO.getIngredients().isEmpty()) {
            for (IngredientSearchDTO searchDTO : recipeSearchDTO.getIngredients()) {
                if (searchDTO.isContains()) {
                    includedIng.add(searchDTO);
                } else {
                    notIncludedIng.add(searchDTO);
                }
            }
        }

        Subquery<Recipe> subquery = notIncludedIngredients(notIncludedIng, cb, cq);
        Predicate total = cb.not(cb.in(recipeRoot.get("id")).value(subquery));
        for (IngredientSearchDTO searchDTO : includedIng) {
            Subquery<Recipe> sub = includedIng(searchDTO, cb, cq);
            total = cb.and(total, cb.in(recipeRoot.get("id")).value(sub));
        }
        return total;
    }

    //create query which returns recipes which contains any of not included ingredients
    public Subquery<Recipe> notIncludedIngredients(List<IngredientSearchDTO> notIngredientSearchDTOS, CriteriaBuilder cb, CriteriaQuery<Recipe> cq) {

        Subquery<Recipe> subquery = cq.subquery(Recipe.class);
        Root<Recipe> subRoot = subquery.from(Recipe.class);
        Join<Recipe, Ingredient> unWanted = subRoot.join("ingredients");
        List<Predicate> notIncludedIngredients = new ArrayList<>();
        for (IngredientSearchDTO notIngredientSearchDTO : notIngredientSearchDTOS) {
            notIncludedIngredients.add(cb.like(unWanted.get("name"), "%" + notIngredientSearchDTO.getName() + "%"));
        }

        subquery.select(subRoot.get("id")).where(cb.or(notIncludedIngredients.toArray(new Predicate[0])));
        return subquery;
    }

    //create query which returns recipes which contains included ingredient
    public Subquery<Recipe> includedIng(IngredientSearchDTO searchDTO, CriteriaBuilder cb, CriteriaQuery<Recipe> cq) {
        Subquery<Recipe> subquery = cq.subquery(Recipe.class);
        Root<Recipe> subRoot = subquery.from(Recipe.class);
        Join<Recipe, Ingredient> wanted = subRoot.join("ingredients");
        subquery.select(subRoot.get("id")).where(cb.like(wanted.get("name"), "%" + searchDTO.getName() + "%"));
        return subquery;
    }

    //create query which returns recipes which contains any of not included instructions
    public Subquery<Recipe> notIncludedInstructions(List<InstructionSearchDTO> notInstructionSearchDTOS, CriteriaBuilder cb, CriteriaQuery<Recipe> cq) {

        Subquery<Recipe> subquery = cq.subquery(Recipe.class);
        Root<Recipe> subRoot = subquery.from(Recipe.class);
        Join<Recipe, Instruction> unWanted = subRoot.join("instructions");
        List<Predicate> notIncludedInstructions = new ArrayList<>();
        for (InstructionSearchDTO notInstructionSearchDTO : notInstructionSearchDTOS) {
            notIncludedInstructions.add(cb.like(unWanted.get("instruction"), "%" + notInstructionSearchDTO.getInstruction() + "%"));
        }

        subquery.select(subRoot.get("id")).where(cb.or(notIncludedInstructions.toArray(new Predicate[0])));
        return subquery;
    }

    //create query which returns recipes which contains included instruction
    public Subquery<Recipe> includedInstruction(InstructionSearchDTO searchDTO, CriteriaBuilder cb, CriteriaQuery<Recipe> cq) {
        Subquery<Recipe> subquery = cq.subquery(Recipe.class);
        Root<Recipe> subRoot = subquery.from(Recipe.class);
        Join<Recipe, Instruction> wanted = subRoot.join("instructions");
        subquery.select(subRoot.get("id")).where(cb.like(wanted.get("instruction"), "%" + searchDTO.getInstruction() + "%"));
        return subquery;
    }


}
