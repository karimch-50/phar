package com.labo.fs.views.organisation.organisationType;


import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.labo.fs.data.entity.OrganisationType;
import com.labo.fs.data.service.OrganisationTypeService;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.util.Tools;
import com.labo.fs.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.TextArea;
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

@PageTitle("OrganisationType")
@Route(value = "organisation-type-form", layout = MainLayout.class)
@RouteAlias(value = "organisationtype-form/:organisationtypeID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed({"SUPERADMIN", "USER", "ADMIN"})
@Uses(Icon.class)
public class OrganisationTypeFormView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final String ORGANISATIONTYPE_FORM_ID = "organisationtypeID";
    private final String ORGANISATIONTYPE_FORM_EDIT_ROUTE_TEMPLATE = "organisationtype-form/%s/edit";
    private final OrganisationTypeService organisationtypeService;
    private final AuthenticatedUser authenticatedUser;
    private Tools tools  = new Tools();

    private BeanValidationBinder<OrganisationType> binder;

    private TextField code;
    private TextArea source;
    private TextField display;
    private TextField definition;
    private TextArea comment;
    
    private OrganisationType organisationtype;
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> OrganisationTypeId = event.getRouteParameters().get(ORGANISATIONTYPE_FORM_ID).map(UUID::fromString);
        if (OrganisationTypeId.isPresent()) {
            Optional<OrganisationType> organisationtypeFromBackend = organisationtypeService.get(OrganisationTypeId.get());
            if (organisationtypeFromBackend.isPresent()) {
                populateForm(organisationtypeFromBackend.get());
                organisationtype = organisationtypeFromBackend.get();
            } else {
                Notification.show(
                        String.format("The requested location was not found, ID = %s", OrganisationTypeId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                event.forwardTo(OrganisationTypeListView.class);
            }
        }
    }

    VerticalLayout layoutColumn2 = new VerticalLayout();

    @Autowired
    public OrganisationTypeFormView(OrganisationTypeService organisationtypeService,
            AuthenticatedUser authenticatedUser) {
        this.organisationtypeService = organisationtypeService;
        this.authenticatedUser = authenticatedUser;

        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        Button saveButton = new Button();
        Button cancelButton = new Button();
        Button goBackButton = new Button();

        addClassNames("ecoleedit-world-view");

        code = new TextField("Code");
        source = new TextArea("Source");
        definition = new TextField("Definition");
        comment = new TextArea("Commentaire");
        display = new TextField("Affichage");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("1000px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Informations sur le type d'organisation");
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

        formLayout2Col.add(code);
        formLayout2Col.add(display);
        formLayout2Col.add(definition);
        formLayout2Col.add(source);
        formLayout2Col.add(comment);

        formLayout2Col.setResponsiveSteps(
            new ResponsiveStep("0", 1),
            new ResponsiveStep("500px", 2));

        formLayout2Col.setColspan(comment, 2);

        layoutColumn2.add(layoutRow);

        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        layoutRow2.add(saveButton);
        layoutRow2.add(cancelButton);
        layoutRow.add(layoutRow2);
        binder = new BeanValidationBinder<>(OrganisationType.class);
        binder.forField(code).asRequired(tools.getMessageRequired()).bind("code");
        binder.forField(source).asRequired(tools.getMessageRequired()).bind("source");
        binder.forField(display).asRequired(tools.getMessageRequired()).bind("display");
        binder.forField(definition).asRequired(tools.getMessageRequired()).bind("definition");
        binder.forField(comment).bind("comment");

        goBackButton.addClickListener(e -> UI.getCurrent().getPage().getHistory().back());


        cancelButton.addClickListener(e -> {
            clearForm();
        });

        saveButton.addClickListener(event -> {
            try {
                if (organisationtype == null) {
                    organisationtype = new OrganisationType();
                    organisationtype.setCreatedBy(authenticatedUser.get().get().getName());
                }
                organisationtype.setUpdatedBy(authenticatedUser.get().get().getName());
                organisationtype.setUpdated(new Date());

                binder.writeBean(organisationtype);
                organisationtypeService.update(organisationtype);
                clearForm();
                UI.getCurrent().navigate(OrganisationTypeListView.class);

            } catch (ValidationException e) {
                e.printStackTrace();
            }

        });
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(OrganisationType value) {
        this.organisationtype = value;
        binder.readBean(this.organisationtype);
    }
}

