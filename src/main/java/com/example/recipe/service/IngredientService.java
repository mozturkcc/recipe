package com.example.recipe.service;

import com.example.recipe.domain.Ingredient;
import com.example.recipe.dto.IngredientDTO;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public boolean exists(IngredientDTO ingredientDTO){
        boolean exists = false;
        Long id = ingredientDTO.getId();

        if(id != null && ingredientRepository.existsById(id)){
            exists = true;
        }
        return exists;
    }

    public IngredientDTO create(IngredientDTO ingredientDTO){

        Ingredient ingredient = IngredientMapper.MAPPER.map(ingredientDTO);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        IngredientDTO returnValue = IngredientMapper.MAPPER.map(savedIngredient);
        return returnValue;
    }

    public IngredientDTO update(IngredientDTO ingredientDTO){
        Ingredient ingredient = IngredientMapper.MAPPER.map(ingredientDTO);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        IngredientDTO returnValue = IngredientMapper.MAPPER.map(savedIngredient);

        return returnValue;
    }

    public List<IngredientDTO> getAll(){
        List<IngredientDTO> ingredientDTOS =
        ingredientRepository.findAll().stream().map(IngredientMapper.MAPPER::map).collect(Collectors.toList());
        return ingredientDTOS;
    }

    public Optional<IngredientDTO> getOne(Long id){
        Optional<IngredientDTO> ingredient = ingredientRepository.findById(id).map(IngredientMapper.MAPPER::map);
        return ingredient;
    }

    public void delete(Long id){
        ingredientRepository.deleteById(id);
    }
}
