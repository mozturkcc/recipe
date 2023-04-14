package com.example.recipe.controller;

import com.example.recipe.controller.utilities.HeaderUtility;
import com.example.recipe.controller.utilities.ResponseUtility;
import com.example.recipe.domain.RecipeType;
import com.example.recipe.dto.RecipeDTO;
import com.example.recipe.dto.RecipeSearchDTO;
import com.example.recipe.service.RecipeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RecipeController {
    RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    /**
     * {@code POST  /recipe} : Create a new recipe.
     *
     * @param recipeDTO the ingredinetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipeDTO, or with status {@code 400 (Bad Request)} if the recipe has already an ID.
     */
    @PostMapping("/recipe")
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO){
        if(!recipeService.exists(recipeDTO)) {
            RecipeDTO result = recipeService.create(recipeDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityCreationAlert("recipe", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("recipe", "idexists", "A new recipe cannot be created with an existing id"))
                    .body(null);
        }
    }

    /**
     * {@code PUT  /recipe/:id} : Updates an existing recipe.
     *
     * @param id the id of the recipe to update.
     * @param recipeDTO the recipe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipe,
     * or with status {@code 400 (Bad Request)} if the recipe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipe couldn't be updated.
     */
    @PutMapping("/recipe/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable(value = "id", required = false) final Long id,
                                                          @Valid @RequestBody RecipeDTO recipeDTO){
        if(id != null && id.equals(recipeDTO.getId()) && recipeService.exists(recipeDTO)) {
            RecipeDTO result = recipeService.update(recipeDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityUpdateAlert("recipe", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("recipe", "idNOTexists", "The entity to be updated does not exists"))
                    .body(null);
        }
    }

    /**
     * {@code PATCH  /recipe/:id} : Partial updates given fields of an existing recipe, field will ignore if it is null
     *
     * @param id the id of the recipeDTO to save.
     * @param recipeDTO the recipeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeDTO,
     * or with status {@code 400 (Bad Request)} if the recipeDTO is not valid or not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipe/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RecipeDTO> partialUpdateRecipe(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody RecipeDTO recipeDTO
    ) {
        if(id != null && id.equals(recipeDTO.getId()) && recipeService.exists(recipeDTO)) {


            RecipeDTO result = recipeService.partialUpdate(recipeDTO);

            return ResponseEntity
                    .ok()
                    .headers(HeaderUtility.createEntityUpdateAlert("recipe", result.getId().toString()))
                    .body(result);
        } else {
            return ResponseEntity
                    .badRequest()
                    .headers(HeaderUtility.createFailureAlert("recipe", "idNOTexists", "The entity to be updated does not exists"))
                    .body(null);
        }
    }


    /**
     * {@code GET  /recipe} : get all the recipe.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipes in body.
     */
    @GetMapping("/recipe")
    public List<RecipeDTO> getAllRecipes(){
        return recipeService.getAll();
    }

    /**
     * {@code GET  /recipe/:id} : get the "id" recipe.
     *
     * @param id the id of the recipeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipe/{id}")
    public ResponseEntity<RecipeDTO> getRecipe(@PathVariable Long id){
        Optional<RecipeDTO> result = recipeService.getOne(id);
        return ResponseUtility.wrapOrNotFound(result);
    }

    /**
     * {@code GET  /recipe/filter} : get all the recipe whith given filter.
     * @param searchDTO to search recipes. You can add ingrideints and instructions for search. Search
     *                  will be positive if your search text found in the recipe records.
     *                  You can arrange search by contains flag in the ingredient or instruction.
     *                  If the flag is false search will return the recipes does not contain "the text".
     *                  Also typeEquals flag is for the recipe type match: VEGAN,VEGETARIAN etc.
     *                  Search will return the recipes which has type equals to the given type if the flag is true and vice verse.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipes in body.
     */
    @GetMapping("/recipe/filter")
    public List<RecipeDTO> getRecipe(@Valid @RequestBody RecipeSearchDTO searchDTO){
        List<RecipeDTO> result = recipeService.getAllBySearchDTO(searchDTO);
        return result;
    }

    /**
     * {@code DELETE  /recipe/:id} : delete the "id" recipe.
     *
     * @param id the id of the recipeDTO to delete.
     * @return the {@link ResponseEntity} wixth status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id){
        recipeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtility.createEntityDeletionAlert("recipe", id.toString())).build();
    }

}
