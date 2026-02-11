package com.test.di25;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestaurantConsole {

    private final RestaurantDao restaurantDao;
    private final PlatDao platDao;
    private final MenuDao menuDao;

    public RestaurantConsole(RestaurantDao restaurantDao, PlatDao platDao, MenuDao menuDao) {
        this.restaurantDao = restaurantDao;
        this.platDao = platDao;
        this.menuDao = menuDao;
    }

    public void run() {
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> creer(); // OK
                    case "2" -> lister(); // OK
                    case "3" -> afficherParId(); // OK
                    case "4" -> modifier(); // OK
                    case "5" -> supprimer(); // OK
                    case "6" -> gererPlatsPourRestaurant(); // OK
                    case "7" -> gererMenusPourRestaurant(); // OK
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
        IO.println("6. Gérer les plats d'un restaurant");
        IO.println("7. Gérer les menus d'un restaurant");
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

        IO.println("Nombre de tables dans ce restaurant : ");
        int nbTables = Integer.parseInt(IO.readln());

        Restaurant r = new Restaurant();
        r.setNom(nom);
        r.setAdresse(adresse);
        r.setCodePostal(codePostal);
        r.setVille(ville);

        // Création des tables
        List<TableResto> tables = new ArrayList<>();

        for (int i = 1; i <= nbTables; i++) {
            IO.println("Nombre de places pour la table " + i + " : ");
            int places = Integer.parseInt(IO.readln());

            TableResto t = new TableResto();
            t.setNumero(i);
            t.setPlaces(places);
            t.setEstDisponible(true);
            t.setRestaurant(r);

            tables.add(t);
        }

        r.setTables(tables);
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

             try {
                 boolean deleted = restaurantDao.deleteById(id);

                 if (deleted) {
                     System.out.println("Restaurant supprimé.\n");
                 } else {
                     System.out.println("Restaurant non trouvé.\n");
                 }

             } catch (IllegalStateException e) {
                 System.out.println("\n" + e.getMessage());
                 System.out.println("Retour au menu précédent.\n");
             }

         } catch (NumberFormatException e) {
             System.out.println("Id invalide.\n");
         }
     }

    // Récupérer le restaurant sur lequel gérer les plats
    private void gererPlatsPourRestaurant() {
        lister();
        IO.println("ID du restaurant à gérer : ");
        Long id = Long.valueOf(IO.readln());

        var opt = restaurantDao.findById(id);
        if (opt.isEmpty()) {
            IO.println("Restaurant introuvable.");
            return;
        }

        Restaurant r = opt.get();
        gererPlats(r);
    }

    // Gérer le plats d'un restaurant
    private void gererPlats(Restaurant r) {
        boolean continuer = true;

        while (continuer) {
            IO.println("\n--- Gestion des plats du restaurant ---");
            IO.println("1. Lister les plats");
            IO.println("2. Ajouter un plat");
            IO.println("3. Modifier un plat");
            IO.println("4. Supprimer un plat");
            IO.println("0. Retour");
            IO.print("Votre choix : ");

            String choix = IO.readln();

            switch (choix) {
                case "1" -> listerPlats(r); // OK
                case "2" -> ajouterPlat(r); // OK
                case "3" -> modifierPlat(r); // OK
                case "4" -> supprimerPlat(r); // OK
                case "0" -> continuer = false;
                default -> IO.println("Choix invalide.");
            }
        }
    }

    // Lister les plats d'un restaurant
    private void listerPlats(Restaurant r) {
        var plats = platDao.findByRestaurant(r.getId());

        if (plats.isEmpty()) {
            IO.println("Aucun plat pour ce restaurant.");
            return;
        }

        IO.println("\n--- Liste des plats ---");
        for (Plat p : plats) {
            System.out.printf("[%d] %s - %bd, %s%n",
                    p.getId(), p.getNom(), p.getPrix(), p.getDescription());
        }
        System.out.println();
    }

    // Ajouter un plat a un restaurant
    private void ajouterPlat(Restaurant r) {
        IO.println("Nom du plat : ");
        String nom = IO.readln();

        IO.println("Prix : ");
        BigDecimal prix = new BigDecimal(IO.readln());

        IO.println("Description : ");
        String description = IO.readln();

        Plat p = new Plat();
        p.setNom(nom);
        p.setPrix(prix);
        p.setDescription(description);
        p.setRestaurant(r);

        platDao.save(p);

        IO.println("Plat ajouté avec ID : " + p.getId());
    }

    // Modifier un plat d'un restaurant
    private void modifierPlat(Restaurant r) {
        IO.println("ID du plat à modifier : ");
        Long id = Long.valueOf(IO.readln());

        Plat p = platDao.findById(id);

        if (p == null || !p.getRestaurant().getId().equals(r.getId())) {
            IO.println("Plat introuvable pour ce restaurant.");
            return;
        }

        IO.println("Nouveau nom (" + p.getNom() + ") : ");
        String nom = IO.readln();
        if (!nom.isBlank()) p.setNom(nom);

        IO.println("Nouveau prix (" + p.getPrix() + ") : ");
        String prix = IO.readln();
        if (!prix.isBlank()) p.setPrix(new BigDecimal(prix));

        IO.println("Nouvelle description (" + p.getDescription() + ") : ");
        String desc = IO.readln();
        if (!desc.isBlank()) p.setDescription(desc);

        platDao.save(p);

        IO.println("Plat modifié.");
    }

    // Supprimer un plat d'un restaurant
    private void supprimerPlat(Restaurant r) {
        IO.println("ID du plat à supprimer : ");
        Long id = Long.valueOf(IO.readln());

        Plat p = platDao.findById(id);

        if (p == null || !p.getRestaurant().getId().equals(r.getId())) {
            IO.println("Plat introuvable pour ce restaurant.");
            return;
        }

        try {
            platDao.delete(id);
            IO.println("Plat supprimé.");
        } catch (IllegalStateException e) {
            IO.println("\n" + e.getMessage());
            IO.println("Retour au menu précédent.\n");
        }
    }

    // Récupérer le restaurant sur lequel gérer les menus
    private void gererMenusPourRestaurant() {
        lister();
        IO.println("ID du restaurant à gérer : ");
        Long id = Long.valueOf(IO.readln());

        var opt = restaurantDao.findById(id);
        if (opt.isEmpty()) {
            IO.println("Restaurant introuvable.");
            return;
        }

        Restaurant r = opt.get();
        gererMenus(r);
    }

    // Gérer les menus d'un restaurant
    private void gererMenus(Restaurant r) {
        boolean continuer = true;

        while (continuer) {
            IO.println("\n--- GESTION DES MENUS DU RESTAURANT ---");
            IO.println("1. Lister les menus");
            IO.println("2. Créer un menu");
            IO.println("3. Ajouter un plat à un menu");
            IO.println("4. Retirer un plat d’un menu");
            IO.println("5. Supprimer un menu");
            IO.println("0. Retour");
            IO.print("Votre choix : ");

            String choix = IO.readln();

            switch (choix) {
                case "1" -> listerMenus(r); // OK
                case "2" -> creerMenu(r); // OK
                case "3" -> ajouterPlatMenu(r); // OK
                case "4" -> retirerPlatMenu(r); // OK
                case "5" -> supprimerMenu(r); // OK
                case "0" -> continuer = false;
                default -> IO.println("Choix invalide.");
            }
        }
    }

    // Lister les menus d'un restaurant
    private void listerMenus(Restaurant r) {
        var menus = menuDao.findByRestaurant(r.getId());

        if (menus.isEmpty()) {
            IO.println("Aucun menu pour ce restaurant.");
            return;
        }

        IO.println("\n--- Liste des menus ---");
        for (Menu m : menus) {
            IO.println("Nom: " + m.getNom() + " - ID: " + m.getId());
            IO.println("Plats :");
            for (Plat p : m.getPlats()) {
                IO.println("    - " + p.getNom() + " - ID: " + p.getId());
            }
        }
    }

    // Créér un nouveau menu d'un restaurant
    private void creerMenu(Restaurant r) {
        IO.println("Nom du menu : ");
        String nom = IO.readln();

        Menu m = new Menu();
        m.setNom(nom);
        m.setRestaurant(r);

        menuDao.save(m);

        IO.println("Menu créé avec ID : " + m.getId());
    }

    // Ajouter un plat au menu d'un restaurant
    private void ajouterPlatMenu(Restaurant r) {
        IO.println("ID du menu : ");
        Long idMenu = Long.valueOf(IO.readln());

        Menu menu = menuDao.findById(idMenu);
        if (menu == null || !menu.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }

        IO.println("ID du plat à ajouter : ");
        Long idPlat = Long.valueOf(IO.readln());

        Plat plat = platDao.findById(idPlat);
        if (plat == null || !plat.getRestaurant().getId().equals(r.getId())) {
            IO.println("Plat introuvable pour ce restaurant.");
            return;
        }

        // Vérifier si le plat est déjà dans le menu
        boolean dejaDansMenu = menu.getPlats().stream()
                .anyMatch(p -> p.getId().equals(idPlat));

        if (dejaDansMenu) {
            IO.println("Ce plat est déjà dans ce menu.");
            return;
        }

        // Ajouter le plat
        menu.addPlat(plat);
        menuDao.save(menu);

        IO.println("Plat ajouté au menu.");
    }

    // Retirer un plat du menu d'un restaurant
    private void retirerPlatMenu(Restaurant r) {
        IO.println("ID du menu : ");
        Long idMenu = Long.valueOf(IO.readln());

        Menu m = menuDao.findById(idMenu);
        if (m == null || !m.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }

        IO.println("ID du plat à retirer : ");
        Long idPlat = Long.valueOf(IO.readln());

        // On cherche le plat dans le menu
        Plat platDansMenu = m.getPlats().stream()
                .filter(p -> p.getId().equals(idPlat))
                .findFirst()
                .orElse(null);

        if (platDansMenu == null) {
            IO.println("Ce plat n'est pas dans ce menu.");
            return;
        }

        m.removePlat(platDansMenu);
        menuDao.save(m);

        IO.println("Plat retiré du menu.");
    }

    // Supprimer un menu d'un restaurant
    private void supprimerMenu(Restaurant r) {
        IO.println("ID du menu à supprimer : ");
        Long id = Long.valueOf(IO.readln());

        Menu m = menuDao.findById(id);

        if (m == null || !m.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }

        try {
            menuDao.delete(id);
            IO.println("Menu supprimé.");
        } catch (IllegalStateException e) {
            IO.println("\n" + e.getMessage());
            IO.println("Retour au menu précédent.\n");
        }
    }
}
