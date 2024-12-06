/*
package com.labo.fs.views.dashboard;

import com.labo.fs.data.dto.evaluation.CalculeModel;
import com.labo.fs.data.dto.evaluation.ChartModel;
import com.labo.fs.data.entity.Evaluation;
import com.labo.fs.data.service.EvaluationLineService;
import com.labo.fs.data.service.EvaluationService;
import com.labo.fs.views.MainLayout;
import com.labo.fs.views.evaluation.chart.SpiderChart;
import com.labo.fs.views.evaluation.evaluation.EvaluationListView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@PageTitle("Chart")
@Route(value = "chart/:evaluationID", layout = MainLayout.class)
@RolesAllowed({"SUPERADMIN", "USER", "ADMIN"})
public class ChartView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final String EVALUATION_FORM_ID = "evaluationID";
    private final String EVALUATION_FORM_EDIT_ROUTE_TEMPLATE = "chart/%s";
    private final EvaluationService evaluationService;
    private final EvaluationLineService evaluationLineService;
    private Evaluation evaluation;
    CalculeModel chartModel;
    SpiderChart spiderChart;
    Display display ;
    VerticalLayout verticalLayout;
    @Autowired

    public ChartView(EvaluationService evaluationService, EvaluationLineService evaluationLineService) {
        this.evaluationService = evaluationService;
        chartModel = new CalculeModel(evaluationLineService);
        this.evaluationLineService = evaluationLineService;
        display = new Display();
        spiderChart = new SpiderChart();
        //getContent().add(verticalLayout);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> evaluationId = event.getRouteParameters().get(EVALUATION_FORM_ID).map(UUID::fromString);
        if (evaluationId.isPresent()) {
            Optional<Evaluation> evaluationFromBackend = evaluationService.get(evaluationId.get());
            if (evaluationFromBackend.isPresent()) {
                evaluation = evaluationFromBackend.get();
                Map<String, Map<String, ChartModel>>  chartModelMaps = chartModel.articleToChartModel(evaluation.getId());
                verticalLayout = display.chartView(chartModelMaps.get("Chapitre"));
                List<ChartModel> valueList = new ArrayList<>(chartModelMaps.get("Chapitre").values());
                List<String> labels = new ArrayList<>();
                List<Integer> values = new ArrayList<>();

                valueList.stream().forEach(c->{
                    labels.add(c.getDisplayName());
                    values.add(c.getGrade().intValue());

                });
                verticalLayout.add(spiderChart);
                spiderChart.updateChart(labels,values);

                getContent().add(verticalLayout);
            } else {
                Notification.show(String.format("The requested maintenance type was not found, ID = %s", evaluationId.get()), 3000, Notification.Position.BOTTOM_START);
                event.forwardTo(EvaluationListView.class);
            }
        }
    }

}
*/
