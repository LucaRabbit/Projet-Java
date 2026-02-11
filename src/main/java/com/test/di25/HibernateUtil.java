package com.test.di25;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
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
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du SessionFactory", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}