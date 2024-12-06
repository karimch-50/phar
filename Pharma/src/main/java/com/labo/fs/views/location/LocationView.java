package com.labo.fs.views.location;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.labo.fs.data.User;
import com.labo.fs.data.dto.location.LocationDTO;
import com.labo.fs.data.entity.Location;
import com.labo.fs.data.enums.LocationType;
import com.labo.fs.data.service.LocationService;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.services.UserService;
import com.labo.fs.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@PageTitle("Liste des localisations")
@Route(value = "locations", layout = MainLayout.class)
@RolesAllowed({"SUPERSUPERADMIN", "SUPERADMIN", "ADMIN"})
public class LocationView extends Composite<VerticalLayout> {
    private final String LOCATION_EDIT_ROUTE_TEMPLATE = "location-form/%s/edit";
    private final LocationService locationService;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private TreeGrid<LocationDTO> grid;
    TreeData<LocationDTO> treeData;

    private User currentUser;
    private Filters filters;

    public LocationView(AuthenticatedUser authenticatedUser, UserService userService
            , LocationService locationService) {
        this.locationService = locationService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;

        treeData = new TreeData<>();
        addClassNames("gridwith-filters-view");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        getContent().setWidth("100%");
        getContent().setHeight("100%");

        filters = new Filters(this, () -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName("withShadow");

        getContent().add(layout);
        getContent().add(createGrid());
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<Location> {
        private final TextField name = new TextField("Nom");
        private final LocationView locationView;

        public Filters(LocationView locationView, Runnable onSearch) {
            this.locationView = locationView;

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("Nom");

            // Action buttons
            Button resetBtn = new Button(new Icon(VaadinIcon.REFRESH));
            resetBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Rechercher");
            searchBtn.setIcon(new Icon(VaadinIcon.SEARCH));
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Button create = new Button("Ajouter l'etage");
            create.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            create.setIcon(VaadinIcon.PLUS.create());
            create.addClickListener(e -> {
                locationView.createDialog(null, "Ajouter Etage", LocationType.ETAGE);
            });

            Div actions = new Div(resetBtn, searchBtn, create);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, actions);
        }

        @Override
        public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(name));
            }

            query.orderBy(criteriaBuilder.desc(root.get("created")));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new TreeGrid<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        List<LocationDTO> locations = locationService.findLocationDTO();
        
        // Build hierarchical data
        addLocationsToTreeData(treeData, null, locations);

        TreeDataProvider<LocationDTO> dataProvider = new TreeDataProvider<>(treeData);

        // Set data provider and columns
        grid.setDataProvider(dataProvider);
        grid.addHierarchyColumn(LocationDTO::getName).setHeader("Location Name");
        // grid.addColumn(LocationDTO::getStatus).setHeader("Status");
        grid.addColumn(locationDTO -> locationDTO.getType().getLabel()).setHeader("Type");

        // Add action column with buttons
        grid.addColumn(new ComponentRenderer<>(location -> createActionButtons(location, treeData, dataProvider)))
            .setHeader("Actions");

        return grid;
    }

    private Component createActionButtons(LocationDTO location, TreeData<LocationDTO> treeData, TreeDataProvider<LocationDTO> dataProvider) {
        HorizontalLayout layout = new HorizontalLayout();

        if (location.getType() == LocationType.ETAGE) {
            Button addLabelButton = new Button("Ajouter Label", event -> {
                // LocationDTO newLabel = new LocationDTO();
                // newLabel.setName("New Label");
                // newLabel.setType(LocationType.LABEL);
                // newLabel.setStatus(LocationStatus.AVAILABLE);

                // location.getChildren().add(newLabel);
                // treeData.addItem(location, newLabel);
                // dataProvider.refreshAll();
                createDialog(location, "Ajouter Label", LocationType.LABEL);
            });
            layout.add(addLabelButton);
        }

        if (location.getType() == LocationType.LABEL) {
            Button addSalleButton = new Button("Ajouter Salle", event -> {
                // LocationDTO newSalle = new LocationDTO();
                // newSalle.setName("New Salle");
                // newSalle.setType(LocationType.SALLE);
                // newSalle.setStatus(LocationStatus.AVAILABLE);

                // location.getChildren().add(newSalle);
                // treeData.addItem(location, newSalle);
                // dataProvider.refreshAll();
                createDialog(location, "Ajouter Salle", LocationType.SALLE);

            });
            layout.add(addSalleButton);
        }

        return layout;
    }

    private void addLocationsToTreeData(TreeData<LocationDTO> treeData, LocationDTO parent, List<LocationDTO> locations) {
        treeData.addItems(parent, locations);
        for (LocationDTO location : locations) {
            addLocationsToTreeData(treeData, location, location.getChildren());
        }
    }

    private void createDialog(LocationDTO parent, String title, LocationType locationType) {
        Dialog dialog = new Dialog();
        dialog.setWidth("60%");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        H2 headline = new H2(title);

        TextField nameField = new TextField("Nom");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");
        formLayout.add(nameField);

        Button cancelButton = new Button("Annuler", e -> dialog.close());
        Button saveButton = new Button("Enregistrer");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> {
            if (nameField.getValue() == null) {
                Notification.show("Remplissez le champ nom");
                return;
            }
            Location location = new Location();
            location.setName(nameField.getValue());
            location.setType(locationType);
            location.setParent(parent == null ? null : locationService.get(parent.getId()).orElse(null));
            // location.setStatus(LocationStatus.AVAILABLE);

            locationService.update(location);

            if (parent != null)
                parent.getChildren().add(new LocationDTO(location));
            treeData.addItem(parent, new LocationDTO(location));
            refreshGrid();
            dialog.close();
        });

        dialog.add(new VerticalLayout(headline, formLayout, new HorizontalLayout(saveButton, cancelButton)));
        dialog.open();     
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

}
