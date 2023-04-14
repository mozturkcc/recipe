package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IngredientSearchDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        IngredientSearchDTO ingredient1 = new IngredientSearchDTO();
        ingredient1.setName("a");
        IngredientSearchDTO ingredient2 = new IngredientSearchDTO();
        ingredient2.setName(ingredient1.getName());
        assertThat(ingredient1).isEqualTo(ingredient2);
        ingredient2.setName("b");
        assertThat(ingredient1).isNotEqualTo(ingredient2);
        ingredient1.setName(null);
        assertThat(ingredient1).isNotEqualTo(ingredient2);
    }
}
