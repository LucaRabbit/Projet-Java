package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

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

    public Restaurant find
}
