import java.util.*;

public class ConsoleHandler {

  public void printData(String text) {
    // Can he replaces with a GUI
    System.out.println(text);
  }

  public String takeInput() {
    printData("Please Enter your input");
    Scanner sc = new Scanner(System.in);
    String input = sc.nextLine();
    //sc.close();
    return input;
  }
}
