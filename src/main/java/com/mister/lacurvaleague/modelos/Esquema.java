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
public class Esquema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long esquemaId;
    private String esquema;

    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;
}
