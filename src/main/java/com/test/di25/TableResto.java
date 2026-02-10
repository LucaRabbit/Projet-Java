package com.test.di25;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tables")
public class TableResto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;
    private Integer places;
    private Boolean estDisponible;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Restaurant getRestaurant() {
        return restaurant;
    }
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<Commande> commandes = new ArrayList<>();

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getPlaces() {
        return places;
    }
    public void setPlaces(Integer places) {
        this.places = places;
    }

    public Boolean getEstDisponible() {
        return estDisponible;
    }
    public void setEstDisponible(Boolean estDisponible) {
        this.estDisponible = estDisponible;
    }
}