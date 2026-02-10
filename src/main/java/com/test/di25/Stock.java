package com.test.di25;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDerniereMaj;
    private Integer quantite;

    @OneToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    public Plat getPlat() {
        return plat;
    }
    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateDerniereMaj() {
        return dateDerniereMaj;
    }
    public void setDateDerniereMaj(LocalDateTime dateDerniereMaj) {
        this.dateDerniereMaj = dateDerniereMaj;
    }

    public Integer getQuantite() {
        return quantite;
    }
    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}