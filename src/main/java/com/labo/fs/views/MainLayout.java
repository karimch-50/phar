package com.labo.fs.views;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.labo.fs.data.Role;
import com.labo.fs.data.User;
import com.labo.fs.data.UserRepository;
import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.services.UserService;
import com.labo.fs.views.authorization.AuthorizationView;
import com.labo.fs.views.dashboard.DashboardView;
import com.labo.fs.views.location.LocationListView;
import com.labo.fs.views.organisation.organisation.OrganisationListView;
import com.labo.fs.views.organisation.organisationType.OrganisationTypeListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {


    private H2 viewTitle;
    private HorizontalLayout orgs;
    private Span numberOfNotifications;
    ContextMenu menu = new ContextMenu();

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    
    private UserRepository repository;
    private UserService userService;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker,
                      UserRepository repository,
                      UserService userService
                      ) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.repository = repository;
        this.userService = userService;
        

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalArgumentException("No user logged in");
        }

        User user = userService.getByUsername(authentication.getName()).orElse(null);

        setPrimarySection(Section.DRAWER);
        addDrawerContent(user);
        
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Right.AUTO);
        // viewTitle.getStyle().set("margin", "2rem");
        // add all user organizations here

        orgs = new HorizontalLayout();
        orgs.addClassName("orgs");

        // align the orgs to the right
        orgs.getStyle().set("display", "block");
        orgs.setWidth("100%");
        orgs.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        numberOfNotifications = new Span();

        Button bellBtn = new Button(VaadinIcon.BELL_O.create());
        bellBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        bellBtn.getStyle().set("font-size", "1.7rem");
        bellBtn.getElement().appendChild(numberOfNotifications.getElement());
        bellBtn.getStyle().set("margin-right", "1rem");

        menu.setOpenOnClick(true);
        menu.setTarget(bellBtn);
        addToNavbar(true, toggle, viewTitle, bellBtn);
    }

    private void addDrawerContent(User user) {
		H1 appName = new H1("SAHTY");
		Image image = new Image("/labo/images/logo.png", "SAHTY Logo");
		image.setHeight("22px");
		image.setWidth("22px");

		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		Header header = new Header(image, appName);
		Scroller scroller = new Scroller(createNavigation(user));
		addToDrawer(header, scroller, createFooter());
	}

    private SideNav createNavigation(User user) {

        // get the current user role
        SideNav nav = new SideNav();
        if (user == null) {
            return nav;
        }
        Set<Role> roles = user.getRoles();
        System.out.println("roles: " + roles);


        if (accessChecker.hasAccess(DashboardView.class))
            nav.addItem(new SideNavItem("Tableau de bord", DashboardView.class,
                    LineAwesomeIcon.TACHOMETER_ALT_SOLID.create()));    
           

        SideNavItem tools = new SideNavItem("Paramètres");
        tools.setPrefixComponent(LineAwesomeIcon.COG_SOLID.create());
        
        
        if (accessChecker.hasAccess(LocationListView.class))
            tools.addItem(new SideNavItem("Location", LocationListView.class,
                    LineAwesomeIcon.LOCATION_ARROW_SOLID.create()));
        if (accessChecker.hasAccess(OrganisationTypeListView.class))
            tools.addItem(new SideNavItem("Catégorie organisations", OrganisationTypeListView.class,
                    LineAwesomeIcon.CLINIC_MEDICAL_SOLID.create()));
        if (accessChecker.hasAccess(OrganisationListView.class))
            tools.addItem(new SideNavItem("Organisations", OrganisationListView.class,
                    LineAwesomeIcon.CLINIC_MEDICAL_SOLID.create()));
        // if (accessChecker.hasAccess(TarifView.class))
        //     tools.addItem(new SideNavItem("tarif", TarifView.class,
        //             LineAwesomeIcon.CASH_REGISTER_SOLID.create()));
        // if (accessChecker.hasAccess(ConventionView.class))
        //     tools.addItem(new SideNavItem("Convention", ConventionView.class,
        //             LineAwesomeIcon.WPFORMS.create()));
        // if (accessChecker.hasAccess(DocumentView.class))
        //     tools.addItem(new SideNavItem("Documents", DocumentView.class,
        //             LineAwesomeIcon.FILE.create()));
       

        nav.addItem(tools);
        if (accessChecker.hasAccess(AuthorizationView.class))
            nav.addItem(new SideNavItem("Authorization", AuthorizationView.class,
                    LineAwesomeIcon.LOCK_SOLID.create()));
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());

        numberOfNotifications.getElement().getThemeList().clear();
        numberOfNotifications.getElement().removeAllChildren();
        menu.removeAll();}
        
       /*  if (notifs.size() != 0)
        {
            numberOfNotifications.getElement()
                        .getThemeList()
                        .addAll(Arrays.asList("badge", "error", "primary", "small", "pill"));
            numberOfNotifications.getStyle()
                    .set("position", "absolute")
                    .set("transform", "translate(-40%, -85%)");
            numberOfNotifications.setText(notifs.size() + "");
            notifs.forEach(notification -> {
                MenuItem item = menu.addItem(notification.getTitle(), e -> {
                    notification.setSeen(true);
                    notificationService.update(notification);
                    getUI().ifPresent(ui -> ui.navigate(NotificationListView.class));
                });
                item.getElement().getStyle().set("cursor", "pointer");
            });
        }
    }*/

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
