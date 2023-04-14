package com.example.recipe.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InstructionDTO {
    Long id;
    @NotNull
    String instruction;
}
