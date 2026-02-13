package com.test.di25;

// Imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestaurantConsole {

    // DAO
    private final RestaurantDao restaurantDao;
    private final PlatDao platDao;
    private final MenuDao menuDao;

    // Injections DAO
    public RestaurantConsole(RestaurantDao restaurantDao, PlatDao platDao, MenuDao menuDao) {
        this.restaurantDao = restaurantDao;
        this.platDao = platDao;
        this.menuDao = menuDao;
    }

    // Boucle du menu
    public void run() {
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> creer();                    // Créer un restaurant OK
                    case "2" -> lister();                   // Lister tous les restaurants OK
                    case "3" -> afficherParId();            // Afficher un restaurant via id OK
                    case "4" -> modifier();                 // Modifier un restaurant OK
                    case "5" -> supprimer();                // Supprimer un restaurant OK
                    case "6" -> gererPlatsPourRestaurant(); // Gérer les plats d'un restaurant OK
                    case "7" -> gererMenusPourRestaurant(); // Gérer les menus d'un restaurant OK
                    case "0" -> {
                        continuer = false;
                        IO.println("Retour au menu principal");
                    }
                    default -> IO.println("Choix invalide. Réessayez.\n"); // Message d'erreur
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

    // Méthode : Créer un restaurant
    private void creer() {
        IO.println("Nom : ");
        String nom = IO.readln();
        IO.println("Adresse : ");
        String adresse = IO.readln();
        IO.println("Code Postal : ");
        String codePostal = IO.readln();
        IO.println("Ville : ");
        String ville = IO.readln();

        // Création objet Restaurant
        Restaurant r = new Restaurant();
        r.setNom(nom);
        r.setAdresse(adresse);
        r.setCodePostal(codePostal);
        r.setVille(ville);

        // Saisie du nombre de places par types de tables
        IO.println("Nombre de tables de 2 places : ");
        int nb2 = Integer.parseInt(IO.readln());

        IO.println("Nombre de tables de 4 places : ");
        int nb4 = Integer.parseInt(IO.readln());

        IO.println("Nombre de tables de 6 places : ");
        int nb6 = Integer.parseInt(IO.readln());

        // Création des tables
        List<TableResto> tables = new ArrayList<>();
        int numero = 1;

        // Création des tables de 2 places
        for (int i = 0; i < nb2; i++) {
            tables.add(creerTable(numero++, 2, r));
        }

        // Création des tables de 4 places
        for (int i = 0; i < nb4; i++) {
            tables.add(creerTable(numero++, 4, r));
        }

        // Création des tables de 6 places
        for (int i = 0; i < nb6; i++) {
            tables.add(creerTable(numero++, 6, r));
        }

        // Association des tables au restaurant
        r.setTables(tables);

        // Sauvegarde
        restaurantDao.create(r);

        IO.println("Restaurant créé avec l'id : " + r.getId());
        IO.println("Tables créées : " + tables.size());
    }

    // Helper : Création de tables
    private TableResto creerTable(int numero, int places, Restaurant r) {
        TableResto t = new TableResto();
        t.setNumero(numero);
        t.setPlaces(places);
        t.setEstDisponible(true);
        t.setRestaurant(r);
        return t;
    }

    // Méthode :  Lister tous les restaurants
    private void lister() {
        List<Restaurant> liste = restaurantDao.findAll();
        // Vérification si des restaurants existent
        if (liste.isEmpty()) {
            System.out.println("Aucun restaurant.\n");
            return;
        }
        // Affichage des restaurants
        System.out.println("--- Liste des restaurants ---");
        for (Restaurant r : liste) {
            System.out.printf("[%d] %s - %s, %s %s%n",
                    r.getId(), r.getNom(), r.getAdresse(), r.getCodePostal(), r.getVille());
        }
        System.out.println();
    }

    // Méthode : Afficher un restaurant via son id
    private void afficherParId() {
        // Demander l'id
        System.out.print("Id du restaurant : ");
        String line = IO.readln();
        try {
            // Recherche du restaurant correspondant à l'id
            Long id = Long.parseLong(line);
            Optional<Restaurant> opt = restaurantDao.findById(id);
            // Pas de correspondance
            if (opt.isEmpty()) {
                System.out.println("Restaurant non trouvé.\n");
                return;
            }
            // Charger le restaurant
            Restaurant r = opt.get();

            // Affichage des données du restaurant
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
        // Demander l'id
        System.out.print("Id du restaurant à modifier : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            // Vérification si existant
            Optional<Restaurant> opt = restaurantDao.findById(id);
            // Pas de correspondance
            if (opt.isEmpty()) {
                System.out.println("Restaurant non trouvé.\n");
                return;
            }
            // Charger le restaurant
            Restaurant r = opt.get();

            // Proposer modification de chaque champ
            // Pas de modification si aucune saisie
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

            // Mise à jour du restaurant
            restaurantDao.update(r);
            System.out.println("Restaurant mis à jour.\n");

        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n"); // Message d'erreur
        }
    }

     //  Méthode : Supprimer un restaurant via son id
     private void supprimer() {
         System.out.print("Id du restaurant à supprimer : ");
         String line = IO.readln();

         try {
             Long id = Long.parseLong(line);

             try {
                 // Vérification si existant
                 boolean deleted = restaurantDao.deleteById(id);

                 if (deleted) {
                     System.out.println("Restaurant supprimé.\n");
                 } else {
                     System.out.println("Restaurant non trouvé.\n");
                 }
             // En cas de contraintes empêchant la suppression
             } catch (IllegalStateException e) {
                 System.out.println("\n" + e.getMessage());
                 System.out.println("Retour au menu précédent.\n");
             }

         } catch (NumberFormatException e) {
             System.out.println("Id invalide.\n"); // Message d'erreur
         }
     }

    // Méthode : Récupérer le restaurant sur lequel gérer les plats
    private void gererPlatsPourRestaurant() {
        // Lister tous les restaurants
        lister();

        IO.println("ID du restaurant à gérer : ");
        Long id = Long.valueOf(IO.readln());

        // Vérifiaction si existant
        var opt = restaurantDao.findById(id);

        // Pas de correspondance
        if (opt.isEmpty()) {
            IO.println("Restaurant introuvable.");
            return;
        }

        // Charger le restaurant
        Restaurant r = opt.get();

        // Passer au restaurant
        gererPlats(r);
    }

    // Menu de gestion des plats d'un restaurant
    private void gererPlats(Restaurant r) {
        boolean continuer = true;

        // Boucle du menu
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
                case "1" -> listerPlats(r);   // Lister tous les plats du restaurant OK
                case "2" -> ajouterPlat(r);   // Ajouter un plat au restaurant OK
                case "3" -> modifierPlat(r);  // Modifier un plat du restaurant OK
                case "4" -> supprimerPlat(r); // Supprimer un plat du restaurant OK
                case "0" -> continuer = false;
                default -> IO.println("Choix invalide."); // Message d'erreur
            }
        }
    }

    // Méthode : Lister tous les plats d'un restaurant
    private void listerPlats(Restaurant r) {
        // Charger tous les plats liés au restaurant
        var plats = platDao.findByRestaurant(r.getId());

        // Aucun plat trouvé
        if (plats.isEmpty()) {
            IO.println("Aucun plat pour ce restaurant.");
            return;
        }

        IO.println("\n--- Liste des plats ---");

        // Regrouper les plats par catégorie
        Map<String, List<Plat>> platsParCategorie = plats.stream()
                .collect(Collectors.groupingBy(Plat::getCategorie));

        // Ordre d'affichage des catégories
        List<String> ordre = List.of("Entrée", "Plat", "Dessert");

        // Affichage des plats par catégorie
        for (String categorie : ordre) {
            List<Plat> liste = platsParCategorie.get(categorie);
            if (liste == null || liste.isEmpty()) continue;

            IO.println("\n" + categorie + "s :");

            for (Plat p : liste) {
                System.out.printf("  [%d] %s - %.2f € - %s%n",
                        p.getId(),
                        p.getNom(),
                        p.getPrix().doubleValue(),
                        p.getDescription()
                );
            }
        }

        IO.println();
    }

    // Methode : Ajouter un plat a un restaurant
    private void ajouterPlat(Restaurant r) {
        // Demander les informations
        IO.println("Nom du plat : ");
        String nom = IO.readln();

        IO.println("Prix : ");
        BigDecimal prix = new BigDecimal(IO.readln());

        IO.println("Description : ");
        String description = IO.readln();

        // Créer l'objet
        Plat p = new Plat();
        p.setNom(nom);
        p.setPrix(prix);
        p.setDescription(description);
        p.setRestaurant(r); // Relier le plat au restaurant

        // Sauvegarde
        platDao.save(p);

        IO.println("Plat ajouté avec ID : " + p.getId());
    }

    // Méthode : Modifier un plat d'un restaurant
    private void modifierPlat(Restaurant r) {
        // Demander l'id
        IO.println("ID du plat à modifier : ");
        Long id = Long.valueOf(IO.readln());

        // Vérification si existant
        Plat p = platDao.findById(id);
        // Vérification de l'appartenance au restaurant
        if (p == null || !p.getRestaurant().getId().equals(r.getId())) {
            IO.println("Plat introuvable pour ce restaurant.");
            return;
        }

        // Proposer modification de chaque champ
        // Pas de modification si aucune saisie
        IO.println("Nouveau nom (" + p.getNom() + ") : ");
        String nom = IO.readln();
        if (!nom.isBlank()) p.setNom(nom);

        IO.println("Nouveau prix (" + p.getPrix() + ") : ");
        String prix = IO.readln();
        if (!prix.isBlank()) p.setPrix(new BigDecimal(prix));

        IO.println("Nouvelle description (" + p.getDescription() + ") : ");
        String desc = IO.readln();
        if (!desc.isBlank()) p.setDescription(desc);

        // Sauvegarde
        platDao.save(p);

        IO.println("Plat modifié.");
    }

    // Méthode : Supprimer un plat d'un restaurant
    private void supprimerPlat(Restaurant r) {
        IO.println("ID du plat à supprimer : ");
        Long id = Long.valueOf(IO.readln());

        // Vérification si existant
        Plat p = platDao.findById(id);
        // Vérification de l'appartenance au restaurant
        if (p == null || !p.getRestaurant().getId().equals(r.getId())) {
            IO.println("Plat introuvable pour ce restaurant.");
            return;
        }
        // Suppression du plat
        try {
            platDao.delete(id);
            IO.println("Plat supprimé.");

        // En cas de contraintes empêchant la suppression
        } catch (IllegalStateException e) {
            IO.println("\n" + e.getMessage());
            IO.println("Retour au menu précédent.\n");
        }
    }

    // Méthode : Récupérer le restaurant sur lequel gérer les menus
    private void gererMenusPourRestaurant() {
        // Lister tous les restaurant
        lister();
        // Demander l'id
        IO.println("ID du restaurant à gérer : ");
        Long id = Long.valueOf(IO.readln());

        // Vérification si existant
        var opt = restaurantDao.findById(id);
        // Pas de correspondance
        if (opt.isEmpty()) {
            IO.println("Restaurant introuvable.");
            return;
        }

        // Charger le restaurant
        Restaurant r = opt.get();

        // Passer au restaurant
        gererMenus(r);
    }

    // Menu de gestion des menus d'un restaurant
    private void gererMenus(Restaurant r) {
        boolean continuer = true;

        // Boucle du menu
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
                case "1" -> listerMenus(r);     // Lister tous les menus du restaurant OK
                case "2" -> creerMenu(r);       // Ajouter un menu au restaurant OK
                case "3" -> ajouterPlatMenu(r); // Ajouter un plat à un menu du restaurant OK
                case "4" -> retirerPlatMenu(r); // Retirer un plat d'un menu du restaurant OK
                case "5" -> supprimerMenu(r);   // Supprimer un menu d'un restaurant OK
                case "0" -> continuer = false;
                default -> IO.println("Choix invalide."); // Message d'erreur
            }
        }
    }

    // Methode : Lister les menus d'un restaurant
    private void listerMenus(Restaurant r) {
        // Charger tous les menus liés au restaurant
        var menus = menuDao.findByRestaurant(r.getId());

        // Aucun menu
        if (menus.isEmpty()) {
            IO.println("Aucun menu pour ce restaurant.");
            return;
        }

        // Affichage des menus
        IO.println("\n--- Liste des menus ---");

        for (Menu m : menus) {
            IO.println("\nNom : " + m.getNom() + " - ID : " + m.getId());

            // Affichage du prix du menu
            if (m.getPrix() != null) {
                System.out.printf("Prix du menu : %.2f €%n", m.getPrix().doubleValue());
            } else {
                IO.println("Prix du menu : non défini");
            }

            // Regrouper les plats par catégorie
            Map<String, List<Plat>> platsParCategorie = m.getPlats().stream()
                    .collect(Collectors.groupingBy(Plat::getCategorie));

            // Orde d'affichage des catégories
            List<String> ordre = List.of("Entrée", "Plat", "Dessert");

            for (String categorie : ordre) {
                List<Plat> liste = platsParCategorie.get(categorie);
                if (liste == null || liste.isEmpty()) continue;
                // Affichage de la catégorie
                IO.println("  " + categorie + "s :");
                // Affichage des plats de la catégorie
                for (Plat p : liste) {
                    System.out.printf("    - [%d] %s (%.2f €)%n",
                            p.getId(),
                            p.getNom(),
                            p.getPrix().doubleValue()
                    );
                }
            }
        }

        IO.println();
    }

    // Methode : Créér un nouveau menu d'un restaurant
    private void creerMenu(Restaurant r) {
        // Demander le nom du menu
        IO.println("Nom du menu : ");
        String nom = IO.readln();

        // Créer objet menu
        Menu m = new Menu();
        m.setNom(nom);
        m.setRestaurant(r); // Associer au restaurant

        // Sauvegarde
        menuDao.save(m);

        IO.println("Menu créé avec ID : " + m.getId());
    }

    // Méthode :  Ajouter un plat au menu d'un restaurant
    private void ajouterPlatMenu(Restaurant r) {
        // Demander l'id du menu
        IO.println("ID du menu : ");
        Long idMenu = Long.valueOf(IO.readln());

        // Charger le menu
        Menu menu = menuDao.findById(idMenu);
        // Vérification si existant
        if (menu == null || !menu.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }
        // Demander l'id du plat
        IO.println("ID du plat à ajouter : ");
        Long idPlat = Long.valueOf(IO.readln());

        // Charger le plat
        Plat plat = platDao.findById(idPlat);
        // Vérification si existant
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

        // Recalculer le prix
        menu.recalculerPrix();

        // Sauvegarde
        menuDao.save(menu);

        IO.println("Plat ajouté au menu. Nouveau prix : " +
                (menu.getPrix() != null ? menu.getPrix() + " €" : "non défini"));

    }

    // Retirer un plat du menu d'un restaurant
    private void retirerPlatMenu(Restaurant r) {
        // Demander l'id du menu
        IO.println("ID du menu : ");
        Long idMenu = Long.valueOf(IO.readln());

        // Charger le menu
        Menu m = menuDao.findById(idMenu);
        // Vérification si existant
        if (m == null || !m.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }
        // Demander l'id du plat
        IO.println("ID du plat à retirer : ");
        Long idPlat = Long.valueOf(IO.readln());

        // Chercher le plat dans le menu
        Plat platDansMenu = m.getPlats().stream()
                .filter(p -> p.getId().equals(idPlat))
                .findFirst()
                .orElse(null);

        if (platDansMenu == null) {
            IO.println("Ce plat n'est pas dans ce menu.");
            return;
        }
        // Retirer le plat
        m.removePlat(platDansMenu);

        // Recalculer le prix
        m.recalculerPrix();

        // Sauvegarde
        menuDao.save(m);

        IO.println("Plat retiré du menu. Nouveau prix : " +
                (m.getPrix() != null ? m.getPrix() + " €" : "non défini"));
    }

    // Méthode :  Supprimer un menu d'un restaurant
    private void supprimerMenu(Restaurant r) {
        // Demander l'id
        IO.println("ID du menu à supprimer : ");
        Long id = Long.valueOf(IO.readln());

        // Charger le menu
        Menu m = menuDao.findById(id);
        // Vérification si existant
        if (m == null || !m.getRestaurant().getId().equals(r.getId())) {
            IO.println("Menu introuvable pour ce restaurant.");
            return;
        }

        // Suppression du menu
        try {
            menuDao.delete(id);
            IO.println("Menu supprimé.");

        // En cas de contraintes empêchant la suppression
        } catch (IllegalStateException e) {
            IO.println("\n" + e.getMessage());
            IO.println("Retour au menu précédent.\n");
        }
    }
}
