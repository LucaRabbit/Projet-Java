package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class RestaurantDao {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // Création d'un nouveau restaurant
    public Restaurant create(Restaurant restaurant) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(restaurant);
            tx.commit();
            return restaurant;
        }
    }

    // Lister tous les restaurants
    @SuppressWarnings("unchecked")
    public List<Restaurant> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Restaurant r order by r.id", Restaurant.class).list();
        }
    }

    // Récupere un restaurant via id
    public Optional<Restaurant> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Restaurant r = session.get(Restaurant.class, id);
            return Optional.ofNullable(r);
        }
    }

    // Mise à jour d'un restaurant
    public Restaurant update(Restaurant restaurant) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(restaurant);
            tx.commit();
            return restaurant;
        }
    }

    // Supprime un restaurant via id
    public boolean deleteById(Long restaurantId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Restaurant r = session.get(Restaurant.class, restaurantId);
            if (r == null) {
                tx.rollback();
                return false;
            }

            // Vérifier s'il existe des commandes pour ce restaurant
            Long countCmd = session.createQuery(
                    "select count(*) from Commande c where c.table.restaurant.id = :id",
                    Long.class
            ).setParameter("id", restaurantId).getSingleResult();

            if (countCmd > 0) {
                tx.rollback();
                throw new IllegalStateException(
                        "Impossible de supprimer : ce restaurant possède des commandes."
                );
            }

            // Charger toutes les collections LAZY
            r.getPlats().size();
            r.getMenus().size();
            r.getTables().size();
            r.getEmployes().size();

            // Nettoyer les relations ManyToMany (menus ↔ plats)
            for (Menu m : r.getMenus()) {
                m.getPlats().size();
                for (Plat p : m.getPlats()) {
                    p.getMenus().remove(m);
                }
            }

            // Supprimer les plats (+ les stocks via orphanRemoval)
            for (Plat p : r.getPlats()) {
                session.remove(p);
            }

            // Supprimer les menus
            for (Menu m : r.getMenus()) {
                session.remove(m);
            }

            // Supprimer les tables
            for (TableResto t : r.getTables()) {
                session.remove(t);
            }

            // Supprimer les employés
            for (Employe e : r.getEmployes()) {
                session.remove(e);
            }

            // Supprimer le restaurant
            session.remove(r);

            tx.commit();
        }
        return true;
    }

}
