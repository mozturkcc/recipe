package com.example.recipe.controller;

import com.example.recipe.controller.utilities.HeaderUtility;
import com.example.recipe.controller.utilities.ResponseUtility;
import com.example.recipe.dto.InstructionDTO;
import com.example.recipe.service.InstructionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InstructionController {

    InstructionService instructionService;

    public InstructionController(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    /**
     * {@code POST  /instruction} : Create a new instruction.
     *
     * @param instructionDTO the ingredinetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instructionDTO, or with status {@code 400 (Bad Request)} if the instruction has already an ID.
     */
    @PostMapping("/instruction")
    public ResponseEntity<InstructionDTO> createInstruction(@Valid @RequestBody InstructionDTO instructionDTO){
        if(!instructionService.exists(instructionDTO)) {
            InstructionDTO result = instructionService.create(instructionDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityCreationAlert("Instruction", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("Instruction", "idexists", "A new Instruction cannot be created with an existing id"))
                    .body(null);
        }
    }

    /**
     * {@code PUT  /instruction/:id} : Updates an existing instruction.
     *
     * @param id the id of the instruction to update.
     * @param instructionDTO the instruction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instruction,
     * or with status {@code 400 (Bad Request)} if the instruction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instruction couldn't be updated.
     */
    @PutMapping("/instruction/{id}")
    public ResponseEntity<InstructionDTO> updateInstruction(@PathVariable(value = "id", required = false) final Long id,
                                                  @Valid @RequestBody InstructionDTO instructionDTO){
        if(id != null && id.equals(instructionDTO.getId()) && instructionService.exists(instructionDTO)) {
            InstructionDTO result = instructionService.update(instructionDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityUpdateAlert("Instruction", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("Instruction", "idNOTexists", "The entity to be updated does not exists"))
                    .body(null);
        }
    }
    /**
     * {@code GET  /instruction} : get all the instruction.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instructions in body.
     */
    @GetMapping("/instruction")
    public List<InstructionDTO> getAllInstructions(){
        return instructionService.getAll();
    }

    /**
     * {@code GET  /instruction/:id} : get the "id" instruction.
     *
     * @param id the id of the instructionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instructionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/instruction/{id}")
    public ResponseEntity<InstructionDTO> getInstruction(@PathVariable Long id){
        Optional<InstructionDTO> result = instructionService.getOne(id);
        return ResponseUtility.wrapOrNotFound(result);
    }

    /**
     * {@code DELETE  /instruction/:id} : delete the "id" instruction.
     *
     * @param id the id of the instructionDTO to delete.
     * @return the {@link ResponseEntity} wixth status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/instruction/{id}")
    public ResponseEntity<Void> deleteInstruction(@PathVariable Long id){
        instructionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtility.createEntityDeletionAlert("Instruction", id.toString())).build();
    }
}
