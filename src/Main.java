import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    CusumMath cusumMath = new CusumMath();
    cusumMath.userData();
    CUSUM_GUI.array = cusumMath.cusum(FileReader.extractData());
    //cusumMath.userData();

    CUSUM_GUI.main(args);
  }
}