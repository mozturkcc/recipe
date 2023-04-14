package com.example.recipe.mapper;

import org.junit.jupiter.api.BeforeEach;

public class InstructionMapperTest {


    private InstructionMapper instructionMapper;

    @BeforeEach
    public void setUp() {
        instructionMapper = new InstructionMapperImpl();
    }
}
