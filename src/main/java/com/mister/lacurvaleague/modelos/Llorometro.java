package com.mister.lacurvaleague.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Llorometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lloroId;
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "mister_id")
    private Mister mister;

    @ManyToOne
    @JoinColumn(name = "jornada_id")
    private Jornada jornada;
    
    
}
