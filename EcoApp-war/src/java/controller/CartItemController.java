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
    private PaginationHelper pagination;
    private int selectedItemIndex;

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

    public PaginationHelper getPagination() {
        if (pagination == null) {
            if (loginBean != null && loginBean.isLoggedIn()) {
                Integer userId = loginBean.getLoggedInUser().getId();
                pagination = new PaginationHelper(10) {
                    @Override
                    public int getItemsCount() {
                        return getFacade().countByUserId(userId);
                    }
                    @Override
                    public DataModel createPageDataModel() {
                        return new ListDataModel(getFacade().findByUserId(userId, getPageFirstItem(), getPageSize()));
                    }
                };
            } else {
                pagination = new PaginationHelper(10) {
                    @Override
                    public int getItemsCount() {
                        return getFacade().count();
                    }
                    @Override
                    public DataModel createPageDataModel() {
                        return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                    }
                };
            }
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (CartItem) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new CartItem();
        current.setCartItemPK(new entity.CartItemPK());
        selectedItemIndex = -1;
        return "Create";
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
        current = (CartItem) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getCartItemPK().setItemId(current.getItem().getId());
            current.getCartItemPK().setUserId(current.getAppUser().getId());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CartItemUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (CartItem) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CartItemDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            selectedItemIndex = count - 1;
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
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

    // Buy Now action
    public String buyItem(Item item) {
        if (loginBean == null || !loginBean.isLoggedIn() || loginBean.isAdmin()) {
            JsfUtil.addErrorMessage("You must be logged in as a regular user to purchase items.");
            return null;
        }
        if (!"AVAILABLE".equals(item.getStatus())) {
            JsfUtil.addErrorMessage("This item is no longer available.");
            return null;
        }
        if (item.getSellerId() != null && item.getSellerId().getId().equals(loginBean.getLoggedInUser().getId())) {
            JsfUtil.addErrorMessage("You cannot purchase your own item.");
            return null;
        }

        // Check if already purchased
        CartItemPK pk = new CartItemPK(loginBean.getLoggedInUser().getId(), item.getId());
        if (ejbFacade.find(pk) != null) {
            JsfUtil.addErrorMessage("You have already purchased this item.");
            return null;
        }

        try {
            CartItem cartItem = new CartItem();
            cartItem.setCartItemPK(pk);
            cartItem.setAppUser(loginBean.getLoggedInUser());
            cartItem.setItem(item);
            cartItem.setDateAdded(new Date());

            ejbFacade.create(cartItem);

            item.setStatus("IN_CART");
            itemFacade.edit(item);

            JsfUtil.addSuccessMessage("Item purchased successfully!");
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Purchase failed.");
            return null;
        }
    }

    // Cancel purchase - resets item status to AVAILABLE and removes cart entry
    public String cancelPurchase(CartItem cartItem) {
        if (cartItem == null || cartItem.getItem() == null) {
            JsfUtil.addErrorMessage("Invalid cart item.");
            return null;
        }

        try {
            Item item = cartItem.getItem();
            item.setStatus("AVAILABLE");
            itemFacade.edit(item);

            ejbFacade.remove(cartItem);

            JsfUtil.addSuccessMessage("Purchase cancelled. Item is now available again.");
            recreateModel();
            recreatePagination();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Failed to cancel purchase.");
            return null;
        }
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
