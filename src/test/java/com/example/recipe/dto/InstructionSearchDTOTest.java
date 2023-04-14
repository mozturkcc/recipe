package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import com.example.recipe.domain.Instruction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InstructionSearchDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instruction.class);
        InstructionSearchDTO instruction1 = new InstructionSearchDTO();
        instruction1.setInstruction("a");
        InstructionSearchDTO instruction2 = new InstructionSearchDTO();
        instruction2.setInstruction(instruction1.getInstruction());
        assertThat(instruction1).isEqualTo(instruction2);
        instruction2.setInstruction("b");
        assertThat(instruction1).isNotEqualTo(instruction2);
        instruction1.setInstruction(null);
        assertThat(instruction1).isNotEqualTo(instruction2);
    }
}
