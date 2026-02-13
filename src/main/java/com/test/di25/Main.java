package com.test.di25;

// Imports
import net.datafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;

public class Main {

    public static void main() {

        // Initialisation des DAO
        PlatDao platDao = new PlatDao();
        MenuDao menuDao = new MenuDao();
        RestaurantDao restaurantDao = new RestaurantDao();
        EmployeDao  employeDao = new EmployeDao();

        // Initialisation des consoles
        RestaurantConsole restaurantConsole = new RestaurantConsole(restaurantDao, platDao, menuDao);
        EmployeConsole employeConsole = new EmployeConsole(employeDao, restaurantDao);

        // Boucle menu principal
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenuPrincipal();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> InitDatabase.init();      // Générer des données de test avec Faker
                    case "2" -> restaurantConsole.run();  // Lancer le menu de gestion des restaurants
                    case "3" -> employeConsole.run();     // Lancer le menu de gestion des employés
                    case "0" -> {                         // Quitter le programme
                        continuer = false;
                        IO.println("Au revoir");
                    }
                    default -> IO.println("Choix invalide. Réessayez.\n"); // Message d'erreur
                }
            }
        } finally {
        }
    }

    // Affichage du menu principal
    private static void afficherMenuPrincipal() {
        IO.println("\n=== MENU PRINCIPAL ===");
        IO.println("1. Générer des données de test (Faker)");
        IO.println("2. Gestion des restaurants");
        IO.println("3. Gestion des employés");
        IO.println("0. Quitter");
        IO.println("Votre choix : ");
    }

}