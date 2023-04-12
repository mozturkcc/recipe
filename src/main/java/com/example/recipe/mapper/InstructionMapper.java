package com.example.recipe.mapper;

import com.example.recipe.domain.Instruction;
import com.example.recipe.dto.InstructionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InstructionMapper {
    InstructionMapper MAPPER = Mappers.getMapper(InstructionMapper.class);
    InstructionDTO map(Instruction value);
    Instruction map(InstructionDTO value);
}
