package session;

import entity.Item;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class ItemFacade extends AbstractFacade<Item> {

    @PersistenceContext(unitName = "EcoApp-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemFacade() {
        super(Item.class);
    }

    public List<Item> findBySellerId(Integer sellerId, int start, int max) {
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i WHERE i.sellerId.id = :sellerId AND i.status <> 'PENDING' ORDER BY i.id DESC", Item.class);
        query.setParameter("sellerId", sellerId);
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public List<Item> findAvailable(int start, int max) {
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i WHERE i.status = 'AVAILABLE' ORDER BY i.id DESC", Item.class);
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public int countAvailable() {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.status = 'AVAILABLE'");
        return ((Long) query.getSingleResult()).intValue();
    }

    public List<Item> findRecent(int max) {
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i WHERE i.status = 'AVAILABLE' ORDER BY i.id DESC", Item.class);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public List<Item> findPendingBySellerId(Integer sellerId, int start, int max) {
        TypedQuery<Item> query = em.createQuery(
            "SELECT i FROM Item i WHERE i.status = 'PENDING' AND i.sellerId.id = :sellerId ORDER BY i.id DESC", Item.class);
        query.setParameter("sellerId", sellerId);
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public int countPendingBySellerId(Integer sellerId) {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.status = 'PENDING' AND i.sellerId.id = :sellerId");
        query.setParameter("sellerId", sellerId);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int countBySellerId(Integer sellerId) {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.sellerId.id = :sellerId AND i.status <> 'PENDING'");
        query.setParameter("sellerId", sellerId);
        return ((Long) query.getSingleResult()).intValue();
    }

    public List<Item> findAvailableExcludingSeller(int start, int max, Integer excludeSellerId) {
        String jpql;
        if (excludeSellerId == null) {
            jpql = "SELECT i FROM Item i WHERE i.status = 'AVAILABLE' ORDER BY i.id DESC";
        } else {
            jpql = "SELECT i FROM Item i WHERE i.status = 'AVAILABLE' AND i.sellerId.id != :excludeSellerId ORDER BY i.id DESC";
        }
        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (excludeSellerId != null) {
            query.setParameter("excludeSellerId", excludeSellerId);
        }
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public int countAvailableExcludingSeller(Integer excludeSellerId) {
        String jpql;
        if (excludeSellerId == null) {
            jpql = "SELECT COUNT(i) FROM Item i WHERE i.status = 'AVAILABLE'";
        } else {
            jpql = "SELECT COUNT(i) FROM Item i WHERE i.status = 'AVAILABLE' AND i.sellerId.id != :excludeSellerId";
        }
        javax.persistence.Query query = em.createQuery(jpql);
        if (excludeSellerId != null) {
            query.setParameter("excludeSellerId", excludeSellerId);
        }
        return ((Long) query.getSingleResult()).intValue();
    }
}
