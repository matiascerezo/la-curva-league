package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class MisterLlorosDTO {
    
    private String nombreMister;
    private List<LloroDetalleDTO> lloros;
}
