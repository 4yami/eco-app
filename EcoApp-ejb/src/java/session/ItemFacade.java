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
            "SELECT i FROM Item i WHERE i.sellerId.id = :sellerId ORDER BY i.id DESC", Item.class);
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

    public int countBySellerId(Integer sellerId) {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(i) FROM Item i WHERE i.sellerId.id = :sellerId");
        query.setParameter("sellerId", sellerId);
        return ((Long) query.getSingleResult()).intValue();
    }
}
