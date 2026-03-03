package com.mister.lacurvaleague.modelos;

import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JugadorReal implements Persistable<String>{

    @Id
    private String nombreCortoJugador;
    private String nombreJugador;
    private String posicion;
    @Override

    @Nullable
    public String getId() {
        return this.getNombreCortoJugador();
    }
    @Override
    public boolean isNew() {
        return true;
    }
}
