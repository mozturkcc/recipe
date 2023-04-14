package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import com.example.recipe.domain.Recipe;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recipe.class);
        RecipeDTO recipe1 = new RecipeDTO();
        recipe1.setId(1L);
        RecipeDTO recipe2 = new RecipeDTO();
        recipe2.setId(recipe1.getId());
        assertThat(recipe1).isEqualTo(recipe2);
        recipe2.setId(2L);
        assertThat(recipe1).isNotEqualTo(recipe2);
        recipe1.setId(null);
        assertThat(recipe1).isNotEqualTo(recipe2);
    }
}
