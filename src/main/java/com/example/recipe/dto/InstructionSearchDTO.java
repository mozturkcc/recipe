package com.example.recipe.dto;

import lombok.Data;

@Data
public class InstructionSearchDTO extends InstructionDTO{
    Long id;
    String instruction;
    boolean contains;
}
