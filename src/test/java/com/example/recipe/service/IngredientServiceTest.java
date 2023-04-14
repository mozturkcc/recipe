package com.example.recipe.service;

import com.example.recipe.IntegrationTest;
import com.example.recipe.domain.Ingredient;
import com.example.recipe.dto.IngredientDTO;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class IngredientServiceTest {

    private static final String TEST_INGREDIENT_NAME = "TEST_POTATOES";
    private static final String TEST_INGREDIENT_UPDATE_NAME = "TEST_TOMATOES";

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private IngredientService ingredientService;

    private Ingredient ingredient;
    private IngredientDTO ingredientDTO;

    @BeforeEach
    public void init(){
        ingredient = new Ingredient();
        ingredient.setName(TEST_INGREDIENT_NAME);
        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName(TEST_INGREDIENT_NAME);
        ingredientDTO = ingredientService.create(ingredientDTO);
    }

    /**
     * Since there is no logic in the seervice layer for now, I didn't mock the repository.
     * */
    @Test
    @Transactional
    void crudTest(){

        //test creation
        Optional<Ingredient> created = ingredientRepository.findById(ingredientDTO.getId());
        assertThat(created).isPresent();

        //test update
        ingredientDTO.setId(ingredientDTO.getId());
        ingredientDTO.setName(TEST_INGREDIENT_UPDATE_NAME);
        ingredientDTO = ingredientService.update(ingredientDTO);
        created = ingredientRepository.findById(ingredientDTO.getId());
        assertThat(created).isPresent();
        IngredientDTO updatedDTO = IngredientMapper.MAPPER.map(created.get());
        assertThat(ingredientDTO).isEqualTo(updatedDTO);

        //test deletion
        ingredientService.delete(updatedDTO.getId());
        created = ingredientRepository.findById(updatedDTO.getId());
        assertThat(created).isEmpty();

    }

    @Test
    @Transactional
    void existsFuncTest(){
        // test that created object exists
        Optional<Ingredient> created = ingredientRepository.findById(ingredientDTO.getId());
        assertThat(created).isPresent();

        //test that existance proved object can be checked by service
        assertThat(ingredientService.exists(ingredientDTO)).isTrue();


        // test that if unique value column attribute value exists, service returns true;
        IngredientDTO testDTO = new IngredientDTO();
        testDTO.setId(2222L);
        testDTO.setName(TEST_INGREDIENT_NAME);
        assertThat(ingredientService.exists(testDTO)).isTrue();

        // test that if primary key attribute value exists, service returns true;
        testDTO.setId(ingredientDTO.getId());
        testDTO.setName(TEST_INGREDIENT_UPDATE_NAME);
        assertThat(ingredientService.exists(testDTO)).isTrue();

        testDTO.setId(2222L);
        assertThat(ingredientService.exists(testDTO)).isFalse();
    }


}
