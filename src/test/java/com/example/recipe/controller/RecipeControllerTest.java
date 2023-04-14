package com.example.recipe.controller;

import com.example.recipe.IntegrationTest;
import com.example.recipe.TestUtil;
import com.example.recipe.domain.Ingredient;
import com.example.recipe.domain.Instruction;
import com.example.recipe.domain.Recipe;
import com.example.recipe.domain.RecipeType;
import com.example.recipe.dto.*;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.mapper.InstructionMapper;
import com.example.recipe.mapper.RecipeMapper;
import com.example.recipe.service.IngredientService;
import com.example.recipe.service.InstructionService;
import com.example.recipe.service.RecipeService;
import com.example.recipe.service.RecipeServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class RecipeControllerTest {
    private static final String DEFAULT_NAME = "Omelet";
    private static final String UPDATED_NAME = "fry the patatoes";


    private static final String ENTITY_API_URL = "/api/recipe";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_FILTER = ENTITY_API_URL + "/filter";

    private RecipeDTO recipeDTO;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private InstructionService instructionService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private MockMvc restMockMvc;


    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

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

    public RecipeDTO createEntity(){
        Recipe recipe = new Recipe();
        recipe.setName(DEFAULT_NAME);

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
        return RecipeMapper.MAPPER.map( recipe);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createUpdatedEntity() {
        Recipe recipe = new Recipe();
        recipe.setName(UPDATED_NAME);
        return recipe;
    }

    @BeforeEach
    public void initTest() {
        recipeDTO = createEntity();
    }

    @Test
    @Transactional
    void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeService.getAll().size();

        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeCreate + 1);
        RecipeDTO testRecipeDTO = recipeDTOList.get(recipeDTOList.size() - 1);
        assertThat(testRecipeDTO.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createIngredientWithExistingId() throws Exception {

        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        // size before api call
        int databaseSizeBeforeCreate = recipeService.getAll().size();


        // An entity with an existing ID cannot be created, so this API call must fail
        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(createdRecipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void getAll() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        List<RecipeDTO> recipeDTOListService = recipeService.getAll();

        // Get all the ingredientList
        MvcResult result = restMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        List<RecipeDTO> recipeDTOList = new ArrayList(Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),RecipeDTO[].class)));
        assertThat(recipeDTOList).isEqualTo(recipeDTOListService);
    }

    @Test
    @Transactional
    void getAllBySearchDTO() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        RecipeSearchDTO recipeSearchDTO = new RecipeSearchDTO();
        List<InstructionSearchDTO> instructionSearchDTOList = new ArrayList<>();
        instructionSearchDTOList.add(createInstructionSearchDTO("oven",true));
        List<IngredientSearchDTO> ingredientSearchDTOS =new ArrayList<>();
        ingredientSearchDTOS.add(createIngredientSearchDTO("potatoes",true));
        ingredientSearchDTOS.add(createIngredientSearchDTO("meat",false));
        recipeSearchDTO.setInstructions(instructionSearchDTOList);
        recipeSearchDTO.setIngredients(ingredientSearchDTOS);

        // Get all the ingredientList
        MvcResult result = restMockMvc
                .perform(
                        get(ENTITY_API_URL_FILTER).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeSearchDTO))
                ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        List<RecipeDTO> recipeDTOList = new ArrayList(Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),RecipeDTO[].class)));
        assertThat(recipeDTOList).hasSize(1);
        assertThat(createdRecipeDTO).isEqualTo(recipeDTOList.get(0));
    }


    @Test
    @Transactional
    void getAllBySearchDTONotExists() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        RecipeSearchDTO recipeSearchDTO = new RecipeSearchDTO();
        List<InstructionSearchDTO> instructionSearchDTOList = new ArrayList<>();
        instructionSearchDTOList.add(createInstructionSearchDTO("oven",false));
        List<IngredientSearchDTO> ingredientSearchDTOS =new ArrayList<>();
        ingredientSearchDTOS.add(createIngredientSearchDTO("potatoes",false));
        recipeSearchDTO.setInstructions(instructionSearchDTOList);
        recipeSearchDTO.setIngredients(ingredientSearchDTOS);

        // Get all the ingredientList
        MvcResult result = restMockMvc
                .perform(
                        get(ENTITY_API_URL_FILTER).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeSearchDTO))
                ).andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        List<RecipeDTO> recipeDTOList = new ArrayList(Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),RecipeDTO[].class)));
        assertThat(recipeDTOList).hasSize(0);
    }
    @Test
    @Transactional
    void getRecipe() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipe = recipeService.create(recipeDTO);

        // Get the Ingredient
        MvcResult result = restMockMvc
                .perform(get(ENTITY_API_URL_ID, createdRecipe.getId()))
                .andExpect(status().isOk()).andReturn();


        ObjectMapper objectMapper = new ObjectMapper();
        RecipeDTO apiResult = objectMapper.readValue(result.getResponse().getContentAsString(),RecipeDTO.class);
        assertThat(apiResult).isEqualTo(createdRecipe);
    }

    @Test
    @Transactional
    void getNonExisting() throws Exception {
        // Get the ingredient
        restMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void patchExisting() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        int databaseSizeBeforeUpdate = recipeService.getAll().size();

        // Update the ingredient
        RecipeDTO updatedRecipeDTO = recipeService.getOne(createdRecipeDTO.getId()).get();
        Recipe expectedRecipe = RecipeMapper.MAPPER.map(updatedRecipeDTO);
        RecipeDTO expectedRecipeDTO =RecipeMapper.MAPPER.map(expectedRecipe);
        List<InstructionDTO> recipeInstructions = updatedRecipeDTO.getInstructions();
        recipeInstructions.remove(0);
        expectedRecipeDTO.setInstructions(recipeInstructions);
        updatedRecipeDTO.setInstructions(recipeInstructions);
        updatedRecipeDTO.setIngredients(null);


        restMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, updatedRecipeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedRecipeDTO))
                )
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeUpdate);
        RecipeDTO testRecipeDTO = recipeDTOList.get(recipeDTOList.size() - 1);
        assertThat(testRecipeDTO).isEqualTo(expectedRecipeDTO);
    }

    @Test
    @Transactional
    void putExisting() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        int databaseSizeBeforeUpdate = recipeService.getAll().size();

        // Update the ingredient
        RecipeDTO updatedRecipeDTO = recipeService.getOne(createdRecipeDTO.getId()).get();

        updatedRecipeDTO.setName(UPDATED_NAME);

        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedRecipeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedRecipeDTO))
                )
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeUpdate);
        RecipeDTO testRecipeDTO = recipeDTOList.get(recipeDTOList.size() - 1);
        assertThat(testRecipeDTO.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExisting() throws Exception {
        int databaseSizeBeforeUpdate = recipeService.getAll().size();
        recipeDTO.setId(count.incrementAndGet());


        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, recipeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList  = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatch() throws Exception {
        int databaseSizeBeforeUpdate = recipeService.getAll().size();
        recipeDTO.setId(count.incrementAndGet());


        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList  = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParam() throws Exception {
        int databaseSizeBeforeUpdate = recipeService.getAll().size();
        recipeDTO.setId(count.incrementAndGet());


        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeUpdate);
    }
    @Test
    @Transactional
    void deleteEntity() throws Exception {
        // Initialize the database
        RecipeDTO createdRecipeDTO = recipeService.create(recipeDTO);

        int databaseSizeBeforeDelete = recipeService.getAll().size();

        // Delete the ingredient
        restMockMvc
                .perform(delete(ENTITY_API_URL_ID, createdRecipeDTO.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the database contains one less item
        List<RecipeDTO> recipeDTOList = recipeService.getAll();
        assertThat(recipeDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
