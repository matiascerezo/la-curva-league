package com.mister.lacurvaleague.modelos;

import java.util.Date;
import java.util.List;

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
    private long id; //Deberia coincidir con el numero de Jornada.
    private int numeroJornada;
    private Date fechaInicio;
    private Date fechaFin;

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL)
    private List<Equipo> equipos;
}
