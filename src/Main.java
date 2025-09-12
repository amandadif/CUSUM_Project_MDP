
public class Main {

  public static void main(String[] args) {

    CusumMath cusumMath = new CusumMath();
    CUSUM_GUI.array = cusumMath.cusum();
    cusumMath.userData();

    CUSUM_GUI.main(args);
  }
}