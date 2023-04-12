package com.example.recipe.service;

import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Recipe;
import com.example.recipe.domain.RecipeType;
import com.example.recipe.dto.*;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.mapper.RecipeMapper;
import com.example.recipe.repository.IngredientRepository;
import com.example.recipe.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public boolean exists(RecipeDTO recipeDTO){
        boolean exists = false;
        Long id = recipeDTO.getId();

        if(id != null && recipeRepository.existsById(id)){
            exists = true;
        }
        return exists;
    }

    public RecipeDTO create(RecipeDTO recipeDTO){

        Recipe recipe = RecipeMapper.MAPPER.map(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        RecipeDTO returnValue = RecipeMapper.MAPPER.map(savedRecipe);
        return returnValue;
    }

    public RecipeDTO update(RecipeDTO recipeDTO){

        Recipe recipe = RecipeMapper.MAPPER.map(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        RecipeDTO returnValue = RecipeMapper.MAPPER.map(savedRecipe);
        return returnValue;
    }

    public List<RecipeDTO> getAll(){
        List<RecipeDTO> result =
                recipeRepository.findAll().stream().map(RecipeMapper.MAPPER::map).collect(Collectors.toList());
        return result;
    }

    public List<RecipeDTO> getAllBySearchDTO(RecipeSearchDTO recipeSearchDTO){
        List<RecipeDTO> result =
                recipeRepository.findAll().stream().map(RecipeMapper.MAPPER::map).collect(Collectors.toList());
        if(recipeSearchDTO.getName() !=null && !recipeSearchDTO.getName().isEmpty()){
            result = result.stream().filter(r-> r.getName().contains(recipeSearchDTO.getName())).collect(Collectors.toList());
        }
        if(recipeSearchDTO.getType() != null){
            if(recipeSearchDTO.isTypeEquals()){
                result = result.stream().filter(r-> r.getType() == recipeSearchDTO.getType()).collect(Collectors.toList());
            } else {
                result = result.stream().filter(r-> r.getType() == recipeSearchDTO.getType()).collect(Collectors.toList());
            }
        }
        if(recipeSearchDTO.getIngredients() != null && !recipeSearchDTO.getIngredients().isEmpty()){

            List<String> notIncluededs = recipeSearchDTO.getIngredients().stream().filter(i -> !i.isContains() && i.getName() != null && !i.getName().isEmpty()).map(IngredientSearchDTO::getName).collect(Collectors.toList());
            if(notIncluededs !=null && !notIncluededs.isEmpty()) {
                result = result.stream().filter(r -> !r.getIngredients().stream().filter(ri -> notIncluededs.stream().anyMatch(ni -> !ri.getName().toLowerCase().contains(ni.toLowerCase()))).collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
            }
            List<String> incluededs = recipeSearchDTO.getIngredients().stream().filter(i -> i.isContains() && i.getName() != null && !i.getName().isEmpty()).map(IngredientSearchDTO::getName).collect(Collectors.toList());
            if(incluededs != null && !incluededs.isEmpty()) {
                result = result.stream().filter(r -> !r.getIngredients().stream().filter(ri -> incluededs.stream().anyMatch(ni -> ri.getName().toLowerCase().contains(ni.toLowerCase()))).collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
            }
        }
        if(recipeSearchDTO.getInstructions() != null && !recipeSearchDTO.getInstructions().isEmpty()){

            List<String> notIncluededs = recipeSearchDTO.getInstructions().stream().filter(i -> !i.isContains() && i.getInstruction() != null && !i.getInstruction().isEmpty()).map(InstructionSearchDTO::getInstruction).collect(Collectors.toList());
            if(notIncluededs !=null && !notIncluededs.isEmpty()) {
                result = result.stream().filter(r -> !r.getInstructions().stream().filter(ri -> notIncluededs.stream().anyMatch(ni -> !ri.getInstruction().toLowerCase().contains(ni.toLowerCase()))).collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
            }
            List<String> incluededs = recipeSearchDTO.getInstructions().stream().filter(i -> i.isContains() && i.getInstruction() != null && !i.getInstruction().isEmpty()).map(InstructionSearchDTO::getInstruction).collect(Collectors.toList());
            if(incluededs != null && !incluededs.isEmpty()) {
                result = result.stream().filter(r -> !r.getInstructions().stream().filter(ri -> incluededs.stream().anyMatch(ni -> ri.getInstruction().toLowerCase().contains(ni.toLowerCase()))).collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
            }

        }
        if(recipeSearchDTO.getPortion() != null ){
            result = result.stream().filter(r-> r.getPortion() == recipeSearchDTO.getPortion()).collect(Collectors.toList());
        }
        return result;
    }

    public Optional<RecipeDTO> getOne(Long id){
        Optional<RecipeDTO> result = recipeRepository.findById(id).map(RecipeMapper.MAPPER::map);
        return result;
    }

    public void delete(Long id){
        recipeRepository.deleteById(id);
    }
}
