import javafx.application.Application;
import javafx.scene.Node;
import java.awt.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;

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
    lineChart.setStyle("-fx-background-color: lightgray");

    XYChart.Series<Number, Number> dataSeries1 = new XYChart.Series<>();
    dataSeries1.setName("CUSUM");

    XYChart.Series<Number, Number> dataSeries2 = new XYChart.Series<>();
    dataSeries2.setName("Data set");

    lineChart.getData().add(dataSeries1);
    lineChart.getData().add(dataSeries2);

    VBox sidePane = new VBox(10);
    //sidePane.setPadding(new Insets(10,10,10,10));
    sidePane.setStyle("-fx-background-color: lightgray");

    Label bootstraps = new Label("Bootstraps: ");
    Label confidence = new Label("Confidence: ");
    Label fileName = new Label("File name: ");
    TextField bootStrap = new TextField();
    TextField confidenceText = new TextField();
    TextField textFile = new TextField();
    Button applyButton = new Button("apply");
    Button exitButton = new Button("exit");

    applyButton.setOnAction(e -> {
      CusumMath.numBootstraps = Integer.parseInt(bootStrap.getText());
      CusumMath.confidenceLevel = Integer.parseInt(confidenceText.getText());
      FileReader.fileName= (textFile.getText());

      try {
        int[] fileData = FileReader.extractData();//reads the file and puts the data into an array
        CUSUM_GUI.array2 = fileData;
        CusumMath cusumMath = new CusumMath();
        CUSUM_GUI.CUSUM_Array = cusumMath.cusum(fileData);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      for (int i = 0; i < CUSUM_Array.length; i++) {
        dataSeries1.getData().add(new XYChart.Data<>((i+1), CUSUM_Array[i]));
      }

      for (int i = 0; i < array2.length; i++) {
        dataSeries2.getData().add(new XYChart.Data<>((i+1), array2[i]));
      }
    });
    exitButton.setOnAction(e -> {
      System.exit(0);
    });
    sidePane.getChildren().addAll(bootstraps, bootStrap, confidence,confidenceText, fileName,textFile,applyButton,exitButton);

    BorderPane root = new BorderPane();

    root.setCenter(lineChart);
    root.setLeft(sidePane);

    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();

  }

  public static void main(String[] args) {
    launch(args);
  }
}
