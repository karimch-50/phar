package com.labo.fs.views.dashboard;


import com.labo.fs.data.entity.Organisation;


import com.labo.fs.data.service.OrganisationService;

import com.labo.fs.security.AuthenticatedUser;
import com.labo.fs.views.MainLayout;

import com.storedobject.chart.BarChart;
import com.storedobject.chart.CategoryData;
import com.storedobject.chart.Data;
import com.storedobject.chart.DataType;
import com.storedobject.chart.LineChart;
import com.storedobject.chart.NightingaleRoseChart;
import com.storedobject.chart.Position;
import com.storedobject.chart.RectangularCoordinate;
import com.storedobject.chart.SOChart;
import com.storedobject.chart.Size;
import com.storedobject.chart.Title;
import com.storedobject.chart.Toolbox;
import com.storedobject.chart.TreeChart;
import com.storedobject.chart.TreeData;
import com.storedobject.chart.XAxis;
import com.storedobject.chart.YAxis;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

// @PageTitle("Dashboard")
// @Route(value = "", layout = MainLayout.class)
// //@PermitAll
// @AnonymousAllowed
// @Uses(Icon.class)
@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class DashboardView extends Composite<VerticalLayout> {


    @Autowired
    public DashboardView() {

    // Creating a chart display area.
    SOChart soChart = new SOChart();
    soChart.setSize("800px", "500px");

    // Let us define some inline data.
    CategoryData labels = new CategoryData("Banana", "Apple", "Orange", "Grapes");
    Data data = new Data(25, 40, 20, 30);

    // We are going to create a couple of charts. So, each chart should be positioned
    // appropriately.
    // Create a self-positioning chart.
    NightingaleRoseChart nc = new NightingaleRoseChart(labels, data);
    Position p = new Position();
    p.setTop(Size.percentage(50));
    nc.setPosition(p); // Position it leaving 50% space at the top

    // Second chart to add.
    BarChart bc = new BarChart(labels, data);
    RectangularCoordinate rc;
    rc  = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
    p = new Position();
    p.setBottom(Size.percentage(55));
    rc.setPosition(p); // Position it leaving 55% space at the bottom
    bc.plotOn(rc); // Bar chart needs to be plotted on a coordinate system

    // Just to demonstrate it, we are creating a "Download" and a "Zoom" toolbox button.
    Toolbox toolbox = new Toolbox();
    toolbox.addButton(new Toolbox.Download(), new Toolbox.Zoom());

    // Let's add some titles.
    Title title = new Title("My First Chart");
    title.setSubtext("2nd Line of the Title");

    // Add the chart components to the chart display area.
    soChart.add(bc, toolbox, title);

    // Now, add the chart display (which is a Vaadin Component) to your layout.
    // getContent().add(soChart);
    }


}
