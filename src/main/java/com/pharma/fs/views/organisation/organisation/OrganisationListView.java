package com.pharma.fs.views.organisation.organisation;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.pharma.fs.data.User;
import com.pharma.fs.data.entity.Organisation;
import com.pharma.fs.data.entity.OrganisationType;
import com.pharma.fs.data.service.OrganisationService;
import com.pharma.fs.data.service.OrganisationTypeService;
import com.pharma.fs.security.AuthenticatedUser;
import com.pharma.fs.services.UserService;
import com.pharma.fs.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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

@PageTitle("Liste des organisations")
@Route(value = "organisation-list", layout = MainLayout.class)
@RolesAllowed({"SUPERSUPERADMIN", "SUPERADMIN", "ADMIN"})
public class OrganisationListView extends Composite<VerticalLayout> {
    private final String ORGANISATION_EDIT_ROUTE_TEMPLATE = "organisation-form/%s/edit";
    private final OrganisationService organisationService;
    private final OrganisationTypeService organisationTypeService;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private Grid<Organisation> grid;
    private User currentUser;
    private Filters filters;

    public OrganisationListView(AuthenticatedUser authenticatedUser, UserService userService
            , OrganisationService organisationService, OrganisationTypeService organisationTypeService) {
        this.organisationService = organisationService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.organisationTypeService = organisationTypeService;

        addClassNames("gridwith-filters-view");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        getContent().setWidth("100%");
        getContent().setHeight("100%");

        filters = new Filters(() -> refreshGrid(), organisationTypeService);
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

    public static class Filters extends Div implements Specification<Organisation> {
        private final TextField name = new TextField("Nom");
        private final TextField code = new TextField("Code");
        private final TextField ville = new TextField("Ville");
        private final MultiSelectComboBox<OrganisationType> type = new MultiSelectComboBox<>("Type d'organisation");
        public Filters(Runnable onSearch, OrganisationTypeService orgnisationTypeService) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("Nom");

            code.setPlaceholder("Code");
            ville.setPlaceholder("Ville");
            type.setPlaceholder("Type d'organisation");
            type.setItems(orgnisationTypeService.getOrganisationTypes());
            type.setItemLabelGenerator(OrganisationType::getCode);


            // Action buttons
            Button resetBtn = new Button(new Icon(VaadinIcon.REFRESH));
            resetBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                code.clear();
                ville.clear();
                type.clear();
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
                UI.getCurrent().navigate(OrganisationFormView.class);
            });

            Div actions = new Div(resetBtn, searchBtn, create);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, code, ville, type, actions);
        }

        @Override
        public Predicate toPredicate(Root<Organisation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate name = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(name));
            }
            if (!code.isEmpty()) {
                String lowerCaseFilter = code.getValue().toLowerCase();
                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(code));
            }
            if (!ville.isEmpty()) {
                String lowerCaseFilter = ville.getValue().toLowerCase();
                Predicate ville = criteriaBuilder.like(criteriaBuilder.lower(root.get("ville")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(ville));
            }
            if (!type.isEmpty()) {
                List<OrganisationType> types = type.getSelectedItems().stream().toList();
                Predicate type = root.get("organisationType").in(types);
                predicates.add(type);
            }

            query.orderBy(criteriaBuilder.desc(root.get("created")));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(Organisation.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addClassName("withShadow");

        grid.addColumn("name").setHeader("Name").setAutoWidth(true);
        grid.addColumn("ville").setHeader("Ville").setAutoWidth(true);
        grid.addColumn("address").setHeader("Address").setAutoWidth(true);
        grid.addColumn("longitude").setHeader("Longitude").setAutoWidth(true);
        grid.addColumn("latitude").setHeader("Latitude").setAutoWidth(true);
        grid.addColumn("active").setHeader("Active").setAutoWidth(true);
        grid.addColumn("created").setHeader("Date creation").setAutoWidth(true);

        grid.addComponentColumn(organisation -> {
            Button edit = new Button("Modifier", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> UI.getCurrent().navigate(String.format(ORGANISATION_EDIT_ROUTE_TEMPLATE, organisation.getId())));

            HorizontalLayout actions = new HorizontalLayout(edit);
            actions.setAlignItems(FlexComponent.Alignment.END);
            return actions;

        }).setHeader("Actions").setAutoWidth(true);

        grid.setItems(query -> organisationService.list(
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
