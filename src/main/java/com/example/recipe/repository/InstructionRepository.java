package com.example.recipe.repository;

import com.example.recipe.domain.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction,Long> {
    Boolean existsByInstruction(String instruction);
}
