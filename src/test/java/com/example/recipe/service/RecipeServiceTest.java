package com.example.recipe.service;

import com.example.recipe.IntegrationTest;
import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Instruction;
import com.example.recipe.domain.Recipe;
import com.example.recipe.domain.RecipeType;
import com.example.recipe.dto.*;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.mapper.InstructionMapper;
import com.example.recipe.mapper.RecipeMapper;
import com.example.recipe.repository.IngredientRepository;
import com.example.recipe.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class RecipeServiceTest {

    private static final String TEST_INGREDIENT_NAME = "TEST_RECIPE_NEW";
    private static final String TEST_INGREDIENT_UPDATE_NAME = "TEST_RECIPE_UPDATED";

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private InstructionService instructionService;

    private Recipe recipe;
    private RecipeDTO recipeDTO;

    private IngredientDTO createIngredientDTO(String name){
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName(name);
        return ingredientService.create(ingredientDTO);
    }

    private Ingredient createIngredient(String name){
        return IngredientMapper.MAPPER.map(createIngredientDTO(name));
    }


    private InstructionDTO createInstructionDTO(String instruction){
        InstructionDTO instructionDTO = new InstructionDTO();
        instructionDTO.setInstruction(instruction);
        return instructionService.create(instructionDTO);
    }

    private Instruction createInstruction(String instruction){
        return InstructionMapper.MAPPER.map(createInstructionDTO(instruction));
    }
    @BeforeEach
    public void init(){
        recipe = new Recipe();
        recipe.setName(TEST_INGREDIENT_NAME);

        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(createIngredient("potatoes"));
        ingredientList.add(createIngredient("tomatoes"));
        ingredientList.add(createIngredient("pepper"));
        ingredientList.add(createIngredient("salt"));
        recipe.setIngredients(ingredientList);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(createInstruction("fry potatoes"));
        instructionList.add(createInstruction("cut tomatoes as cube"));
        instructionList.add(createInstruction("oven the meal 45 min 200 Celsius"));

        recipe.setInstructions(instructionList);

        recipe.setPortion(3);

        recipe.setType(RecipeType.VEGAN);

        recipeDTO = RecipeMapper.MAPPER.map(recipe);
        recipeDTO = recipeService.create(recipeDTO);
    }

    /**
     * Since there is no logic in the seervice layer for now, I didn't mock the repository.
     * */
    @Test
    @Transactional
    void crudTest(){

        //test creation
        Optional<Recipe> created = recipeRepository.findById(recipeDTO.getId());
        assertThat(created).isPresent();
        RecipeDTO actualRecipeDTO = RecipeMapper.MAPPER.map(created.get());
        assertThat(actualRecipeDTO).isEqualTo(recipeDTO);

        //test update

        recipeDTO.setName(TEST_INGREDIENT_UPDATE_NAME);
        recipeDTO = recipeService.update(recipeDTO);
        created = recipeRepository.findById(recipeDTO.getId());
        assertThat(created).isPresent();
        actualRecipeDTO = RecipeMapper.MAPPER.map(created.get());
        assertThat(recipeDTO).isEqualTo(actualRecipeDTO);

        //testPartial

        Recipe expectedRecipe = RecipeMapper.MAPPER.map(recipeDTO);
        RecipeDTO expectedRecipeDTO =RecipeMapper.MAPPER.map(expectedRecipe);
        List<InstructionDTO> recipeInstructions = recipeDTO.getInstructions();
        recipeInstructions.remove(0);

        expectedRecipeDTO.setInstructions(recipeInstructions);
        recipeDTO.setInstructions(recipeInstructions);
        recipeDTO.setIngredients(null);

        recipeDTO = recipeService.partialUpdate(recipeDTO);

        assertThat(recipeDTO).isEqualTo(expectedRecipeDTO);




        //test deletion
        recipeService.delete(recipeDTO.getId());
        created = recipeRepository.findById(recipeDTO.getId());
        assertThat(created).isEmpty();

    }

    @Test
    @Transactional
    void existsFuncTest(){
        // test that created object exists
        Optional<Recipe> created = recipeRepository.findById(recipeDTO.getId());
        assertThat(created).isPresent();

        //test that existance proved object can be checked by service
        assertThat(recipeService.exists(recipeDTO)).isTrue();


        // test that if primary key attribute value not exists, service returns false;
        IngredientDTO testDTO = new IngredientDTO();
        testDTO.setId(2222L);
        assertThat(ingredientService.exists(testDTO)).isFalse();
    }

    @Test
    @Transactional
    void getAllBySearchDTO(){

        RecipeSearchDTO recipeSearchDTO = new RecipeSearchDTO();
        List<InstructionSearchDTO> instructionSearchDTOList = new ArrayList<>();
        instructionSearchDTOList.add(createInstructionSearchDTO("oven",true));
        List<IngredientSearchDTO> ingredientSearchDTOS =new ArrayList<>();
        ingredientSearchDTOS.add(createIngredientSearchDTO("potatoes",true));
        ingredientSearchDTOS.add(createIngredientSearchDTO("meat",false));
        recipeSearchDTO.setInstructions(instructionSearchDTOList);
        recipeSearchDTO.setIngredients(ingredientSearchDTOS);

        List<RecipeDTO> result = recipeService.getAllBySearchDTO(recipeSearchDTO);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(recipeDTO);

        ingredientSearchDTOS.add(createIngredientSearchDTO("tomato",false));
        recipeSearchDTO.setIngredients(ingredientSearchDTOS);

        result = recipeService.getAllBySearchDTO(recipeSearchDTO);
        assertThat(result).hasSize(0);
    }

    IngredientSearchDTO createIngredientSearchDTO(String name, Boolean contains){
        IngredientSearchDTO ingredientSearchDTO = new IngredientSearchDTO();
        ingredientSearchDTO.setContains(contains);
        ingredientSearchDTO.setName(name);
        return ingredientSearchDTO;
    }

    InstructionSearchDTO createInstructionSearchDTO(String instruction, Boolean contains){
        InstructionSearchDTO instructionSearchDTO = new InstructionSearchDTO();
        instructionSearchDTO.setContains(contains);
        instructionSearchDTO.setInstruction(instruction);
        return instructionSearchDTO;
    }
}
