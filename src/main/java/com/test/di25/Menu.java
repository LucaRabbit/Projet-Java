package com.test.di25;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(
            name = "menu_plats",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "plat_id")
    )
    private List<Plat> plats = new ArrayList<>();

    public void addPlat(Plat plat) {
        plats.add(plat);
        plat.getMenus().add(this);
    }

    public void removePlat(Plat plat) {
        plats.remove(plat);
        plat.getMenus().remove(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<Plat> getPlats() { return plats; }
    public void setPlats(List<Plat> plats) { this.plats = plats; }
}