package com.mister.lacurvaleague.modelos;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long jornadaId; //Deberia coincidir con el numero de Jornada.
    private int numeroJornada;
    private Date fechaInicio;
    private Date fechaFin;

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Equipo> equipos;
}
