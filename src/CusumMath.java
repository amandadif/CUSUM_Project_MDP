import java.util.Scanner;

// fisher yates shuffle to randomize the data in the array
// Use arrayList
// how many bootstraps do you want to do (iterations)?
// what's your confidence you want to use?
// randomize the data and find cusum for as many bootstraps as they want to use
// "change at 3305 with confidence 1.0... change at 2613 with confidence 1.0... Change at 2366 with confidence 0.97"
// Change Points Found:
// Value:    Confidence:
// 1168       1.0
// 2011       0.97
public class CusumMath {

  private int numBootstraps;
  private int confidenceLevel;

  public CusumMath() {
    numBootstraps = 0;
    confidenceLevel = 0;
  }

  public void userData() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("How many bootstraps? : ");
    numBootstraps = scanner.nextInt();
    System.out.print("What is your confidence level? : ");
    confidenceLevel = scanner.nextInt();
  }

  public int[] cusum() {

    int[] arr = {1, 2, 3, 4, 5};
    int[] cumulativeSum = new int[arr.length];

    cumulativeSum[0] = arr[0];
    for (int i = 1; i < arr.length; i++) {
      cumulativeSum[i] = cumulativeSum[i - 1] + arr[i];
    }

    // Prints the result
    for (int num : cumulativeSum) {
      System.out.print(num + " ");
    }

    return cumulativeSum;
    //calcAverage(arr);
  }

  public int calcAverage(int[] array) {
    int average = 0;
    for(int i = 0; i < array.length; i++){
      average += array[i];
    }
    average /= array.length;
    System.out.println("Average is " + average);
    return average;
  }

  public int randomizer(int[] array) {
    return 0;
  }

  public int getNumBootstraps() {
    return numBootstraps;
  }
  public int getConfidenceLevel() {
    return confidenceLevel;
  }



}
