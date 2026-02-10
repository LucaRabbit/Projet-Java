package com.test.di25;

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
        IO.println("1. Créer un restaurant");
        IO.println("2. Lister tous les restaurants");
        IO.println("3. Afficher un restaurant par id");
        IO.println("4. Modifier un restaurant");
        IO.println("3. Supprimer un restaurant par id");
        IO.println("0. Retour");
        IO.print("Votre choix : ");
    }

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

    private void lister() {
        List<Restaurant> lister = restaurantDao.findAll();
    }
}
