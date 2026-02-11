package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Optional;

public class EmployeDao {

    private final SessionFactory sessionFactory;

    public EmployeDao() {
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

    // Création d'un nouveau employé
    public Employe create(Employe employe) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(employe);
            tx.commit();
            return employe;
        }
    }

    // Récupere un employé via id
    public Optional<Employe> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Employe e = session.get(Employe.class, id);
            return Optional.ofNullable(e);
        }
    }

    // Lister tous les employés
    public List<Employe> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Employe e order by e.id", Employe.class).list();
        }
    }

    // Lister les employés d'un restaurant via id
    public List<Employe> findByRestaurantId(Long restaurantId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "from Employe e where e.restaurant.id = :id order by e.id",
                            Employe.class
                    )
                    .setParameter("id", restaurantId)
                    .list();
        }
    }

    // Mise à jour d'un employé
    public Employe update(Employe employe) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(employe);
            tx.commit();
            return employe;
        }
    }

    // Supprime un employé via id
    public boolean deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Employe e = session.get(Employe.class, id);
            if (e == null) {
                tx.rollback();
                return false;
            }
            session.remove(e);
            tx.commit();
            return true;
        }
    }

}
