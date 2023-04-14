package com.example.recipe.controller;

import com.example.recipe.IntegrationTest;
import com.example.recipe.TestUtil;
import com.example.recipe.domain.Instruction;
import com.example.recipe.dto.InstructionDTO;
import com.example.recipe.mapper.InstructionMapper;
import com.example.recipe.service.InstructionService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class InstructionControllerTest {
    private static final String DEFAULT_INSTRUCTION = "oven 15 min. 200 Celsius";
    private static final String UPDATED_INSTRUCTION = "fry the patatoes";


    private static final String ENTITY_API_URL = "/api/instruction";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private Instruction instruction;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private InstructionService instructionService;

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instruction createEntity(){
        Instruction instruction = new Instruction();
        instruction.setInstruction(DEFAULT_INSTRUCTION);
        return instruction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instruction createUpdatedEntity() {
        Instruction instruction = new Instruction();
        instruction.setInstruction(UPDATED_INSTRUCTION);
        return instruction;
    }

    @BeforeEach
    public void initTest() {
        instruction = createEntity();
    }

    @Test
    @Transactional
    void create() throws Exception {
        int databaseSizeBeforeCreate = instructionService.getAll().size();
        // Create the Instruction
        InstructionDTO instructionDTO = InstructionMapper.MAPPER.map(instruction);
        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instructionDTO))
                )
                .andExpect(status().isOk());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeCreate + 1);
        InstructionDTO testinstructionDTO = instructionDTOList.get(instructionDTOList.size() - 1);
        assertThat(testinstructionDTO.getInstruction()).isEqualTo(DEFAULT_INSTRUCTION);
    }

    @Test
    @Transactional
    void createWithExistingId() throws Exception {

        InstructionDTO instructionDTO = instructionService.create(InstructionMapper.MAPPER.map(instruction));

        // size before api call
        int databaseSizeBeforeCreate = instructionService.getAll().size();


        // An entity with an existing ID cannot be created, so this API call must fail
        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instructionDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsRequired() throws Exception {
        int databaseSizeBeforeTest = instructionService.getAll().size();
        // set the field null
        instruction.setInstruction(null);

        // Create the Instruction, which fails.
        InstructionDTO instructionDTO = InstructionMapper.MAPPER.map(instruction);

        restMockMvc
                .perform(
                        post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instructionDTO))
                )
                .andExpect(status().isBadRequest());

        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAll() throws Exception {
        // Initialize the database
        InstructionDTO instructionDTO = instructionService.create(InstructionMapper.MAPPER.map(instruction));

        // Get all the InstructionList
        restMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(instructionDTO.getId().intValue())))
                .andExpect(jsonPath("$.[*].instruction").value(hasItem(DEFAULT_INSTRUCTION)));
    }

    @Test
    @Transactional
    void getInstruction() throws Exception {
        // Initialize the database
        InstructionDTO instructionDTO =instructionService.create(InstructionMapper.MAPPER.map(instruction));

        // Get the Instruction
        restMockMvc
                .perform(get(ENTITY_API_URL_ID, instructionDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(instructionDTO.getId().intValue()))
                .andExpect(jsonPath("$.instruction").value(DEFAULT_INSTRUCTION));
    }

    @Test
    @Transactional
    void getNonExisting() throws Exception {
        // Get the Instruction
        restMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExisting() throws Exception {
        // Initialize the database
        InstructionDTO instructionDTO = instructionService.create(InstructionMapper.MAPPER.map(instruction));

        int databaseSizeBeforeUpdate = instructionService.getAll().size();

        // Update the Instruction
        InstructionDTO updatedInstructionDTO = instructionService.getOne(instructionDTO.getId()).get();

        updatedInstructionDTO.setInstruction(UPDATED_INSTRUCTION);

        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, updatedInstructionDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(updatedInstructionDTO))
                )
                .andExpect(status().isOk());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeUpdate);
        InstructionDTO testInstructionDTO = instructionDTOList.get(instructionDTOList.size() - 1);
        assertThat(testInstructionDTO.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
    }

    @Test
    @Transactional
    void putNonExisting() throws Exception {
        int databaseSizeBeforeUpdate = instructionService.getAll().size();
        instruction.setId(count.incrementAndGet());

        // Create the Instruction
        InstructionDTO instructionDTO = InstructionMapper.MAPPER.map(instruction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, instructionDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(instructionDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatch() throws Exception {
        int databaseSizeBeforeUpdate = instructionService.getAll().size();
        instruction.setId(count.incrementAndGet());

        // Create the Instruction
        InstructionDTO instructionDTO = InstructionMapper.MAPPER.map(instruction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(instructionDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParam() throws Exception {
        int databaseSizeBeforeUpdate = instructionService.getAll().size();
        instruction.setId(count.incrementAndGet());

        // Create the Instruction
        InstructionDTO instructionDTO = InstructionMapper.MAPPER.map(instruction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instructionDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Instruction in the database
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeUpdate);
    }
    @Test
    @Transactional
    void deleteEntity() throws Exception {
        // Initialize the database
        InstructionDTO instructionDTO = instructionService.create(InstructionMapper.MAPPER.map(instruction));

        int databaseSizeBeforeDelete = instructionService.getAll().size();

        // Delete the Instruction
        restMockMvc
                .perform(delete(ENTITY_API_URL_ID, instructionDTO.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the database contains one less item
        List<InstructionDTO> instructionDTOList = instructionService.getAll();
        assertThat(instructionDTOList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
