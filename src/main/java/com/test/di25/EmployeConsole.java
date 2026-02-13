package com.test.di25;

import java.util.List;
import java.util.Optional;

public class EmployeConsole {
    // DAO
    private final EmployeDao employeDao;
    private final RestaurantDao restaurantDao;
    // Injections DAO
    public EmployeConsole(EmployeDao employeDao, RestaurantDao restaurantDao) {
        this.employeDao = employeDao;
        this.restaurantDao = restaurantDao;
    }
    // Boucle du menu
    public void run() {
        try {
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = IO.readln();
                switch (choix) {
                    case "1" -> creer();               // Créer un employé OK
                    case "2" -> lister();              // Lister tous les employés OK
                    case "3" -> listerParRestaurant(); // Lister tous les employés d'un restaurant OK
                    case "4" -> afficherParId();       // Afficher un employé via id OK
                    case "5" -> modifier();            // Modifier un employé OK
                    case "6" -> supprimer();           // Supprimzr un employé
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
    // Affichage du menu
    private static void afficherMenu() {
        IO.println("\n--- GESTION DES EMPLOYES ---");
        IO.println("1. Créer un employé ");
        IO.println("2. Lister tous les employes");
        IO.println("3. Lister les employés d'un restaurant");
        IO.println("4. Afficher un employé par id");
        IO.println("5. Modifier un employé");
        IO.println("6. Supprimer un employé par id");
        IO.println("0. Retour");
        IO.print("Votre choix : ");
    }

    // Methode : Créer un nouveau employé
    private void creer() {
        // Demander les informations
        IO.println("Nom : ");
        String nom = IO.readln();

        IO.println("Prénom : ");
        String prenom = IO.readln();

        IO.println("Poste : ");
        String poste = IO.readln();

        IO.println("Salaire : ");
        String salaire = IO.readln();

        IO.println("ID du restaurant : ");
        Long idRestaurant = Long.valueOf(IO.readln());

        // Récupération du restaurant
        var optRestaurant = restaurantDao.findById(idRestaurant);
        // Pas de correspondance
        if (optRestaurant.isEmpty()) {
            IO.println("Restaurant introuvable.");
            return;
        }
        // Charger le restaurant
        Restaurant r = optRestaurant.get();

        // Création de l'employé
        Employe e = new Employe();
        e.setNom(nom);
        e.setPrenom(prenom);
        e.setPoste(poste);
        e.setSalaire(Double.valueOf(salaire));
        e.setRestaurant(r);

        // Sauvegarde
        employeDao.create(e);

        IO.println("Employé créé avec l'id : " + e.getId());
    }

    // Lister tous les employés
    private void lister() {
        List<Employe> liste = employeDao.findAll();
        if (liste.isEmpty()) {
            System.out.println("Aucun employé.\n");
            return;
        }
        // Affichage des employés
        System.out.println("--- Liste des employés ---");
        for (Employe e : liste) {
            System.out.printf("[%d] %s - %s, %s %s €%n",
                    e.getId(), e.getNom(), e.getPrenom(), e.getPoste(), e.getSalaire());
        }
        System.out.println();
    }

    // Méthode : Lister les employés d'un restaurant
    private void listerParRestaurant() {
        // Demander l'id
        IO.println("ID du restaurant : ");
        Long id = Long.valueOf(IO.readln());

        // Charger les employés
        var employes = employeDao.findByRestaurantId(id);
        // PAs d'employés
        if (employes.isEmpty()) {
            IO.println("Aucun employé trouvé pour ce restaurant.");
            return;
        }
        // Affichage des employés du restaurant
        IO.println("\n--- Employés du restaurant " + id + " ---");
        for (Employe e : employes) {
            System.out.printf("[%d] %s - %s, %s %s%n",
                    e.getId(), e.getNom(), e.getPrenom(), e.getPoste(), e.getSalaire());
        }
        System.out.println();
    }

    // Methode : Afficher un employé via son id
    private void afficherParId() {
        System.out.print("Id de l'employé : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            // Vérification si existant
            Optional<Employe> opt = employeDao.findById(id);
            // Pas de correspondance
            if (opt.isEmpty()) {
                System.out.println("Employé non trouvé.\n");
                return;
            }
            // Charger l'employé
            Employe e = opt.get();

            // Affichage de l'employé
            System.out.printf("\n[%d] %s - %s, %s %s%n",
                    e.getId(), e.getNom(), e.getPrenom(), e.getPoste(), e.getSalaire());
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n");
        }
        System.out.println();
    }

    // Methode :  Modifier un restaurant
    private void modifier() {
        // Demander L'id
        System.out.print("Id de l'employé à modifier : ");
        String line = IO.readln();
        try {
            Long id = Long.parseLong(line);
            // Vérification si existant
            Optional<Employe> opt = employeDao.findById(id);
            // Pas de correspondance
            if (opt.isEmpty()) {
                System.out.println("Employé non trouvé.\n");
                return;
            }
            // Charger l'employé
            Employe e = opt.get();

            // Proposer modification de chaque champ
            // Pas de modification si aucune saisie
            System.out.print("Nouveau nom (actuel: " + e.getNom() + ") : ");
            String nom = IO.readln();
            if (!nom.isEmpty()) e.setNom(nom);

            System.out.print("Nouveau prénom (actuel: " + e.getPrenom() + ") : ");
            String prenom = IO.readln();
            if (!prenom.isEmpty()) e.setPrenom(prenom);

            System.out.print("Nouveau poste (actuel: " + e.getPoste() + ") : ");
            String poste = IO.readln();
            if (!poste.isEmpty()) e.setPoste(poste);

            System.out.print("Nouveau salaire (actuel: " + e.getSalaire() + ") : ");
            String salaire = IO.readln();
            if (!salaire.isEmpty()) e.setSalaire(Double.valueOf(salaire));

            // Mise a jour de l'employé
            employeDao.update(e);

            System.out.println("Employé mis à jour.\n");
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n"); // Message d'erreur
        }
    }

    // Methode : Supprimer un employe via son id
    private void supprimer() {
        // Demander l'id
        System.out.print("Id de l'employé à supprimer : ");
        String line = IO.readln();
        // Suppression de l'employé
        try {
            Long id = Long.parseLong(line);
            if (employeDao.deleteById(id)) {
                System.out.println("Employé supprimé.\n");
            } else {
                System.out.println("Employé non trouvé.\n");
            }

        // En cas de contraintes empêchant la suppression
        } catch (NumberFormatException e) {
            System.out.println("Id invalide.\n");
        }
    }

}
