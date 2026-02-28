package com.mister.lacurvaleague.modelos.dto;

import java.util.List;

import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterLlorosDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class LlorometroDTO {

    private int numeroJornada;
    private List<MisterLlorosDTO> equiposLloros;
}

