package com.example.recipe.mapper;

import com.example.recipe.domain.Ingredient;
import com.example.recipe.dto.IngredientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IngredientMapper {
    IngredientMapper MAPPER = Mappers.getMapper(IngredientMapper.class);
    IngredientDTO map(Ingredient value);
    Ingredient map(IngredientDTO value);
}
