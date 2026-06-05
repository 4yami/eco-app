package session;

import entity.CartItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class CartItemFacade extends AbstractFacade<CartItem> {

    @PersistenceContext(unitName = "EcoApp-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CartItemFacade() {
        super(CartItem.class);
    }

    public List<CartItem> findByUserId(Integer userId, int start, int max) {
        TypedQuery<CartItem> query = em.createNamedQuery("CartItem.findByUserId", CartItem.class);
        query.setParameter("userId", userId);
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public int countByUserId(Integer userId) {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.cartItemPK.userId = :userId");
        query.setParameter("userId", userId);
        return ((Long) query.getSingleResult()).intValue();
    }

    public List<CartItem> findByUserIdAndPurchased(Integer userId, boolean purchased, int start, int max) {
        TypedQuery<CartItem> query = em.createQuery(
            "SELECT c FROM CartItem c WHERE c.cartItemPK.userId = :userId AND c.purchased = :purchased ORDER BY c.dateAdded DESC", CartItem.class);
        query.setParameter("userId", userId);
        query.setParameter("purchased", purchased);
        query.setFirstResult(start);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public int countByUserIdAndPurchased(Integer userId, boolean purchased) {
        javax.persistence.Query query = em.createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.cartItemPK.userId = :userId AND c.purchased = :purchased");
        query.setParameter("userId", userId);
        query.setParameter("purchased", purchased);
        return ((Long) query.getSingleResult()).intValue();
    }
}
