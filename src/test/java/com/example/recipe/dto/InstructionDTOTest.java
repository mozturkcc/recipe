package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import com.example.recipe.domain.Instruction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InstructionDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instruction.class);
        InstructionDTO instruction1 = new InstructionDTO();
        instruction1.setId(1L);
        InstructionDTO instruction2 = new InstructionDTO();
        instruction2.setId(instruction1.getId());
        assertThat(instruction1).isEqualTo(instruction2);
        instruction2.setId(2L);
        assertThat(instruction1).isNotEqualTo(instruction2);
        instruction1.setId(null);
        assertThat(instruction1).isNotEqualTo(instruction2);
    }
}
