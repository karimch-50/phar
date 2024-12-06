package com.labo.fs.views.organisation.organisationType;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.labo.fs.data.User;
import com.labo.fs.data.entity.OrganisationType;
import com.labo.fs.data.service.OrganisationTypeService;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.services.UserService;
import com.labo.fs.views.MainLayout;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@PageTitle("Liste des types d'organisations")
@Route(value = "organisation-type-list", layout = MainLayout.class)
@RolesAllowed({"SUPERSUPERADMIN", "SUPERADMIN", "ADMIN"})
public class OrganisationTypeListView extends Composite<VerticalLayout> {
    private final String ORGANISATIONTYPE_EDIT_ROUTE_TEMPLATE = "organisationtype-form/%s/edit";
    private final OrganisationTypeService organisationTypeService;
    private final AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private Grid<OrganisationType> grid;
    private User currentUser;
    private Filters filters;

    public OrganisationTypeListView(AuthenticatedUser authenticatedUser, UserService userService
            , OrganisationTypeService organisationTypeService) {
        this.organisationTypeService = organisationTypeService;
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

    public static class Filters extends Div implements Specification<OrganisationType> {
        private final TextField code = new TextField("Code");
        private final TextField source = new TextField("Source");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            code.setPlaceholder("Code");
            source.setPlaceholder("Source");

            // Action buttons
            Button resetBtn = new Button(new Icon(VaadinIcon.REFRESH));
            resetBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            resetBtn.addClickListener(e -> {
                code.clear();
                source.clear();
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
                UI.getCurrent().navigate(OrganisationTypeFormView.class);
            });

            Div actions = new Div(resetBtn, searchBtn, create);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(code, source, actions);
        }

        @Override
        public Predicate toPredicate(Root<OrganisationType> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!code.isEmpty()) {
                String lowerCaseFilter = code.getValue().toLowerCase();
                Predicate code = criteriaBuilder.like(criteriaBuilder.lower(root.get("code")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(code));
            }
            if (!source.isEmpty()) {
                String lowerCaseFilter = source.getValue().toLowerCase();
                Predicate source = criteriaBuilder.like(criteriaBuilder.lower(root.get("source")),
                        "%" + lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(source));
            }

            query.orderBy(criteriaBuilder.desc(root.get("created")));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(OrganisationType.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.addClassName("withShadow");

        grid.addColumn("code").setAutoWidth(true).setHeader("Code");
        grid.addColumn("source").setAutoWidth(true).setHeader("Source");
        grid.addColumn("display").setAutoWidth(true).setHeader("Affichage");
        grid.addColumn("definition").setAutoWidth(true).setHeader("Definition");

        grid.addComponentColumn(orgType -> {
            Button edit = new Button("Modifier", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> UI.getCurrent().navigate(String.format(ORGANISATIONTYPE_EDIT_ROUTE_TEMPLATE, orgType.getId())));

            HorizontalLayout actions = new HorizontalLayout(edit);
            actions.setAlignItems(FlexComponent.Alignment.END);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
        grid.setItems(query -> organisationTypeService.list(
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
