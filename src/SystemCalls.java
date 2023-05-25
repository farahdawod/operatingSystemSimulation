public class SystemCalls {

  public DiskReader diskReader;
  public ConsoleHandler consoleHandler;
  public Memory memory;

  public SystemCalls(Memory memory) {
    diskReader = new DiskReader();
    consoleHandler = new ConsoleHandler();
    this.memory = memory;
  }

  public void writeToDisk(String text, String filePath) {
    diskReader.writeToDisk(text, filePath);
    /*translation = " try { BufferedWriter wr = new BufferedWriter(new FileWriter(" + instruction[1] + ")); \n" +
            "wr.write(" + instruction[2] + ");\n" +
            "} catch (IOException e) {\n" +
            "throw new RuntimeException(e);}";*/
  }

  public String readFromDisk(String filePath) throws Exception {
    return diskReader.readFromDisk(filePath);
  }

  public void print(String text) {
    consoleHandler.printData(text);
  }

  public String takeInput() {
    return consoleHandler.takeInput();
    /*translation = "System.out.print(\"Please enter a value\");" + '\n'
                          + "Scanner sc = new Scanner(System.in);" + '\n'
                          + "if (sc.hasNextInt()) { int " + instruction[1] + " = sc.nextInt(); } \n" +
                          "else { String" + instruction[1] + "= sc.nextLine(); }";*/
  }

  public void addToMemory(String descriptionOfData, Object data, int index) {
    memory.add(descriptionOfData, data, index);
  }

  public Object readFromMemory(int index) {
    return memory.getFromMemory(index);
  }
}
