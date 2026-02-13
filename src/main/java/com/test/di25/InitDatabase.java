package com.test.di25;

import net.datafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class InitDatabase {

    public static void init() {
        IO.println("Démarrage d'Hibernate...");

        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Restaurant.class)
                .addAnnotatedClass(TableResto.class)
                .addAnnotatedClass(Commande.class)
                .addAnnotatedClass(Employe.class)
                .addAnnotatedClass(Menu.class)
                .addAnnotatedClass(Plat.class)
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Stock.class)
                .buildSessionFactory();

        Session session = factory.openSession();

        try {
            session.beginTransaction();

            Faker faker = new Faker(new Locale("fr"));

            for (int i = 1; i <= 10; i++) {

                Restaurant restaurant = new Restaurant();
                restaurant.setNom(faker.restaurant().name());
                restaurant.setAdresse(faker.address().streetAddress());
                restaurant.setCodePostal(faker.address().zipCode());
                restaurant.setVille(faker.address().city());

                session.persist(restaurant);

                // --- Génération des plats indépendants ---
                String dessert = faker.options().option(
                        "Tiramisu",
                        "Crème brûlée",
                        "Mousse au chocolat",
                        "Panna cotta",
                        "Tarte aux pommes",
                        "Fondant au chocolat",
                        "Île flottante",
                        "Profiteroles",
                        "Cheesecake",
                        "Mille-feuille"
                );

                Set<String> nomsPlats = new HashSet<>();

                int nbPlatsIndep = faker.number().numberBetween(5, 12);

                for (int p = 0; p < nbPlatsIndep; p++) {

                    String[] categories = {"Entrée", "Plat", "Dessert"};
                    String categorie = faker.options().option(categories);

                    String nomPlat = switch (categorie) {
                        case "Entrée" -> faker.food().vegetable() + " " + faker.food().spice();
                        case "Plat" -> faker.food().dish();
                        default -> dessert;
                    };

                    if (nomsPlats.contains(nomPlat)) {
                        nomPlat = nomPlat + " " + faker.number().randomDigit();
                    }
                    nomsPlats.add(nomPlat);

                    BigDecimal prix = switch (categorie) {
                        case "Entrée" -> BigDecimal.valueOf(faker.number().randomDouble(2, 5, 12));
                        case "Plat" -> BigDecimal.valueOf(faker.number().randomDouble(2, 12, 25));
                        default -> BigDecimal.valueOf(faker.number().randomDouble(2, 4, 10));
                    };

                    Plat plat = new Plat();
                    plat.setNom(nomPlat);
                    plat.setPrix(prix);
                    plat.setDescription(faker.lorem().sentence(8));
                    plat.setCategorie(categorie);
                    plat.setRestaurant(restaurant);

                    restaurant.getPlats().add(plat);
                    session.persist(plat);

                    Stock stock = new Stock();
                    stock.setPlat(plat);
                    stock.setQuantite(faker.number().numberBetween(5, 50));
                    stock.setDateDerniereMaj(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 10)));

                    session.persist(stock);
                }

                // --- Génération des menus ---
                int nbMenus = faker.number().numberBetween(1, 4);

                for (int m = 1; m <= nbMenus; m++) {

                    Menu menu = new Menu();
                    menu.setNom(faker.options().option(
                            "Menu Découverte",
                            "Menu Tradition",
                            "Menu Gourmand",
                            "Menu Dégustation",
                            "Menu du Chef"
                    ));
                    menu.setRestaurant(restaurant);

                    // Séparer les plats par catégorie
                    List<Plat> entrees = restaurant.getPlats().stream()
                            .filter(p -> "Entrée".equals(p.getCategorie()))
                            .toList();

                    List<Plat> plats = restaurant.getPlats().stream()
                            .filter(p -> "Plat".equals(p.getCategorie()))
                            .toList();

                    List<Plat> desserts = restaurant.getPlats().stream()
                            .filter(p -> "Dessert".equals(p.getCategorie()))
                            .toList();

                    // Vérifier qu'on peut créer un menu complet
                    if (entrees.isEmpty() || plats.isEmpty() || desserts.isEmpty()) {
                        continue; // impossible → on saute ce menu
                    }

                    // --- 1 Entrée ---
                    Plat entree = faker.options().option(entrees.toArray(new Plat[0]));
                    if (!menu.getPlats().contains(entree)) {
                        menu.addPlat(entree);
                    }

                    // --- 1 ou 2 Plats ---
                    int nbPlatsMenu = faker.number().numberBetween(1, 3);

                    List<Plat> platsCopy = new ArrayList<>(plats);
                    platsCopy.remove(entree); // sécurité si jamais

                    for (int pm = 0; pm < nbPlatsMenu && !platsCopy.isEmpty(); pm++) {
                        Plat platChoisi = faker.options().option(platsCopy.toArray(new Plat[0]));
                        menu.addPlat(platChoisi);
                        platsCopy.remove(platChoisi); // éviter les doublons
                    }

                    // --- 1 Dessert ---
                    Plat dessertChoisi = faker.options().option(desserts.toArray(new Plat[0]));
                    if (!menu.getPlats().contains(dessertChoisi)) {
                        menu.addPlat(dessertChoisi);
                    }

                    // --- Calcul automatique du prix du menu ---
                    menu.recalculerPrix();

                    restaurant.addMenu(menu);
                    session.persist(menu);
                }

                // Tables
                int nbTables = faker.number().numberBetween(3, 12);

                for (int j = 1; j <= nbTables; j++) {

                    TableResto table = new TableResto();
                    table.setNumero(j);

                    int places = faker.options().option(2, 4, 6);
                    table.setPlaces(places);

                    // Une table a 70% de chances d'être occupée
                    boolean occupee = faker.random().nextDouble() < 0.7;
                    table.setEstDisponible(!occupee);

                    restaurant.addTable(table);

                    if (!occupee) {
                        // Table libre → pas de clients, pas de commande
                        continue;
                    }

                    // --- Clients assis à la table ---
                    int nbClients = faker.number().numberBetween(1, table.getPlaces());

                    List<Client> clients = new ArrayList<>();

                    for (int c = 0; c < nbClients; c++) {
                        Client client = new Client();
                        client.setNom(faker.name().lastName());
                        client.setPrenom(faker.name().firstName());
                        client.setTelephone(faker.phoneNumber().cellPhone());

                        session.persist(client);
                        clients.add(client);
                    }

                    // --- Commande unique pour la table ---
                    Commande commande = new Commande();
                    commande.setTable(table);

                    // Client principal (celui qui paie)
                    commande.setClient(clients.getFirst());

                    commande.setDateHeure(
                            LocalDateTime.now()
                                    .minusMinutes(faker.number().numberBetween(5, 300))
                                    .withNano(0)
                    );

                    String[] statuts = {"EN_COURS", "TERMINEE", "PAYEE"};
                    commande.setStatut(faker.options().option(statuts));

                    BigDecimal total = BigDecimal.ZERO;

                    // --- Plats commandés ---
                    int nbPlats = faker.number().numberBetween(nbClients, nbClients * 3);

                    for (int p = 0; p < nbPlats; p++) {
                        Plat platChoisi = faker.options().option(
                                restaurant.getPlats().toArray(new Plat[0])
                        );

                        commande.addPlat(platChoisi);
                        total = total.add(platChoisi.getPrix());
                    }

                    // --- Menus commandés ---
                    int nbMenusCommande = faker.number().numberBetween(0, 2);

                    if (!restaurant.getMenus().isEmpty()) {
                        for (int m = 0; m < nbMenusCommande; m++) {
                            Menu menuChoisi = faker.options().option(
                                    restaurant.getMenus().toArray(new Menu[0])
                            );

                            commande.addMenu(menuChoisi);

                            // Utiliser directement le prix du menu si existant
                            BigDecimal prixMenu = menuChoisi.getPrix();
                            if (prixMenu == null) {
                                // fallback sinon
                                double moyenne = menuChoisi.getPlats().stream()
                                        .mapToDouble(p -> p.getPrix().doubleValue())
                                        .average()
                                        .orElse(15.0);
                                prixMenu = BigDecimal.valueOf(moyenne);
                            }

                            total = total.add(prixMenu);
                        }
                    }

                    commande.setTotal(total.setScale(2, RoundingMode.HALF_UP).doubleValue());

                    session.persist(commande);
                }

                // Employés
                int nbEmployes = faker.number().numberBetween(3, 15);
                for (int k = 0; k < nbEmployes; k++) {
                    Employe employe = new Employe();
                    employe.setNom(faker.name().lastName());
                    employe.setPrenom(faker.name().firstName());
                    employe.setPoste(faker.job().position());
                    employe.setSalaire((double) faker.number().numberBetween(1800, 3500));
                    restaurant.addEmploye(employe);
                }

                session.flush();
            }

            session.getTransaction().commit();
            System.out.println("Données générées avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }

}
