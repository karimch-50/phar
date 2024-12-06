package com.labo.fs.views.location;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.labo.fs.data.entity.Location;
import com.labo.fs.data.enums.LocationStatus;
import com.labo.fs.data.service.LocationService;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.util.Tools;
import com.labo.fs.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Location")
@Route(value = "location-form", layout = MainLayout.class)
@RouteAlias(value = "location-form/:locationID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"SUPERADMIN", "USER", "ADMIN"})
@Uses(Icon.class)
public class LocationFormView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final String LOCATION_FORM_ID = "locationID";
    private final String LOCATION_FORM_EDIT_ROUTE_TEMPLATE = "location-form/%s/edit";
    private final LocationService locationService;
    private final AuthenticatedUser authenticatedUser;
    private Tools tools  = new Tools();

    private BeanValidationBinder<Location> binder;

    private TextField name;
    private ComboBox<LocationStatus> status;
    private TextField address;
    private Checkbox active;
    
    private Location location;
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> LocationId = event.getRouteParameters().get(LOCATION_FORM_ID).map(UUID::fromString);
        if (LocationId.isPresent()) {
            Optional<Location> locationFromBackend = locationService.get(LocationId.get());
            if (locationFromBackend.isPresent()) {
                populateForm(locationFromBackend.get());
                location = locationFromBackend.get();
            } else {
                Notification.show(
                        String.format("The requested location was not found, ID = %s", LocationId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                event.forwardTo(LocationListView.class);
            }
        }
    }

    VerticalLayout layoutColumn2 = new VerticalLayout();

    @Autowired
    public LocationFormView(LocationService locationService,
            AuthenticatedUser authenticatedUser) {
        this.locationService = locationService;
        this.authenticatedUser = authenticatedUser;

        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        Button saveButton = new Button();
        Button cancelButton = new Button();
        Button goBackButton = new Button();

        addClassNames("ecoleedit-world-view");

        name = new TextField("Nom");
        
        status = new ComboBox<>("Status");
        status.setItems(LocationStatus.values());
        status.setItemLabelGenerator(LocationStatus::getLabel);
        status.setRequired(true);

        address = new TextField("Address");
        active = new Checkbox("Active ?");


        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("1000px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Informations sur la localisation");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");

        getContent().add(goBackButton);
        goBackButton.addClassName("btn-go-back");
        goBackButton.setText("<");
        goBackButton.getStyle().set("align-self", "flex-start");


        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        saveButton.setText("Enregistrer");
        saveButton.setWidth("min-content");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelButton.setText("Réinitialiser");
        cancelButton.setWidth("min-content");

        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(name);
        formLayout2Col.add(status);
        formLayout2Col.add(address);
        formLayout2Col.add(active);

        active.setValue(true);

        formLayout2Col.setResponsiveSteps(
            new ResponsiveStep("0", 1),
            new ResponsiveStep("500px", 2));

        layoutColumn2.add(layoutRow);

        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        layoutRow2.add(saveButton);
        layoutRow2.add(cancelButton);
        layoutRow.add(layoutRow2);
        binder = new BeanValidationBinder<>(Location.class);
        binder.forField(name).asRequired("Name is required").bind("name");
        binder.forField(status).asRequired("Status is required").bind("status");
        binder.forField(address).asRequired("Address is required").bind("address");
        binder.forField(active).bind("active");

        
        goBackButton.addClickListener(e -> UI.getCurrent().getPage().getHistory().back());


        cancelButton.addClickListener(e -> {
            clearForm();
        });

        saveButton.addClickListener(event -> {
            try {
                if (location == null) {
                    location = new Location();
                    location.setCreatedBy(authenticatedUser.get().get().getName());
                }
                location.setUpdatedBy(authenticatedUser.get().get().getName());
                location.setUpdated(new Date());

                binder.writeBean(location);
                locationService.update(location);
                clearForm();
                UI.getCurrent().navigate(LocationListView.class);

            } catch (ValidationException e) {
                e.printStackTrace();
            }

        });
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Location value) {
        this.location = value;
        binder.readBean(this.location);
    }
}

