package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IngredientSearchDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IngredientSearchDTO.class);
        IngredientSearchDTO ingredient1 = new IngredientSearchDTO();
        ingredient1.setId(1L);
        IngredientSearchDTO ingredient2 = new IngredientSearchDTO();
        ingredient2.setId(ingredient1.getId());
        assertThat(ingredient1).isEqualTo(ingredient2);
        ingredient2.setId(2L);
        assertThat(ingredient1).isNotEqualTo(ingredient2);
        ingredient1.setId(null);
        assertThat(ingredient1).isNotEqualTo(ingredient2);
    }
}
