/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author syahm
 */
@Entity
@Table(name = "CART_ITEM")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CartItem.findAll", query = "SELECT c FROM CartItem c"),
    @NamedQuery(name = "CartItem.findByUserId", query = "SELECT c FROM CartItem c WHERE c.cartItemPK.userId = :userId"),
    @NamedQuery(name = "CartItem.findByItemId", query = "SELECT c FROM CartItem c WHERE c.cartItemPK.itemId = :itemId"),
    @NamedQuery(name = "CartItem.findByDateAdded", query = "SELECT c FROM CartItem c WHERE c.dateAdded = :dateAdded")})
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CartItemPK cartItemPK;
    @Column(name = "DATE_ADDED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    @Column(name = "PURCHASED")
    private boolean purchased;
    @Column(name = "REJECTED")
    private boolean rejected;
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AppUser appUser;
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;

    public CartItem() {
    }

    public CartItem(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public CartItem(int userId, int itemId) {
        this.cartItemPK = new CartItemPK(userId, itemId);
    }

    public CartItemPK getCartItemPK() {
        return cartItemPK;
    }

    public void setCartItemPK(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartItemPK != null ? cartItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItem)) {
            return false;
        }
        CartItem other = (CartItem) object;
        if ((this.cartItemPK == null && other.cartItemPK != null) || (this.cartItemPK != null && !this.cartItemPK.equals(other.cartItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CartItem[ cartItemPK=" + cartItemPK + " ]";
    }
    
}
