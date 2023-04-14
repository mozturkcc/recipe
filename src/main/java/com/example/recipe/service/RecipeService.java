package com.example.recipe.service;

import com.example.recipe.domain.Recipe;
import com.example.recipe.dto.*;
import com.example.recipe.mapper.RecipeMapper;
import com.example.recipe.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    RecipeRepository recipeRepository;


    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public boolean exists(RecipeDTO recipeDTO) {
        boolean exists = false;
        Long id = recipeDTO.getId();

        if (id != null && recipeRepository.existsById(id)) {
            exists = true;
        }
        return exists;
    }

    public RecipeDTO create(RecipeDTO recipeDTO) {

        Recipe recipe = RecipeMapper.MAPPER.map(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        RecipeDTO returnValue = RecipeMapper.MAPPER.map(savedRecipe);
        return returnValue;
    }

    public RecipeDTO update(RecipeDTO recipeDTO) {

        Recipe recipe = RecipeMapper.MAPPER.map(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipe);
        RecipeDTO returnValue = RecipeMapper.MAPPER.map(savedRecipe);
        return returnValue;
    }

    public RecipeDTO partialUpdate(RecipeDTO recipeDTO){
        Optional<Recipe> recipe = recipeRepository.findById(recipeDTO.getId())
                .map(existingRecipe -> {
                            RecipeMapper.MAPPER.partialUpdate(existingRecipe, recipeDTO);
                            return existingRecipe;
                        }
                ).map(recipeRepository::save);
            RecipeDTO result = RecipeMapper.MAPPER.map(recipe.get());
        return result;
    }

    public List<RecipeDTO> getAll() {
        List<RecipeDTO> result =
                recipeRepository.findAll().stream().map(RecipeMapper.MAPPER::map).collect(Collectors.toList());
        return result;
    }

    public List<RecipeDTO> getAllBySearchDTO(RecipeSearchDTO recipeSearchDTO) {
        List<RecipeDTO> result =
                recipeRepository.filterRecipes(recipeSearchDTO).stream().map(RecipeMapper.MAPPER::map).collect(Collectors.toList());
        return result;

    }

    public Optional<RecipeDTO> getOne(Long id) {
        Optional<RecipeDTO> result = recipeRepository.findById(id).map(RecipeMapper.MAPPER::map);
        return result;
    }

    public void delete(Long id) {
        recipeRepository.deleteById(id);
    }
}
