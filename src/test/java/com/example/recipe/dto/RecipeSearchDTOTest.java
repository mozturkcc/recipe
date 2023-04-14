package com.example.recipe.dto;

import com.example.recipe.TestUtil;
import com.example.recipe.domain.Recipe;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeSearchDTOTest {
    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recipe.class);
        RecipeSearchDTO recipe1 = new RecipeSearchDTO();
        recipe1.setName("Test1");
        RecipeSearchDTO recipe2 = new RecipeSearchDTO();
        recipe2.setName(recipe1.getName());
        assertThat(recipe1).isEqualTo(recipe2);
        recipe2.setName("Test2");
        assertThat(recipe1).isNotEqualTo(recipe2);
        recipe1.setName(null);
        assertThat(recipe1).isNotEqualTo(recipe2);
    }
}
