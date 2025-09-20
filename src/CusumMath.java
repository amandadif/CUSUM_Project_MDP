import java.util.ArrayList;
import java.util.Random;

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

/**
 * The cusum math class takes the raw data, builds a mean centered array, calculates the Sdiff as a score, compares
 * that score to many other shuffled scenarios based on how many bootstraps there are. If it is found to be significant, it locates
 * the change from the extreme value in the cusum array, then recursively splits the array and repeats until it finds all
 * changes.
 */
public class CusumMath {

  public static record ChangePoint(int index, double confidence) {}
  private record SegmentResults(boolean significant, int changeIndex, double confidence, double sDiff) {}
  public static int numBootstraps;
  public static double confidenceLevel;
  private Random randomInt;
  private ArrayList<ChangePoint> results = new ArrayList<>();  //(index, confidence) pairs list, can remove if we just return a record

  public CusumMath() {
    numBootstraps = 0;
    confidenceLevel = 0.0;
    randomInt = new Random();
  }

  /**
   * Calculates a cumulative sum from an array of deviations from the mean
   *
   * @param arr
   * @return
   */
  public double[] cusum(double[] arr) {
    double mean = calcAverage(arr); //uses the overall mean
    double[] cumulativeSum = new double[arr.length]; //array of deviations from the overall mean?

    cumulativeSum[0] = (int) (arr[0] - mean);   //first element in the array is the (value - mean)
    for (int i = 1; i < arr.length; i++) {
      cumulativeSum[i] = cumulativeSum[i - 1] + (arr[i] - mean);
    }

    return cumulativeSum;
  }

  public double calcAverage(double[] array) {
    double average = 0;
    for(int i = 0; i < array.length; i++){
      average += array[i];
    }
    average /= array.length;

    return average;
  }

  /**
   * total vertical spread
   *
   * @param array
   * @return
   */
  public double calcSdiff(double[] array) {
    double maxValue = array[0];   //initialize both with the first element in the array
    double minValue = array[0];
    double diff = 0;

    for(int i = 0; i < array.length; i++){  //goes through the array once it's computed from cusum
      maxValue = Math.max(maxValue, array[i]);
      minValue = Math.min(minValue, array[i]);
    }
    diff = maxValue - minValue;

    return diff;
  }

  /**
   * uses fisher yates shuffle
   *
   * @param array
   * @return
   */
  public double[] randomizer(double[] array) {
    double[] shuffledArray = new double[array.length];
    double temp = 0;
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
   *
   * @param originalArray
   * @returns Sdiff permanent
   */
  public double bootstrapOnce(double[] originalArray) {   //run one bootstrap iteration and return a single statistic
    double[] shuffledArray = randomizer(originalArray); //gets a shuffled copy
    double[] shuffledCusum = cusum(shuffledArray);
    double Sdiff = calcSdiff(shuffledCusum);
    return Sdiff;
  }

  /**
   *
   * @param originalArray
   * @param num
   * @return
   */
  public double bootstrapConfidence(double[] originalArray, int num) {
    double[] originalCusum = cusum(originalArray);
    double originalSdiff = calcSdiff(originalCusum);
    double confidence;
    if(num <= 0){
      return 0.0;
    }

    int count = 0;
    for(int i = 0; i < num; i++) {
      double newSdiff = bootstrapOnce(originalArray);
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
  public int estimateChangeIndex(double[] cusum) {
    double maxAbs = Math.abs(cusum[0]);
    int changeIndex = 0;
    for(int i = 1; i < cusum.length; i++) {
      double currentValue = Math.abs(cusum[i]); //keeps track of largest value in cusum array
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
  public SegmentResults analyzeSegment(double[] arrayData) {
    if(arrayData == null || arrayData.length < 4) {  //just <4 because it needs to be large enough
      return new SegmentResults(false, -1, 0.0, 0);
    }
    double[] cusumArray = cusum(arrayData);  //builds the mean centered cusum of the array
    double sdiff = calcSdiff(cusumArray);  // computes the sDiff
    double confidence = bootstrapConfidence(arrayData, getNumBootstraps()); //computes the confidence with bootstrap method
    if(confidence < getConfidenceLevel()) {   //compare this confidence to the given confidence level
      return new SegmentResults(false, -1, confidence, sdiff);
    }
    int index = estimateChangeIndex(cusumArray);
    return new SegmentResults(true, index, confidence, sdiff);
  }

  public double[] split(double[] array, int from, int to){
    if(from > to) {
      return new double[0]; //return an empty array
    }
    double[] temp = new double[to - from + 1];
    for(int i = from; i <= to; i++) {   //fill temp with parameter array
      temp[i - from] = array[i];
    }
    return temp;
  }

  public ArrayList<ChangePoint> findChanges(double[] array) {
    int startOffset = 0;
    ArrayList<ChangePoint> changePoints = new ArrayList<>();
    if(array == null || array.length < 4) {
      return new ArrayList<>();  //return an empty array
    }
    return findChangesRecursive(array, startOffset);
  }

  public ArrayList<ChangePoint> findChangesRecursive(double[] array, int startOffset) {
    ArrayList<ChangePoint> out = new ArrayList<>();
    if(array == null || array.length < 4) {
      return out;
    }
    SegmentResults segmentResults = analyzeSegment(array);
    if(!segmentResults.significant()) {
      return out;
    }
    int index = segmentResults.changeIndex(); //index is within array
    if(index <= 0 || index >= array.length - 1) {
      return out;
    }
    int indexOverall = startOffset + index;
    out.add(new ChangePoint(indexOverall, segmentResults.confidence()));

    double[] left = split(array, 0, index); //split the array at the change from the beginning
    double[] right = new double[0];
    if(index + 1 <= array.length - 1) {
       right = split(array, index + 1, array.length - 1);  //offset by + 1 because the right segment begins after the split point
    }

    out.addAll(findChangesRecursive(left, startOffset));  //recursion
    out.addAll(findChangesRecursive(right, startOffset + index + 1));  //recursion. adds an entire list into out list

    return out;
  }

  public int getNumBootstraps() {
    return numBootstraps;
  }
  public double getConfidenceLevel() {
    return confidenceLevel;
  }

}
