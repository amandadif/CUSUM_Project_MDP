import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class CUSUM_GUI extends Application {
  public static int[] array2;
  public static int[] CUSUM_Array;
  public void start(Stage primaryStage) {
    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Time");
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Values");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("CUSUM Chart");

    XYChart.Series<Number, Number> dataSeries1 = new XYChart.Series<>();
    dataSeries1.setName("CUSUM");

    XYChart.Series<Number, Number> dataSeries2 = new XYChart.Series<>();
    dataSeries2.setName("Data set");

    for (int i = 0; i < CUSUM_Array.length; i++) {
      dataSeries1.getData().add(new XYChart.Data<>((i+1), CUSUM_Array[i]));
    }

    for (int i = 0; i < array2.length; i++) {
      dataSeries2.getData().add(new XYChart.Data<>((i+1), array2[i]));
    }

    lineChart.getData().add(dataSeries1);
    lineChart.getData().add(dataSeries2);

    Scene scene = new Scene(lineChart, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();


  }

  public static void main(String[] args) {
    launch(args);
  }
}
