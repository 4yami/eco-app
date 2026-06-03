package controller;

import controller.util.JsfUtil;
import entity.AppUser;
import entity.Role;
import session.AppUserFacade;
import session.RoleFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("registerBean")
@RequestScoped
public class RegisterController implements Serializable {

    private String email;
    private String username;
    private String password;
    private String phone;

    @EJB
    private AppUserFacade appUserFacade;

    @EJB
    private RoleFacade roleFacade;

    public String register() {
        if (email == null || email.trim().isEmpty()
                || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {
            JsfUtil.addErrorMessage("All fields are required.");
            return null;
        }

        if (appUserFacade.findByUsername(username.trim()) != null) {
            JsfUtil.addErrorMessage("Username already exists.");
            return null;
        }

        if (appUserFacade.findByEmail(email.trim()) != null) {
            JsfUtil.addErrorMessage("Email already exists.");
            return null;
        }

        Role userRole = roleFacade.findByRoleName("USER");
        if (userRole == null) {
            JsfUtil.addErrorMessage("User role is not configured.");
            return null;
        }

        AppUser user = new AppUser();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(password);
        user.setPhone(phone.trim());
        user.setRoleId(userRole);

        try {
            appUserFacade.create(user);
            JsfUtil.addSuccessMessage("Registration successful. Please log in.");
            return "/login.xhtml?faces-redirect=true";
        } catch (Exception ex) {
            JsfUtil.addErrorMessage(ex, "Registration failed.");
            return null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
