import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FileReader {
  public static int[] extractData() throws IOException {
  try(Scanner sc = new Scanner(System.in)) {
    System.out.println("What file would you like to read?");
    String fileName = sc.nextLine();
    RandomAccessFile raf ;try {
      raf = new RandomAccessFile(fileName, "r");
      System.out.println("Found file " + fileName + " yippee!");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    raf.seek(0);
    int[] rows = new int[50];
    String line;
    int i = 0;
    while ((line = raf.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;
      rows[i] = (Integer.parseInt(line));
      i++;
    }
    System.out.println(Arrays.toString(rows));
    return rows;
  }
  }
}
