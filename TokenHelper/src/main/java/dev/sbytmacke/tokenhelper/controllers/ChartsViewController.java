package dev.sbytmacke.tokenhelper.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class ChartsViewController {
/*
    @FXML
    private NumberAxis percentSuccess;
    @FXML
    private NumberAxis totalBets;
    @FXML
    private LineChart<Number, Number> lineChart;
*/

    @FXML
    private PieChart pieChart;

    private MainViewController mainViewController;


    public void init(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        initBindings();
    }

    private void initBindings() {
        //initLineChartsAxis();
    }

/*    private void initLineChartsAxis() {
        // Configuración del gráfico de línea
        percentSuccess.setLabel("X - % Success");
        totalBets.setLabel("Y - Total Bets");

        // Configuración de la serie de datos
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        List<UserDTO> users = mainViewController.userViewModel.getAll();

        // Cargar los datos en la serie
        for (UserDTO bets : users) {
            series.getData().add(new XYChart.Data<>(bets.getTotalSuccess(), bets.getTotalBets()));
        }

        // Añadir la serie al gráfico
        lineChart.getData().add(series);
    }*/

}
