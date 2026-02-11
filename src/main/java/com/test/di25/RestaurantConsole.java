package com.test.di25;

import java.util.List;
import java.util.Optional;

public class RestaurantConsole {

    private final RestaurantDao restaurantDao;

    public RestaurantConsole(RestaurantDao restaurantDao) {
        this.restaurantDao = restaurantDao;
    }

    public void run() {
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> creer();
                    case "2" -> lister();
                    case "3" -> afficherParId();
                    case "4" -> modifier();
                    case "5" -> supprimer();
                    case "0" -> {
                        continuer = false;
                        IO.println("Retour au menu principal");
                    }
                    default -> IO.println("Choix invalide. Réessayez.\n");
                }
            }
        } finally {
        }
    }

    private static void afficherMenu() {
        IO.println("\n--- GESTION DES RESTAURANTS ---");
        IO.println("1. Créer un restaurant ");
        IO.println("2. Lister tous les restaurants");
        IO.println("3. Afficher un restaurant par id");
        IO.println("4. Modifier un restaurant");
        IO.println("5. Supprimer un restaurant par id");
        IO.println("0. Retour");
        IO.print("Votre choix : ");
    }

    // Créer un restaurant
    private void creer() {
        IO.println("Nom : ");
        String nom = IO.readln();
        IO.println("Adresse : ");
        String adresse = IO.readln();
        IO.println("Code Postal : ");
        String codePostal = IO.readln();
        IO.println("Ville : ");
        String ville = IO.readln();

        Restaurant r = new Restaurant();
        r.setNom(nom);
        r.setAdresse(adresse);
        r.setCodePostal(codePostal);
        r.setVille(ville);

        restaurantDao.create(r);
        IO.println("Restaurant créé avec l'id : " + r.getId());
    }

    // Lister tous les restaurants
    private void lister() {
        List<Restaurant> liste = restaurantDao.findAll();
        if (liste.isEmpty()) {
            System.out.println("Aucun restaurant.\n");
            return;
        }
        System.out.println("--- Liste des restaurants ---");
        for (Restaurant r : liste) {
            System.out.printf("[%d] %s - %s, %s %s%n",
                    r.getId(), r.getNom(), r.getAdresse(), r.getCodePostal(), r.getVille());
        }
        System.out.println();
    }

    // Afficher un restaurant via son id
    private void afficherParId() {
        System.out.print("Id du restaurant : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            Optional<Restaurant> opt = restaurantDao.findById(id);
            if (opt.isEmpty()) {
                System.out.println("Restaurant non trouvé.\n");
                return;
            }
            Restaurant r = opt.get();
            System.out.printf("\n[%d] %s - %s, %s %s%n",
                    r.getId(), r.getNom(), r.getAdresse(), r.getCodePostal(), r.getVille());
            System.out.println("Nombre de tables : " + r.getTables().size());
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n");
        }
        System.out.println();
    }

    // Modifier un restaurant
    private void modifier() {
        System.out.print("Id du restaurant à modifier : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            Optional<Restaurant> opt = restaurantDao.findById(id);
            if (opt.isEmpty()) {
                System.out.println("Restaurant non trouvé.\n");
                return;
            }
            Restaurant r = opt.get();
            System.out.print("Nouveau nom (actuel: " + r.getNom() + ") : ");
            String nom = IO.readln();
            if (!nom.isEmpty()) r.setNom(nom);
            System.out.print("Nouvelle adresse (actuel: " + r.getAdresse() + ") : ");
            String adresse = IO.readln();
            if (!adresse.isEmpty()) r.setAdresse(adresse);
            System.out.print("Nouveau code postal (actuel: " + r.getCodePostal() + ") : ");
            String codePostal = IO.readln();
            if (!codePostal.isEmpty()) r.setCodePostal(codePostal);
            System.out.print("Nouvelle ville (actuel: " + r.getVille() + ") : ");
            String ville = IO.readln();
            if (!ville.isEmpty()) r.setVille(ville);

            restaurantDao.update(r);
            System.out.println("Restaurant mis à jour.\n");
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n");
        }
    }

     // Supprimer un restaurant via son id
    private void supprimer() {
        System.out.print("Id du restaurant à supprimer : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            if (restaurantDao.deleteById(id)) {
                System.out.println("Restaurant supprimé.\n");
            } else {
                System.out.println("Restaurant non trouvé.\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n");
        }
    }
}
