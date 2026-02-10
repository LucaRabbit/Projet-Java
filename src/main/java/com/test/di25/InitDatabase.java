package com.test.di25;

import net.datafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

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

                // --- Génération des plats indépendants ---
                int nbPlatsIndep = faker.number().numberBetween(5, 12);
                for (int p = 0; p < nbPlatsIndep; p++) {

                    Plat plat = new Plat();
                    plat.setNom(faker.food().dish());
                    plat.setPrix(BigDecimal.valueOf(faker.number().randomDouble(2, 8, 25)));
                    plat.setDescription(faker.food().ingredient());
                    plat.setRestaurant(restaurant);

                    restaurant.getPlats().add(plat);
                    session.persist(plat);

                    // Stock
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
                    menu.setNom("Menu " + m);
                    menu.setRestaurant(restaurant);

                    int nbPlatsMenu = faker.number().numberBetween(2, 5);
                    for (int p = 0; p < nbPlatsMenu; p++) {

                        Plat platChoisi = restaurant.getPlats().get(
                                faker.number().numberBetween(0, restaurant.getPlats().size())
                        );

                        menu.addPlat(platChoisi); // Many-to-Many
                    }

                    restaurant.addMenu(menu);
                    session.persist(menu);
                }

                // Tables
                int nbTables = faker.number().numberBetween(3, 12);
                for (int j = 1; j <= nbTables; j++) {

                    TableResto table = new TableResto();
                    table.setNumero(j);
                    table.setPlaces(faker.number().numberBetween(2, 10));
                    table.setEstDisponible(faker.bool().bool());
                    restaurant.addTable(table);

                    // Clients
                    int nbClients = faker.number().numberBetween(0, table.getPlaces());
                    for (int c = 0; c < nbClients; c++) {

                        Client client = new Client();
                        client.setNom(faker.name().lastName());
                        client.setPrenom(faker.name().firstName());
                        client.setTelephone(faker.phoneNumber().cellPhone());

                        session.persist(client);

                        // Commande
                        Commande commande = new Commande();
                        commande.setClient(client);
                        commande.setTable(table);
                        commande.setDateHeure(
                                LocalDateTime.now()
                                        .minusMinutes(faker.number().numberBetween(5, 300))
                                        .withNano(0)
                        );

                        // Statut aléatoire
                        String[] statuts = {"EN_COURS", "TERMINEE", "PAYEE"};
                        commande.setStatut(statuts[faker.number().numberBetween(0, statuts.length)]);

                        // Total en BigDecimal
                        BigDecimal total = BigDecimal.ZERO;

                        // Plusieurs plats
                        int nbPlats = faker.number().numberBetween(1, 4);
                        for (int p = 0; p < nbPlats; p++) {
                            Plat platChoisi = restaurant.getPlats().get(
                                    faker.number().numberBetween(0, restaurant.getPlats().size())
                            );
                            commande.addPlat(platChoisi);

                            total = total.add(platChoisi.getPrix());
                        }

                        // Plusieurs menus
                        int nbMenusCommande = faker.number().numberBetween(0, 3);
                        for (int m = 0; m < nbMenusCommande; m++) {
                            Menu menuChoisi = restaurant.getMenus().get(
                                    faker.number().numberBetween(0, restaurant.getMenus().size())
                            );
                            commande.addMenu(menuChoisi);

                            double prixMenu = menuChoisi.getPlats().stream()
                                    .mapToDouble(p -> p.getPrix().doubleValue())
                                    .average()
                                    .orElse(15.0);

                            total = total.add(BigDecimal.valueOf(prixMenu));
                        }

                        // Arrondi final
                        total = total.setScale(2, RoundingMode.HALF_UP);
                        commande.setTotal(total.doubleValue());

                        session.persist(commande);
                    }
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

                session.persist(restaurant);
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
