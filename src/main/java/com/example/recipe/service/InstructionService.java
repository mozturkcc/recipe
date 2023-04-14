package com.example.recipe.service;

import com.example.recipe.domain.Instruction;
import com.example.recipe.dto.InstructionDTO;
import com.example.recipe.mapper.InstructionMapper;
import com.example.recipe.repository.InstructionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructionService {
    InstructionRepository instructionRepository;

    public InstructionService(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    public boolean exists(InstructionDTO instructionDTO){
        boolean exists = false;
        Long id = instructionDTO.getId();
        String instruction = instructionDTO.getInstruction();
        if(id != null && instructionRepository.existsById(id)){
            exists = true;
        }
        if (instruction != null && instructionRepository.existsByInstruction(instruction)){
            exists =true;
        }
        return exists;
    }

    public InstructionDTO create(InstructionDTO instructionDTO){

        Instruction instruction = InstructionMapper.MAPPER.map(instructionDTO);
        Instruction savedInstruction = instructionRepository.save(instruction);
        InstructionDTO result = InstructionMapper.MAPPER.map(savedInstruction);
        return result;
    }

    public InstructionDTO update(InstructionDTO instructionDTO){
        Instruction instruction = InstructionMapper.MAPPER.map(instructionDTO);
        Instruction savedInstruction = instructionRepository.save(instruction);
        InstructionDTO result = InstructionMapper.MAPPER.map(savedInstruction);

        return result;
    }

    public List<InstructionDTO> getAll(){
        List<InstructionDTO> instructionDTOS =
                instructionRepository.findAll().stream().map(InstructionMapper.MAPPER::map).collect(Collectors.toList());
        return instructionDTOS;
    }

    public Optional<InstructionDTO> getOne(Long id){
        Optional<InstructionDTO> instruction = instructionRepository.findById(id).map(InstructionMapper.MAPPER::map);
        return instruction;
    }

    public void delete(Long id){
        instructionRepository.deleteById(id);
    }
}
