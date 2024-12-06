package com.labo.fs.views.authorization;

import com.labo.fs.data.Role;
import com.labo.fs.data.User;
import com.labo.fs.data.entity.Organisation;
import com.labo.fs.data.service.OrganisationService;
import com.labo.fs.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Tag("div")
public class EditForm  extends Component   {
    
    public EditForm(User user, UserService userService, OrganisationService organisationService, PasswordEncoder passwordEncoder, Dialog dialog){
        FormLayout formLayout = new FormLayout(); // Use FormLayout for arranging fields
        formLayout.setSizeFull();

        // Fields
        TextField username = new TextField("Username");
        TextField password = new TextField("Password");
        password.getElement().setAttribute("type", "password"); // Mask the password input
        TextField firstName = new TextField("Name");
        TextField lastName = new TextField("Nom");

        Button editPassword;
        HorizontalLayout nameLayout = new HorizontalLayout(username, password);
        nameLayout.setAlignItems(Alignment.BASELINE);
        // Roles MultiSelectComboBox
        MultiSelectComboBox<Role> roles = new MultiSelectComboBox<>("Roles");
        roles.setItems(Role.values());
        roles.setItemLabelGenerator(Role::name);
        roles.setPlaceholder("Select Role");
        
        // Organisations MultiSelectComboBox
        MultiSelectComboBox<Organisation> organisations = new MultiSelectComboBox<>("Organisations");
        organisations.setItems(organisationService.findAll());
        organisations.setItemLabelGenerator(Organisation::getName);
        organisations.setPlaceholder("Select Organisations");
        
        // Active Checkbox
        Checkbox active = new Checkbox("Active");
        if(user.getUsername() != null && !user.getUsername().isEmpty()){
            editPassword = new Button(new Icon(VaadinIcon.PENCIL));
            password.setEnabled(false);
            nameLayout.add(editPassword);
            editPassword.addClickListener(e -> {
                password.setEnabled(true);
            });
            username.setValue(user.getUsername());
            roles.setValue(user.getRoles());
            organisations.setValue(user.getOrganisations());
            
        }
        Button save = new Button("Save", e -> {
            user.setUsername(username.getValue());

            if (password.isEnabled() && !password.isEmpty()) {
                user.setHashedPassword(passwordEncoder.encode(password.getValue()));
            }
            
            user.setRoles(roles.getValue());
            
            //user.setActive((active.getValue()));
            user.setName(firstName.getValue() + " " + lastName.getValue());
            
            user.setOrganisations( organisations.getValue());
            try {
                userService.update(user);
            }  catch (DataIntegrityViolationException excp) {
                //Add a notification to the user
                Notification notification = new Notification("Erreur : Un utilisateur avec cet email existe déjà.", 3000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            }catch (Exception excp) {
                //Add a notification to the user
                Notification notification = new Notification("Erreur : " + excp.getMessage(), 3000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            }
            // "user_organisation_pkey"
            // reload the authorization view
            UI.getCurrent().getPage().reload();
            dialog.close();
        });
        
        // Cancel Button
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actionsLayout = new HorizontalLayout(save, cancel);
        
        // Adding components to the form layout
        
        formLayout.add(nameLayout, roles, organisations, active, actionsLayout);
        formLayout.setColspan(save, 2); // Span the save button across two columns
        formLayout.setColspan(cancel, 2); // Span the cancel button across two columns
        
        // Styling adjustments
        // make save blue
        save.getElement().getStyle().set("background-color", "blue");
        save.getElement().getStyle().set("color", "white");
        save.setWidth("40%");
        save.getStyle().set("margin", "6px");
        // make cancel red
        cancel.getElement().getStyle().set("background-color", "red");
        cancel.getElement().getStyle().set("color", "white");
        cancel.setWidth("40%");
        cancel.getStyle().set("margin", "6px");
        active.getStyle().set("margin-top", "20px");
        save.getStyle().set("margin-top", "20px");
        cancel.getStyle().set("margin-top", "20px");

        // Add the form layout to the dialog
        dialog.add(formLayout);


    }
}
