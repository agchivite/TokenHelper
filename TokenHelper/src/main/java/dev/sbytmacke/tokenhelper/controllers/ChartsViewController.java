package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.util.List;

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
        initPieChart();
    }

    private void initPieChart() {
        List<UserDTO> users = mainViewController.userViewModel.getAll();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (UserDTO user : users) {
            // Limitar la longitud del nombre, ya que pieChar no permite mucho espacio
            String displayName = user.getUsername().length() > 14
                    ? user.getUsername().substring(0, 10) + "..." // Mostrar solo los primeros 10 caracteres seguidos de "..."
                    : user.getUsername();

            PieChart.Data slice = new PieChart.Data(displayName, user.getTotalBets());
            pieChartData.add(slice);
        }

        pieChart.setData(pieChartData);
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
