package controller;

import controller.util.JsfUtil;
import entity.AppUser;
import session.AppUserFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.IOException;

@Named("loginBean")
@SessionScoped
public class LoginController implements Serializable {

    private String username;
    private String password;
    private AppUser loggedInUser;

    @EJB
    private AppUserFacade appUserFacade;

    public String login() {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JsfUtil.addErrorMessage("Username and password are required.");
            return null;
        }

        AppUser user = appUserFacade.findByUsername(username.trim());
        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            JsfUtil.addErrorMessage("Invalid username or password.");
            return null;
        }

        loggedInUser = user;
        return "/public.xhtml?faces-redirect=true";
    }

    public String logout() {
        loggedInUser = null;
        username = null;
        password = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public boolean isAdmin() {
        if (loggedInUser == null || loggedInUser.getRoleId() == null) {
            return false;
        }
        String roleName = loggedInUser.getRoleId().getRoleName();
        return roleName != null && roleName.equals("ADMIN");
    }

    public void requireLogin() {
        if (!isLoggedIn()) {
            redirectTo("/login.xhtml");
        }
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            redirectTo("/index.xhtml");
        }
    }

    private void redirectTo(String viewPath) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null || context.getExternalContext() == null) {
            return;
        }
        String contextPath = context.getExternalContext().getRequestContextPath();
        try {
            context.getExternalContext().redirect(contextPath + "/faces" + viewPath);
            context.responseComplete();
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Unable to redirect.");
        }
    }

    public AppUser getLoggedInUser() {
        return loggedInUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
