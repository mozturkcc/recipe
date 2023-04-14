package com.example.recipe.mapper;

import com.example.recipe.domain.Recipe;
import com.example.recipe.dto.RecipeDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecipeMapper {
    RecipeMapper MAPPER = Mappers.getMapper(RecipeMapper.class);

    RecipeDTO map(Recipe value);

    Recipe map(RecipeDTO value);


    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Recipe target,RecipeDTO value);
}
