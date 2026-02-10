package com.test.di25;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;
    private String statut;
    private Double total;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableResto table;

    public TableResto getTable() {
        return table;
    }
    public void setTable(TableResto table) {
        this.table = table;
    }

    @ManyToMany
    @JoinTable(
            name = "commande_menus",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private List<Menu> menus = new ArrayList<>();

    public List<Menu> getMenus() {
        return menus;
    }
    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
    public void addMenu(Menu menu) {
        menus.add(menu);
    }

    @ManyToMany
    @JoinTable(
            name = "commande_plats",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "plat_id")
    )
    private List<Plat> plats = new ArrayList<>();

    public List<Plat> getPlats() {
        return plats;
    }
    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }
    public void addPlat(Plat plat) {
        plats.add(plat);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
}