package com.pharma.fs.views.location;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.pharma.fs.data.User;
import com.pharma.fs.data.entity.Location;
import com.pharma.fs.data.service.LocationService;
import com.pharma.fs.security.AuthenticatedUser;
import com.pharma.fs.services.UserService;
import com.pharma.fs.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@PageTitle("Liste des localisations")
@Route(value = "location-list", layout = MainLayout.class)
@RolesAllowed({"SUPERSUPERADMIN", "SUPERADMIN", "ADMIN"})
public class LocationListView extends Composite<VerticalLayout> {
    private final String LOCATION_EDIT_ROUTE_TEMPLATE = "location-form/%s/edit";
    private final LocationService locationService;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private Grid<Location> grid;
    private User currentUser;
    private Filters filters;

    public LocationListView(AuthenticatedUser authenticatedUser, UserService userService
            , LocationService locationService) {
        this.locationService = locationService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;

        addClassNames("gridwith-filters-view");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        getContent().setWidth("100%");
        getContent().setHeight("100%");

        filters = new Filters(() -> refreshGrid());
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

        public Filters(Runnable onSearch) {

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

            Button create = new Button("Ajouter");
            create.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            create.setIcon(VaadinIcon.PLUS.create());
            create.addClickListener(e -> {
                UI.getCurrent().navigate(LocationFormView.class);
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
        grid = new Grid<>(Location.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addClassName("withShadow");

        grid.addColumn("name").setHeader("Name").setAutoWidth(true);
        grid.addColumn("address").setHeader("Address").setAutoWidth(true);
        grid.addColumn(location -> location.getStatus() != null ? location.getStatus().getLabel() : "N/A").setHeader("Status").setAutoWidth(true);
        grid.addColumn("active").setHeader("Active").setAutoWidth(true);

        grid.addComponentColumn(location -> {
            Button edit = new Button("Modifier", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> UI.getCurrent().navigate(String.format(LOCATION_EDIT_ROUTE_TEMPLATE, location.getId())));

            HorizontalLayout actions = new HorizontalLayout(edit);
            actions.setAlignItems(FlexComponent.Alignment.END);
            return actions;

        }).setHeader("Actions").setAutoWidth(true);
        grid.setItems(query -> locationService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

}
