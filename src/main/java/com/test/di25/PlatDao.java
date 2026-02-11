package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class PlatDao {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // Récuperer un plat via id
    public Plat findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Plat p = session.get(Plat.class, id);
            if (p != null) {
                p.getMenus().size();
            }
            return p;
        }
    }

    // Lister les plats d'un restaurant via id
    public List<Plat> findByRestaurant(Long restaurantId) {
        try (Session session = sessionFactory.openSession()) {

            List<Plat> plats = session.createQuery(
                    "from Plat p where p.restaurant.id = :id order by p.id",
                    Plat.class
            ).setParameter("id", restaurantId).list();

            // Charger les menus associés
            for (Plat p : plats) {
                p.getMenus().size();
            }

            return plats;
        }
    }

    // Sauvergarder un plat
    public void save(Plat plat) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(plat);
            tx.commit();
        }
    }

    // Supprimer un plat
    public void delete(Long platId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Plat plat = session.get(Plat.class, platId);
            if (plat == null) {
                tx.rollback();
                return;
            }

            // Vérifier si le plat est utilisé dans une commande
            Long count = session.createQuery(
                    "select count(*) from Commande c join c.plats p where p.id = :id",
                    Long.class
            ).setParameter("id", platId).getSingleResult();

            if (count > 0) {
                tx.rollback();
                throw new IllegalStateException(
                        "Impossible de supprimer : ce plat est utilisé dans une commande."
                );
            }

            // Charger les menus associés
            plat.getMenus().size();

            // Nettoyer la relation ManyToMany
            for (Menu m : plat.getMenus()) {
                m.getPlats().remove(plat);
            }

            // Supprimer le plat
            session.remove(plat);

            tx.commit();
        }
    }
}