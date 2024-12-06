package com.labo.fs.util;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.StreamResource;

import lombok.Data;

import java.util.Arrays;
import java.util.Base64;

@Data
public class Tools  {

    private String  messageRequired = "Champ obligatoire";

    public static ConfirmDialog confirmationDialog(String message, String title, String confirmText) {
        ConfirmDialog dialog = new ConfirmDialog();
        Span status = new Span();
        status.setVisible(false);
        dialog.setHeader(title);
        dialog.setText(message);

        dialog.setRejectable(true);
        dialog.setRejectText("Annuler");
        dialog.addRejectListener(event -> dialog.close());

        dialog.setConfirmText(confirmText);
        return dialog;
    }

    public Component downloadFile(String fileName, byte[] file , String label, VaadinIcon vaadinIcon ) {
        StreamResource resource = new StreamResource(
                fileName, () -> new ByteArrayInputStream(file) // Stream the file data
        );
        resource.setContentType(determineMimeType(fileName));
        Button downloadButton = new Button(new Icon(vaadinIcon));
        downloadButton.setText(label);
        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().appendChild(downloadButton.getElement());
        downloadLink.getElement().setAttribute("download", true);
        return downloadLink;
    }

    private String determineMimeType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

    public DatePickerI18n getFrenchDatePicker() {
        return (new DatePickerI18n()
                .setMonthNames(Arrays.asList("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                                            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"))
                .setWeekdays(Arrays.asList("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"))
                .setWeekdaysShort(Arrays.asList("Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"))
                .setFirstDayOfWeek(1)
                .setToday("Aujourd'hui")
                .setCancel("Annuler")
            );
    }


    public VerticalLayout createDescriptionLayout (Dialog dialog, String title, String description) {
        dialog.setWidth("60%");
        dialog.setResizable(true);
        dialog.setDraggable(true);
        H3 headline = new H3(title);

        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em")
                .set("font-weight", "bold");

        TextArea descriptionField = new TextArea();
        descriptionField.setReadOnly(true);
        descriptionField.setValue(description);
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(descriptionField, 2);
        formLayout.setHeight("95%");



        formLayout.add(descriptionField);
        Button cancelButton = new Button("Fermer", e -> dialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(headline, formLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setHeight("100%");
        // dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        // dialogLayout.getStyle().set("width", "400px").set("max-width", "100%");
        return dialogLayout;
    }

    public void showPdfViewer(String fileName, byte[] date) {
        Dialog dialog = new Dialog();

        dialog.setWidth("60%");
        dialog.setHeight("90%");
        dialog.setResizable(true);
        dialog.setDraggable(true);


        PdfViewer pdfViewer = new PdfViewer();
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(date));
        resource.setContentType("application/pdf"); // Set content type to PDF
        pdfViewer.setHeight("95%");
        pdfViewer.setSrc(resource);
        dialog.add(pdfViewer);

        Button closeButton = new Button("Fermer", e -> dialog.close());
        dialog.add(closeButton);
        dialog.open();
    }

    public String generateReference(String OrgCode, String type, long count) {
        return OrgCode + "-" + type + "-" + Integer.toString(LocalDate.now().getYear() % 100) + String.format("%04d", count + 1);
    }
}
