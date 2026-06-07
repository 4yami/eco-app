package controller;

import entity.CartItem;
import entity.CartItemPK;
import entity.Item;
import controller.util.JsfUtil;
import controller.util.PaginationHelper;
import session.CartItemFacade;
import session.ItemFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("cartItemController")
@SessionScoped
public class CartItemController implements Serializable {

    private CartItem current;
    private DataModel items = null;
    @EJB
    private session.CartItemFacade ejbFacade;
    @EJB
    private ItemFacade itemFacade;
    private int selectedItemIndex;

    private List<CartItem> cartItemList;
    private PaginationHelper historyPagination;
    private DataModel historyItems;

    @Inject
    private LoginController loginBean;

    public CartItemController() {
    }

    public CartItem getSelected() {
        if (current == null) {
            current = new CartItem();
            current.setCartItemPK(new entity.CartItemPK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private CartItemFacade getFacade() {
        return ejbFacade;
    }

    // ----- Cart (unpaginated) -----

    public List<CartItem> getCartItemList() {
        if (cartItemList == null && loginBean != null && loginBean.isLoggedIn()) {
            cartItemList = ejbFacade.findByUserIdAndPurchased(loginBean.getLoggedInUser().getId(), false, 0, Integer.MAX_VALUE);
        }
        return cartItemList;
    }

    public int getCartItemCount() {
        List<CartItem> list = getCartItemList();
        return list == null ? 0 : list.size();
    }

    private void recreateCartItemList() {
        cartItemList = null;
    }

    // ----- Purchase History (paginated) -----

    public PaginationHelper getHistoryPagination() {
        if (historyPagination == null && loginBean != null && loginBean.isLoggedIn()) {
            Integer userId = loginBean.getLoggedInUser().getId();
            historyPagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().countByUserIdAndPurchased(userId, true);
                }
                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findByUserIdAndPurchased(userId, true, getPageFirstItem(), getPageSize()));
                }
            };
        }
        return historyPagination;
    }

    public DataModel getHistoryItems() {
        if (historyItems == null) {
            historyItems = getHistoryPagination().createPageDataModel();
        }
        return historyItems;
    }

    public int getHistoryCount() {
        PaginationHelper ph = getHistoryPagination();
        return ph == null ? 0 : ph.getItemsCount();
    }

    public String historyNext() {
        getHistoryPagination().nextPage();
        historyItems = null;
        return null;
    }

    public String historyPrevious() {
        getHistoryPagination().previousPage();
        historyItems = null;
        return null;
    }

    private void recreateHistory() {
        historyPagination = null;
        historyItems = null;
    }

    // ----- Cart Actions -----

    public String addToCart(Item item) {
        if (loginBean == null || !loginBean.isLoggedIn() || loginBean.isAdmin()) {
            JsfUtil.addErrorMessage("You must be logged in as a regular user to add items to cart.");
            return null;
        }
        if (!"AVAILABLE".equals(item.getStatus())) {
            JsfUtil.addErrorMessage("This item is no longer available.");
            return null;
        }
        if (item.getSellerId() != null && item.getSellerId().getId().equals(loginBean.getLoggedInUser().getId())) {
            JsfUtil.addErrorMessage("You cannot add your own item to cart.");
            return null;
        }

        CartItemPK pk = new CartItemPK(loginBean.getLoggedInUser().getId(), item.getId());
        if (ejbFacade.find(pk) != null) {
            JsfUtil.addErrorMessage("This item is already in your cart.");
            return null;
        }

        try {
            CartItem cartItem = new CartItem();
            cartItem.setCartItemPK(pk);
            cartItem.setAppUser(loginBean.getLoggedInUser());
            cartItem.setItem(item);
            cartItem.setDateAdded(new Date());
            cartItem.setPurchased(false);

            ejbFacade.create(cartItem);

            item.setStatus("IN_CART");
            itemFacade.edit(item);

            JsfUtil.addSuccessMessage("Item added to cart!");
            recreateCartItemList();
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Failed to add item to cart.");
            return null;
        }
    }

    public String removeFromCart(CartItem cartItem) {
        if (cartItem == null || cartItem.getItem() == null) {
            JsfUtil.addErrorMessage("Invalid cart item.");
            return null;
        }

        try {
            Item item = cartItem.getItem();
            item.setStatus("AVAILABLE");
            itemFacade.edit(item);

            ejbFacade.remove(cartItem);

            JsfUtil.addSuccessMessage("Item removed from cart.");
            recreateCartItemList();
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Failed to remove item from cart.");
            return null;
        }
    }

    public String purchaseOne(CartItem cartItem) {
        if (loginBean == null || !loginBean.isLoggedIn()) {
            JsfUtil.addErrorMessage("You must be logged in.");
            return null;
        }
        if (cartItem == null || cartItem.getItem() == null) {
            JsfUtil.addErrorMessage("Invalid cart item.");
            return null;
        }

        try {
            cartItem.setPurchased(true);
            cartItem.setRejected(false);
            ejbFacade.edit(cartItem);
            Item item = cartItem.getItem();
            item.setStatus("PENDING");
            itemFacade.edit(item);

            JsfUtil.addSuccessMessage("Purchase submitted! Awaiting seller approval.");
            recreateCartItemList();
            recreateHistory();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Failed to purchase item.");
        }
        return null;
    }

    public String purchaseAll() {
        if (loginBean == null || !loginBean.isLoggedIn()) {
            JsfUtil.addErrorMessage("You must be logged in.");
            return null;
        }

        List<CartItem> cart = getCartItemList();
        if (cart == null || cart.isEmpty()) {
            JsfUtil.addErrorMessage("Your cart is empty.");
            return null;
        }

        int success = 0;
        int skipped = 0;
        for (CartItem ci : cart) {
            try {
                ci.setPurchased(true);
                ci.setRejected(false);
                ejbFacade.edit(ci);
                Item item = ci.getItem();
                item.setStatus("PENDING");
                itemFacade.edit(item);
                success++;
            } catch (Exception e) {
                skipped++;
            }
        }

        recreateCartItemList();
        recreateHistory();

        if (skipped == 0) {
            JsfUtil.addSuccessMessage(success + " item(s) submitted for seller approval!");
        } else {
            JsfUtil.addSuccessMessage(success + " item(s) submitted. " + skipped + " item(s) skipped due to errors.");
        }
        return null;
    }

    // ----- Standard CRUD helpers (for admin pages) -----

    public String prepareList() {
        return "/cartItem/List.xhtml?faces-redirect=true";
    }

    public String prepareView() {
        current = (CartItem) getHistoryItems().getRowData();
        return "/cartItem/View.xhtml?faces-redirect=true";
    }

    public String prepareCreate() {
        current = new CartItem();
        current.setCartItemPK(new entity.CartItemPK());
        current.setPurchased(false);
        selectedItemIndex = -1;
        return "/cartItem/Create.xhtml?faces-redirect=true";
    }

    public String create() {
        try {
            current.getCartItemPK().setItemId(current.getItem().getId());
            current.getCartItemPK().setUserId(current.getAppUser().getId());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CartItemCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (CartItem) getHistoryItems().getRowData();
        return "/cartItem/Edit.xhtml?faces-redirect=true";
    }

    public String update() {
        try {
            current.getCartItemPK().setItemId(current.getItem().getId());
            current.getCartItemPK().setUserId(current.getAppUser().getId());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CartItemUpdated"));
            return "/cartItem/View.xhtml?faces-redirect=true";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (CartItem) getHistoryItems().getRowData();
        performDestroy();
        recreateHistory();
        return "/cartItem/List.xhtml?faces-redirect=true";
    }

    public String destroyAndView() {
        performDestroy();
        recreateHistory();
        return "/cartItem/List.xhtml?faces-redirect=true";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CartItemDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public CartItem getCartItem(entity.CartItemPK id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = CartItem.class)
    public static class CartItemControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CartItemController controller = (CartItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cartItemController");
            return controller.getCartItem(getKey(value));
        }

        entity.CartItemPK getKey(String value) {
            entity.CartItemPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entity.CartItemPK();
            key.setUserId(Integer.parseInt(values[0]));
            key.setItemId(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(entity.CartItemPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getUserId());
            sb.append(SEPARATOR);
            sb.append(value.getItemId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof CartItem) {
                CartItem o = (CartItem) object;
                return getStringKey(o.getCartItemPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CartItem.class.getName());
            }
        }

    }

}
