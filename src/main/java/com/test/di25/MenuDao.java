package com.test.di25;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

// Un DAO (Data Access Object) -> une couche qui isole la logique d’accès aux données du reste de l’application
// Le mappage consiste à transformer les données de la base (tables, colonnes) en objets Java (classes, attributs)

public class MenuDao {
    // Utilisation de Hibernate
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    // Récuperer un menu via id
    public Menu findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            // Charger le menu
            Menu m = session.get(Menu.class, id);
            if (m != null) {
                // Charger les plats du menu
                m.getPlats().size();
                // Charger les menus associés à chaque plat
                for (Plat p : m.getPlats()) {
                    p.getMenus().size();
                }
            }
            return m;
        }
    }

    // Récuperer tous les menus d'un restaurant
    public List<Menu> findByRestaurant(Long restaurantId) {
        try (Session session = sessionFactory.openSession()) {

            List<Menu> menus = session.createQuery(
                    "from Menu m where m.restaurant.id = :id order by m.id",
                    Menu.class
            ).setParameter("id", restaurantId).list();

            // Charger les plats associés
            for (Menu m : menus) {
                m.getPlats().size();
            }

            return menus;
        }
    }

    // Sauvegarder/Mettre à jour un menu
    public void save(Menu menu) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(menu);
            tx.commit();
        }
    }

    // Supprimer un menu
    public void delete(Long menuId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            // Charger le menu
            Menu menu = session.get(Menu.class, menuId);
            if (menu == null) {
                tx.rollback();
                return;
            }

            // Vérifier si le menu est associé à une commande
            Long count = session.createQuery(
                    "select count(*) from Commande c join c.menus m where m.id = :id",
                    Long.class
            ).setParameter("id", menuId).getSingleResult();

            if (count > 0) {
                tx.rollback();
                throw new IllegalStateException(
                        "Impossible de supprimer : ce menu est utilisé dans une commande."
                );
            }

            // Charger les plats associés
            menu.getPlats().size();

            // Nettoyer la relation ManyToMany
            for (Plat p : menu.getPlats()) {
                p.getMenus().remove(menu);
            }

            // Supprimer le menu
            session.remove(menu);

            tx.commit();
        }
    }
}