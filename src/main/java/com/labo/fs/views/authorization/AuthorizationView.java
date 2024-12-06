package com.labo.fs.views.authorization;

import com.labo.fs.data.User;
import com.labo.fs.data.entity.Organisation;
import com.labo.fs.data.enums.UserRole;
import com.labo.fs.data.service.OrganisationService;
import com.labo.fs.services.UserService;
import com.labo.fs.views.MainLayout;
import com.labo.fs.ui.components.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@PageTitle("Authorisation")

@Route(value = "authorization", layout = MainLayout.class)
@RolesAllowed({"SUPERADMIN"})
public class AuthorizationView extends Composite<VerticalLayout> {
    private final OrganisationService sampleOrganisationService;
    private final UserService userService;
    private final OrganisationService organisationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthorizationView(OrganisationService sampleOrganisationService,
            UserService userService, OrganisationService organisationService) {
        this.sampleOrganisationService = sampleOrganisationService;
        this.userService = userService;
        this.organisationService = organisationService;

        H2 title = new H2("Authorisation d'utilisateur");

        title.setWidth("100%");
        title.getStyle().set("text-align", "center");

        // Add a button to add a new user
        Button addUser = new Button("Ajouter un utilisateur");
        addUser.getStyle().set("margin-right", "2em");
        addUser.getStyle().set("color", "white");
        addUser.getStyle().set("background-color", "blue");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull(); // Make the layout take the full width
        layout.setJustifyContentMode(JustifyContentMode.END); // Align items to the right
        layout.add(addUser);
        // create a grid to list all users
        Grid<User> usergrid = new Grid<>();
        usergrid.getStyle().set("margin-top", "1em");
        usergrid.setItems(userService.All());
        addUser.addClickListener(e -> {
            // Add a new user
            User user = new User();
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            dialog.setWidth("500px");
            dialog.setHeight("400px");
            dialog.add(new EditForm(user, userService, organisationService, passwordEncoder, dialog));
            dialog.open();
            dialog.addDialogCloseActionListener(event -> {
                usergrid.setItems(userService.All());
                // style the notification

                Notification notification = new Notification("Utilisateur ajouté avec succès.", 3000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            });

        });

        // search here
        // usergrid.addColumn(new ComponentRenderer<>(user -> {
        // byte[] profilePicture = user.getProfilePicture();
        // String imageString;
        // if (profilePicture != null) {
        // imageString = "data:image/png;base64," +
        // Base64.getEncoder().encodeToString(profilePicture);
        // } else {
        // imageString = "data:image/png;base64,"; // add your placeholder image here
        // }
        // Image image = new Image(imageString, "Profile Picture");
        // image.setHeight("3em");
        // image.getStyle().set("border-radius", "50%");
        // return image;
        // })).setHeader("Profile Picture");
        // add button to grid
        usergrid.addColumn(User::getUsername).setHeader("Username");
        //usergrid.addColumn(User::getUpdated).setHeader("Created Date");
        usergrid.addColumn(user -> user.getOrganisations().stream().map(o -> o.getName()).toList())
                .setHeader("Organisations");

        usergrid.addColumn(user -> user.getRoles()).setHeader("Roles");
        //

        // usergrid.addComponentColumn(user -> {
        // Button button = new Button("Modifier");
        // button.getStyle().set("color", "white");
        // button.getStyle().set("background-color", "blue");
        // button.addClickListener(e -> {
        // permissionsPopup(user);
        // });
        // return button;
        // }).setHeader("Permissions");
        usergrid.addItemClickListener(event -> {
            User selectedUser = event.getItem();
            Dialog d = new Dialog();
            d.setCloseOnEsc(false);
            d.setCloseOnOutsideClick(false);
            d.setWidth("500px");
            d.setHeight("400px");
            d.add(new EditForm(selectedUser, userService, organisationService, passwordEncoder, d));
            d.open();
            d.addDialogCloseActionListener(e -> {
                usergrid.setItems(userService.All());
                // style the notification

                Notification notification = new Notification("Utilisateur ajouté avec succès.", 3000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            });
            // User loggedInUser = userService.getLoggedInUser();
            // try {
            // final Authentication authentication =
            // SecurityContextHolder.getContext().getAuthentication();
            // if (authentication == null) {
            // throw new IllegalArgumentException("No user logged in");
            // }
            // User user = userService.getByUsername(authentication.getName()).orElse(null);
            // User selectedUser = event.getItem();

            // if (user != selectedUser) {
            // organisationsInfo(user, selectedUser);
            // }

            // } catch (Exception e) {
            // System.out.println("Error: " + e.getMessage());
            // }
            // System.out.println("User: ----------->" + user.getUsername());
        });
        getContent().add(title, layout, usergrid);
    }

    public List<UserRole> getRolesUnder(UserRole userRole) {
        List<UserRole> roles = new ArrayList<>();
        roles = switch (userRole) {
            case SUPER_ADMIN -> Arrays.asList(UserRole.ADMIN, UserRole.SCHEDULER, UserRole.USER, UserRole.DEFAULT);
            case ADMIN -> Arrays.asList(UserRole.SCHEDULER, UserRole.USER, UserRole.DEFAULT);
            case SCHEDULER -> Arrays.asList(UserRole.USER, UserRole.DEFAULT);
            case USER -> Arrays.asList(UserRole.DEFAULT);
            default -> Collections.singletonList(UserRole.DEFAULT);
        };
        return roles;
    }

    public void showErrorMessage(String message) {
        Notification.show(message, 3000, Notification.Position.BOTTOM_CENTER);
    }

    private Avatar getOrgAvatar(Organisation organisation) {
        Avatar avatar = new Avatar();
        avatar.addClassNames(LumoStyles.BorderRadius._50);

        if (organisation.getPhoto() != null) {
            byte[] imageBytes = organisation.getPhoto();
            String base64ImageData = Base64.getEncoder().encodeToString(imageBytes);
            String dataUrl = "data:image/jpeg;base64," + base64ImageData; // Assuming JPEG format
            avatar.setImage(dataUrl);
        }
        return avatar;
    }


}