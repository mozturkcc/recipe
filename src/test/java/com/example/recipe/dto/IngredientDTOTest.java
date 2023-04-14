package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import com.example.recipe.domain.Ingredient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IngredientDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        IngredientDTO ingredient1 = new IngredientDTO();
        ingredient1.setId(1L);
        IngredientDTO ingredient2 = new IngredientDTO();
        ingredient2.setId(ingredient1.getId());
        assertThat(ingredient1).isEqualTo(ingredient2);
        ingredient2.setId(2L);
        assertThat(ingredient1).isNotEqualTo(ingredient2);
        ingredient1.setId(null);
        assertThat(ingredient1).isNotEqualTo(ingredient2);
    }
}
