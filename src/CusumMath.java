import java.util.ArrayList;
import java.util.Random;
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

  private record ChangePoint(int index, double confidence) {}
  private record SegmentResults(boolean significant, int changeIndex, double confidence, int sDiff) {}
  public int numBootstraps;
  public double confidenceLevel;
  private Random randomInt;
  private ArrayList<Integer> results; //change to (index, confidence) pairs list

  public CusumMath() {
    numBootstraps = 0;
    confidenceLevel = 0.0;
    randomInt = new Random();
  }

  public void userData() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("How many bootstraps? : ");
    numBootstraps = scanner.nextInt();
    System.out.print("What is your confidence level? : ");
    double num = scanner.nextDouble();
    confidenceLevel = num / 100; //change the whole number to a percent
  }

  /**
   * Calculates a cumulative sum from an array of deviations from the mean
   * @param arr
   * @return
   */
  public int[] cusum(int[] arr) {
    int mean = calcAverage(arr); //uses the overall mean
    int[] cumulativeSum = new int[arr.length]; //array of deviations from the overall mean?

    cumulativeSum[0] = arr[0] - mean;   //first element in the array is the (value - mean)
    for (int i = 1; i < arr.length; i++) {
      cumulativeSum[i] = cumulativeSum[i - 1] + (arr[i] - mean);
    }

    // Prints the result for now, we can delete once done
    for (int num : cumulativeSum) {
      System.out.print(num + " ");
    }

    return cumulativeSum;
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

  /**
   * total vertical spread
   * @param array
   * @return
   */
  public int calcSdiff(int[] array) {
    int maxValue = array[0];   //initialize both with the first element in the array
    int minValue = array[0];
    int diff = 0;

    for(int i = 0; i < array.length; i++){  //goes through the array once it's computed from cusum
      maxValue = Math.max(maxValue, array[i]);
      minValue = Math.min(minValue, array[i]);
    }
    diff = maxValue - minValue;

    return diff;
  }

  /**
   * uses fisher yates shuffle
   * @param array
   * @return
   */
  public int[] randomizer(int[] array) {
    int[] shuffledArray = new int[array.length];
    int temp = 0;
    int randomIndex = 0;

    if(array == null || array.length < 2){  //if it's too small or null, there's nothing to do so just return the array
      return array;
    }
    int j = 0;
    while(j < array.length) {
      shuffledArray[j] = array[j];   //fill the shuffledArray with the same values at the same places as array
      j++;
    }
    for(int i = shuffledArray.length-1; i > 0; i--){   //start at last index, goes backwards
      randomIndex = randomInt.nextInt(i+1);  //generate a new random index on every loop pass between 0 and i
      temp = shuffledArray[i];
      shuffledArray[i] = shuffledArray[randomIndex];
      shuffledArray[randomIndex] = temp;  //swap the og value with the random new value
    }
    return shuffledArray;
  }

  /**
   * need this to get a base statistic to compare to
   * might want to make into a double eventually?
   * @param originalArray
   * @returns Sdiff permanent
   */
  public int bootstrapOnce(int[] originalArray) {   //run one bootstrap iteration and return a single statistic
    int[] shuffledArray = randomizer(originalArray); //gets a shuffled copy
    int[] shuffledCusum = cusum(shuffledArray);
    int Sdiff = calcSdiff(shuffledCusum);
    return Sdiff;
  }

  /**
   *
   * @param originalArray
   * @param num
   * @return
   */
  public double bootstrapConfidence(int[] originalArray, int num) {
    int[] originalCusum = cusum(originalArray);
    int originalSdiff = calcSdiff(originalCusum);
    double confidence;
    int count = 0;
    for(int i = 0; i < num; i++) {
      int newSdiff = bootstrapOnce(originalArray);
      if(newSdiff < originalSdiff) {
        count++;
      }
    }
    confidence = count / (double) num;
    return confidence;    //we use this to compare this confidence to the base
                          // number and we can tell if its significant change or not
  }

  /**
   * used to estimate where the change happened using the largest index in the array
   * @param cusum
   * @returns the largest number's index in cusum
   */
  public int estimateChangeIndex(int[] cusum) {
    int maxAbs = Math.abs(cusum[0]);
    int changeIndex = 0;
    for(int i = 1; i < cusum.length; i++) {
      int currentValue = Math.abs(cusum[i]); //keeps track of largest value in cusum array
      if(currentValue > maxAbs) {
        maxAbs = currentValue;
        changeIndex = i;
      }
    }
    return changeIndex;
  }

  /**
   * Decides of the segment has a significant change
   * If yes, it will estimate where
   * @param arrayData could be the whole array or a sub array from recursion
   */
  public SegmentResults analyzeSegment(int[] arrayData) {


  }

  public int getNumBootstraps() {
    return numBootstraps;
  }
  public double getConfidenceLevel() {
    return confidenceLevel;
  }













}
