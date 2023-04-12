package com.example.recipe.mapper;

import com.example.recipe.domain.Recipe;
import com.example.recipe.dto.RecipeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecipeMapper {
    RecipeMapper MAPPER = Mappers.getMapper(RecipeMapper.class);
    RecipeDTO map(Recipe value);
    Recipe map(RecipeDTO value);
}
