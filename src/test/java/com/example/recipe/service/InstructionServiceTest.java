package com.example.recipe.service;

import com.example.recipe.IntegrationTest;
import com.example.recipe.domain.Instruction;
import com.example.recipe.dto.InstructionDTO;
import com.example.recipe.mapper.InstructionMapper;
import com.example.recipe.repository.InstructionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class InstructionServiceTest {

    private static final String TEST_INSTRUCTION_NAME = "TEST_FLAME";
    private static final String TEST_INSTRUCTION_UPDATE_NAME = "TEST_OVEN";

    @Autowired
    private InstructionRepository instructionRepository;
    @Autowired
    private InstructionService instructionService;

    private Instruction instruction;
    private InstructionDTO instructionDTO;

    @BeforeEach
    public void init(){
        instruction = new Instruction();
        instruction.setInstruction(TEST_INSTRUCTION_NAME);
        instructionDTO = new InstructionDTO();
        instructionDTO.setInstruction(TEST_INSTRUCTION_NAME);
        instructionDTO = instructionService.create(instructionDTO);
    }

    /**
     * Since there is no logic in the seervice layer for now, I didn't mock the repository.
     * */
    @Test
    @Transactional
    void crudTest(){

        //test creation
        Optional<Instruction> created = instructionRepository.findById(instructionDTO.getId());
        assertThat(created).isPresent();

        //test update
        instructionDTO.setId(instructionDTO.getId());
        instructionDTO.setInstruction(TEST_INSTRUCTION_UPDATE_NAME);
        instructionDTO = instructionService.update(instructionDTO);
        created = instructionRepository.findById(instructionDTO.getId());
        assertThat(created).isPresent();
        InstructionDTO updatedDTO = InstructionMapper.MAPPER.map(created.get());
        assertThat(instructionDTO).isEqualTo(updatedDTO);

        //test deletion
        instructionService.delete(updatedDTO.getId());
        created = instructionRepository.findById(updatedDTO.getId());
        assertThat(created).isEmpty();

    }

    @Test
    @Transactional
    void existsFuncTest(){
        // test that created object exists
        Optional<Instruction> created = instructionRepository.findById(instructionDTO.getId());
        assertThat(created).isPresent();

        //test that existance proved object can be checked by service
        assertThat(instructionService.exists(instructionDTO)).isTrue();


        // test that if unique value column attribute value exists, service returns true;
        InstructionDTO testDTO = new InstructionDTO();
        testDTO.setId(2222L);
        testDTO.setInstruction(TEST_INSTRUCTION_NAME);
        assertThat(instructionService.exists(testDTO)).isTrue();

        // test that if primary key attribute value exists, service returns true;
        testDTO.setId(instructionDTO.getId());
        testDTO.setInstruction(TEST_INSTRUCTION_UPDATE_NAME);
        assertThat(instructionService.exists(testDTO)).isTrue();

        testDTO.setId(2222L);
        assertThat(instructionService.exists(testDTO)).isFalse();
    }


}
