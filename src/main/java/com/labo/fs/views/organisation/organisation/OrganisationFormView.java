package com.labo.fs.views.organisation.organisation;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.labo.fs.data.entity.Location;
import com.labo.fs.data.entity.Organisation;
import com.labo.fs.data.entity.OrganisationType;
import com.labo.fs.data.service.LocationService;
import com.labo.fs.data.service.OrganisationService;
import com.labo.fs.data.service.OrganisationTypeService;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.util.Tools;
import com.labo.fs.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Organisation")
@Route(value = "organisation-form", layout = MainLayout.class)
@RouteAlias(value = "organisation-form/:organisationID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"SUPERADMIN", "USER", "ADMIN"})
@Uses(Icon.class)
public class OrganisationFormView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final String ORGANISATION_FORM_ID = "organisationID";
    private final String ORGANISATION_FORM_EDIT_ROUTE_TEMPLATE = "organisation-form/%s/edit";
    private final OrganisationService organisationService;
    private final OrganisationTypeService organisationTypeService;
    private final AuthenticatedUser authenticatedUser;
    private final LocationService locationService;
    private Tools tools = new Tools();

    private BeanValidationBinder<Organisation> binder;

    private TextField name;
    private TextField ville;
    private TextField code;
    private TextField address;
    private TextArea description;
    private NumberField longitude;
    private NumberField latitude;
    private Checkbox active;
    private MultiSelectComboBox<Location> locations;
    private Upload file;
    private Image filePreview;
    TextField timingInfo;

    Select<OrganisationType> organisationType;
    
    private Organisation organisation;
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> OrganisationId = event.getRouteParameters().get(ORGANISATION_FORM_ID).map(UUID::fromString);
        if (OrganisationId.isPresent()) {
            Optional<Organisation> organisationFromBackend = organisationService.get(OrganisationId.get());
            if (organisationFromBackend.isPresent()) {
                populateForm(organisationFromBackend.get());
                organisation = organisationFromBackend.get();
            } else {
                Notification.show(
                        String.format("The requested location was not found, ID = %s", OrganisationId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                event.forwardTo(OrganisationListView.class);
            }
        }
    }

    VerticalLayout layoutColumn2 = new VerticalLayout();

    @Autowired
    public OrganisationFormView(OrganisationService organisationService,
                OrganisationTypeService organisationTypeService,
                LocationService locationService,
                AuthenticatedUser authenticatedUser) {
        this.organisationService = organisationService;
        this.organisationTypeService = organisationTypeService;
        this.locationService = locationService;
        this.authenticatedUser = authenticatedUser;

        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        Button saveButton = new Button();
        Button cancelButton = new Button();
        Button goBackButton = new Button();
        filePreview = new Image();
        file = new Upload();

        addClassNames("ecoleedit-world-view");
        file.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        filePreview.setWidth("100%");
        file.getStyle().set("box-sizing", "border-box");
        file.getElement().appendChild(filePreview.getElement());

        name = new TextField("Name");
        ville = new TextField("Ville");
        code = new TextField("Code");
        address = new TextField("Address");
        description = new TextArea("Description");
        longitude = new NumberField("Longitude");
        latitude = new NumberField("Latitude");
        active = new Checkbox("Active ?");
        locations = new MultiSelectComboBox<>();

        locations.setLabel("Locations");
        locations.setItems(locationService.findAll());
        locations.setItemLabelGenerator(Location::getName);

        active.setValue(true);

        organisationType = new Select<>();
        organisationType.setLabel("Type d'organisation");
        organisationType.setPlaceholder("Select Organisation type");
        organisationType.setItems(organisationTypeService.getOrganisationTypes());
        organisationType.setItemLabelGenerator(OrganisationType::getCode);


        timingInfo = new TextField();
        timingInfo.setLabel("timing");
        timingInfo.setPlaceholder("7/7 - open from 8:30 to 6:00 - 24/24");
        timingInfo.setValue("24/24 - 7/7");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("1000px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Informations sur l'organisation");
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
        VerticalLayout layoutRow3 = new VerticalLayout();
        layoutRow3.setAlignItems(
          Alignment.CENTER);  
        
        layoutRow3.add(filePreview, file);
        formLayout2Col.add(layoutRow3);
        formLayout2Col.add(name);
        // formLayout2Col.add(file, filePreview);
        formLayout2Col.add(organisationType);
        formLayout2Col.add(locations);
        formLayout2Col.add(description);
        formLayout2Col.add(ville);
        formLayout2Col.add(code);
        formLayout2Col.add(address);
        formLayout2Col.add(longitude);
        formLayout2Col.add(latitude);
        formLayout2Col.add(timingInfo);
        formLayout2Col.add(active);

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
        binder = new BeanValidationBinder<>(Organisation.class);
        
        binder.forField(name).asRequired(tools.getMessageRequired()).bind("name");
        binder.forField(ville).asRequired(tools.getMessageRequired()).bind("ville");
        binder.forField(code).asRequired(tools.getMessageRequired()).bind("code");
        binder.forField(address).asRequired(tools.getMessageRequired()).bind("address");
        binder.forField(longitude).asRequired(tools.getMessageRequired()).bind("longitude");
        binder.forField(latitude).asRequired(tools.getMessageRequired()).bind("latitude");
        binder.forField(timingInfo).asRequired(tools.getMessageRequired()).bind("timing");
        binder.forField(description).asRequired(tools.getMessageRequired()).bind("description");
        binder.forField(organisationType).asRequired(tools.getMessageRequired()).bind("organisationType");
        binder.forField(active).bind("active");
        binder.forField(locations).bind("locations");

        
        goBackButton.addClickListener(e -> UI.getCurrent().getPage().getHistory().back());

        cancelButton.addClickListener(e -> {
            clearForm();
        });

        saveButton.addClickListener(event -> {
            try {
                if (organisation == null) {
                    organisation = new Organisation();
                    organisation.setCreatedBy(authenticatedUser.get().get().getName());
                }
                organisation.setUpdatedBy(authenticatedUser.get().get().getName());
                organisation.setUpdated(new Date());

                binder.writeBean(organisation);
                
                try {
                    String dataUrl = filePreview.getSrc();
                    if (dataUrl != null && !dataUrl.isEmpty() && dataUrl.startsWith("data:image"))
                    {
                        byte[] imageData = Base64.getDecoder().decode(dataUrl.split(",")[1]);
                        this.organisation.setPhoto(imageData);
                    }
                } catch (Exception e1) {
                    System.out.println("Error setting file");
                }
                // this.organisation.setLocations(locations.getSelectedItems());
                organisationService.update(organisation);
                clearForm();
                UI.getCurrent().navigate(OrganisationListView.class);

            } catch (ValidationException e) {
                e.printStackTrace();
            }

        });

        file.addFileRejectedListener(event -> {
            String errorMessage = "Erreur de chargement de l'image";
            Notification notification = Notification.show(
                    errorMessage,
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        file.addSucceededListener(event -> {
            String errorMessage = event.getFileName() + " Chargé avec succès";
            Notification notification = Notification.show(
                    errorMessage,
                    2000,
                    Notification.Position.MIDDLE);
        });
        attachImageUpload(file, filePreview);

    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Organisation value) {
        this.organisation = value;
        binder.readBean(this.organisation);
    }

    public void attachImageUpload(Upload picture, Image preview) {
        ByteArrayOutputStream pictureBuffer = new ByteArrayOutputStream();
        MemoryBuffer buffer = new MemoryBuffer();
        picture.setAcceptedFileTypes("image/*");
        picture.setReceiver(buffer);

        picture.addSucceededListener(event -> {
            try {
                String mimeType = event.getMIMEType();
                pictureBuffer.write(buffer.getInputStream().readAllBytes());
                String base64ImageData = Base64.getEncoder().encodeToString(pictureBuffer.toByteArray());
                String dataUrl = "data:" + mimeType + ";base64," + base64ImageData;
        
                // Set the preview image source to the data URL
                preview.setSrc(dataUrl);
                // preview.setVisible(false);
        
                // Reset the buffer for the next upload
                pictureBuffer.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // preview.setVisible(true);
    }
}

