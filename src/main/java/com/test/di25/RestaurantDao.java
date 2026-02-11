package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

public class RestaurantDao {

    private final SessionFactory sessionFactory;

    public RestaurantDao() {
        this.sessionFactory = new Configuration()
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
    }

    public void close() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

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
    public boolean deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Restaurant r = session.get(Restaurant.class, id);
            if (r == null) {
                tx.rollback();
                return false;
            }
            session.remove(r);
            tx.commit();
            return true;
        }
    }

}
