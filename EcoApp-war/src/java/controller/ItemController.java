package controller;

import entity.Item;
import controller.util.JsfUtil;
import controller.util.PaginationHelper;
import session.ItemFacade;

import java.io.Serializable;
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

@Named("itemController")
@SessionScoped
public class ItemController implements Serializable {

    private Item current;
    private DataModel items = null;
    @EJB
    private session.ItemFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    @Inject
    private LoginController loginBean;

    private boolean myItemsMode;

    public ItemController() {
    }

    public Item getSelected() {
        if (current == null) {
            current = new Item();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ItemFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            if (myItemsMode && loginBean != null && loginBean.isLoggedIn()) {
                Integer sellerId = loginBean.getLoggedInUser().getId();
                pagination = new PaginationHelper(10) {
                    @Override
                    public int getItemsCount() {
                        return getFacade().countBySellerId(sellerId);
                    }
                    @Override
                    public DataModel createPageDataModel() {
                        return new ListDataModel(getFacade().findBySellerId(sellerId, getPageFirstItem(), getPageSize()));
                    }
                };
            } else {
                pagination = new PaginationHelper(10) {
                    @Override
                    public int getItemsCount() {
                        return getFacade().countAvailable();
                    }
                    @Override
                    public DataModel createPageDataModel() {
                        return new ListDataModel(getFacade().findAvailable(getPageFirstItem(), getPageSize()));
                    }
                };
            }
        }
        return pagination;
    }

    public String prepareList() {
        myItemsMode = false;
        recreateModel();
        return "List";
    }

    public String prepareMyItems() {
        myItemsMode = true;
        recreateModel();
        recreatePagination();
        return "MyList";
    }

    public String prepareView() {
        current = (Item) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Item();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            if (current.getSellerId() == null && loginBean != null && loginBean.isLoggedIn() && !loginBean.isAdmin()) {
                current.setSellerId(loginBean.getLoggedInUser());
            }
            if (current.getStatus() == null) {
                current.setStatus("AVAILABLE");
            }
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ItemCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Item) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ItemUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Item) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ItemDeleted"));
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

    public Item getItem(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    public List<Item> getRecentItems() {
        return getFacade().findRecent(6);
    }

    public void resetListMode() {
        myItemsMode = false;
        recreateModel();
        recreatePagination();
    }

    public void enterMyItemsMode() {
        myItemsMode = true;
        recreateModel();
        recreatePagination();
    }

    public boolean isMyItemsMode() {
        return myItemsMode;
    }

    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getItem(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
