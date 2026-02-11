package com.test.di25;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String nom;

    private String adresse;
    private String codePostal;
    private String ville;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TableResto> tables = new ArrayList<>();

    public List<TableResto> getTables() {
        return tables;
    }
    public void setTables(List<TableResto> tables) {
        this.tables = tables;
    }
    public void addTable(TableResto table) {
        tables.add(table);
        table.setRestaurant(this);
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Employe> employes = new ArrayList<>();

    public List<Employe> getEmployes() {
        return employes;
    }
    public void setEmployes(List<Employe> employes) {
        this.employes = employes;
    }
    public void addEmploye(Employe employe) {
        employes.add(employe);
        employe.setRestaurant(this);
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menus = new ArrayList<>();

    public List<Menu> getMenus() {
        return menus;
    }
    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setRestaurant(this);
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Plat> plats = new ArrayList<>();

    public List<Plat> getPlats() {
        return plats;
    }
    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCodePostal() {
        return codePostal;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }
}