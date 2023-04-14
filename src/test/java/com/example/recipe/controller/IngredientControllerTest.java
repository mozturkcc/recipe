package com.example.recipe.controller;

import com.example.recipe.IntegrationTest;
import com.example.recipe.TestUtil;
import com.example.recipe.domain.Ingredient;
import com.example.recipe.dto.IngredientDTO;
import com.example.recipe.mapper.IngredientMapper;
import com.example.recipe.service.IngredientService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class IngredientControllerTest {
    private static final String DEFAULT_NAME = "Potatoes";
    private static final String UPDATED_NAME = "Tomatoes";


    private static final String ENTITY_API_URL = "/api/ingredient";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private Ingredient ingredient;

    @Autowired
    private MockMvc restMockMvc;
    @Autowired
    private IngredientService ingredientService;

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createEntity(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName(DEFAULT_NAME);
        return ingredient;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ingredient createUpdatedEntity(EntityManager em) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(UPDATED_NAME);
        return ingredient;
    }

    @BeforeEach
    public void initTest() {
        ingredient = createEntity();
    }

    @Test
    @Transactional
    void createIngredient() throws Exception {
        int databaseSizeBeforeCreate = ingredientService.getAll().size();
        // Create the Ingredient
        IngredientDTO ingredientDTO = IngredientMapper.MAPPER.map(ingredient);
        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
                )
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeCreate + 1);
        IngredientDTO testIngredientDTO = ingredientDTOList.get(ingredientDTOList.size() - 1);
        assertThat(testIngredientDTO.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createIngredientWithExistingId() throws Exception {

        IngredientDTO ingredientDTO = ingredientService.create(IngredientMapper.MAPPER.map(ingredient));

        // size before api call
        int databaseSizeBeforeCreate = ingredientService.getAll().size();


        // An entity with an existing ID cannot be created, so this API call must fail
        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ingredientService.getAll().size();
        // set the field null
        ingredient.setName(null);

        // Create the Ingredient, which fails.
        IngredientDTO ingredientDTO = IngredientMapper.MAPPER.map(ingredient);

        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
                )
                .andExpect(status().isBadRequest());

        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIngredients() throws Exception {
        // Initialize the database
        IngredientDTO ingredientDTO = ingredientService.create(IngredientMapper.MAPPER.map(ingredient));

        // Get all the ingredientList
        restMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ingredientDTO.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getIngredient() throws Exception {
        // Initialize the database
        IngredientDTO ingredientDTO =ingredientService.create(IngredientMapper.MAPPER.map(ingredient));

        // Get the Ingredient
        restMockMvc
                .perform(get(ENTITY_API_URL_ID, ingredientDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(ingredientDTO.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExisting() throws Exception {
        // Get the ingredient
        restMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExisting() throws Exception {
        // Initialize the database
        IngredientDTO ingredientDTO = ingredientService.create(IngredientMapper.MAPPER.map(ingredient));

        int databaseSizeBeforeUpdate = ingredientService.getAll().size();

        // Update the ingredient
        IngredientDTO updatedIngredientDTO = ingredientService.getOne(ingredientDTO.getId()).get();

        updatedIngredientDTO.setName(UPDATED_NAME);

        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedIngredientDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedIngredientDTO))
                )
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeUpdate);
        IngredientDTO testIngredientDTO = ingredientDTOList.get(ingredientDTOList.size() - 1);
        assertThat(testIngredientDTO.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExisting() throws Exception {
        int databaseSizeBeforeUpdate = ingredientService.getAll().size();
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = IngredientMapper.MAPPER.map(ingredient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, ingredientDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatch() throws Exception {
        int databaseSizeBeforeUpdate = ingredientService.getAll().size();
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = IngredientMapper.MAPPER.map(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(ingredientDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParam() throws Exception {
        int databaseSizeBeforeUpdate = ingredientService.getAll().size();
        ingredient.setId(count.incrementAndGet());

        // Create the Ingredient
        IngredientDTO ingredientDTO = IngredientMapper.MAPPER.map(ingredient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ingredientDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Ingredient in the database
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeUpdate);
    }
    @Test
    @Transactional
    void deleteEntity() throws Exception {
        // Initialize the database
        IngredientDTO ingredientDTO = ingredientService.create(IngredientMapper.MAPPER.map(ingredient));

        int databaseSizeBeforeDelete = ingredientService.getAll().size();

        // Delete the ingredient
        restMockMvc
                .perform(delete(ENTITY_API_URL_ID, ingredientDTO.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the database contains one less item
        List<IngredientDTO> ingredientDTOList = ingredientService.getAll();
        assertThat(ingredientDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
