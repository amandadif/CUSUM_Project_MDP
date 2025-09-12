import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CUSUM_GUI extends Application {

  public static int[] array;
  public void start(Stage primaryStage) {

    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Time");
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Values");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("CUSUM Chart");

    XYChart.Series<Number, Number> dataSeries1 = new XYChart.Series<>();
    dataSeries1.setName("Series 1");

    for (int i = 0; i < array.length; i++) {
      dataSeries1.getData().add(new XYChart.Data<>((i+1), array[i]));
    }

    lineChart.getData().add(dataSeries1);

    Scene scene = new Scene(lineChart, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();


  }

  public static void main(String[] args) {
    launch(args);
  }
}
