/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package session;

import entity.Role;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author syahm
 */
@Stateless
public class RoleFacade extends AbstractFacade<Role> {

    @PersistenceContext(unitName = "EcoApp-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RoleFacade() {
        super(Role.class);
    }

    public Role findByRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }
        TypedQuery<Role> query = em.createNamedQuery("Role.findByRoleName", Role.class);
        query.setParameter("roleName", roleName);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
     
}
