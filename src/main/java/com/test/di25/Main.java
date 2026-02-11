package com.test.di25;

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

        RestaurantDao restaurantDao = new RestaurantDao();
        RestaurantConsole restaurantConsole = new RestaurantConsole(restaurantDao);

        EmployeDao  employeDao = new EmployeDao();
        EmployeConsole employeConsole = new EmployeConsole(employeDao, restaurantDao);

        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenuPrincipal();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> InitDatabase.init();
                    case "2" -> restaurantConsole.run();
                    case "3" -> employeConsole.run();
                    case "0" -> {
                        continuer = false;
                        IO.println("Au revoir");
                    }
                    default -> IO.println("Choix invalide. Réessayez.\n");
                }
            }
        } finally {
        }
    }

    private static void afficherMenuPrincipal() {
        IO.println("\n=== MENU PRINCIPAL ===");
        IO.println("1. Générer des données de test (Faker");
        IO.println("2. Gestion des restaurants");
        IO.println("3. Gestion des employés");
        IO.println("0. Quitter");
        IO.println("Votre choix : ");
    }

}