package com.example.recipe.controller;

import com.example.recipe.controller.utilities.ResponseUtility;
import com.example.recipe.dto.IngredientDTO;
import com.example.recipe.service.IngredientService;
import com.example.recipe.controller.utilities.HeaderUtility;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class IngredientController {
    IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }


    /**
     * {@code POST  /ingredient} : Create a new ingredient.
     *
     * @param ingredientDTO the ingredinetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ingredientDTO, or with status {@code 400 (Bad Request)} if the ingredient has already an ID.
     */
    @PostMapping("/ingredient")
    public ResponseEntity<IngredientDTO> createIngredient(@Valid @RequestBody IngredientDTO ingredientDTO){
        if(!ingredientService.exists(ingredientDTO)) {
            IngredientDTO result = ingredientService.create(ingredientDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityCreationAlert("ingredient", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("ingredient", "idexists", "A new ingredient cannot be created with an existing id"))
                    .body(null);
        }
    }

    /**
     * {@code PUT  /ingredient/:id} : Updates an existing ingredient.
     *
     * @param id the id of the ingredient to update.
     * @param ingredientDTO the ingredient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ingredient,
     * or with status {@code 400 (Bad Request)} if the ingredient is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ingredient couldn't be updated.
     */
    @PutMapping("/ingredient/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable(value = "id", required = false) final Long id,
                                                          @Valid @RequestBody IngredientDTO ingredientDTO){
        if(id != null && id.equals(ingredientDTO.getId()) && ingredientService.exists(ingredientDTO)) {
            IngredientDTO result = ingredientService.update(ingredientDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityUpdateAlert("ingredient", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("ingredient", "idNOTexists", "The entity to be updated does not exists"))
                    .body(null);
        }
    }
    /**
     * {@code GET  /ingredient} : get all the ingredient.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ingredients in body.
     */
    @GetMapping("/ingredient")
    public List<IngredientDTO> getAllIngredients(){
        return ingredientService.getAll();
    }

    /**
     * {@code GET  /ingredient/:id} : get the "id" ingredient.
     *
     * @param id the id of the ingredientDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ingredientDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ingredient/{id}")
    public ResponseEntity<IngredientDTO> getIngredient(@PathVariable Long id){
        Optional<IngredientDTO> ingredientDTO = ingredientService.getOne(id);
        return ResponseUtility.wrapOrNotFound(ingredientDTO);
    }

    /**
     * {@code DELETE  /ingredient/:id} : delete the "id" ingredient.
     *
     * @param id the id of the ingredientDTO to delete.
     * @return the {@link ResponseEntity} wixth status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ingredient/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id){
        ingredientService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtility.createEntityDeletionAlert("ingredient", id.toString())).build();
    }

}
