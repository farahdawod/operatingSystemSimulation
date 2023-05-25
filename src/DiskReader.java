import java.util.*;
import java.io.*;
public class DiskReader {
    // This class has the 2 system calls to read and write to the disk
    public String readFromDisk(String filePath) throws Exception{
        try{
            File file = new File(filePath);
            Scanner reader= new Scanner(file);
            StringBuilder sb= new StringBuilder();
            while(reader.hasNextLine()){
                sb.append(reader.nextLine());
            }
            reader.close();
            return sb.toString();
        }
        catch(Exception ex){
            throw ex;
        }
        /*translation = "try { BufferedReader br = new BufferedReader(new FileReader(" + instruction[3] + "));\n" +
                "String " + instruction[1] + "= br.readLine(); } \n" +
                " catch (FileNotFoundException e) {throw new RuntimeException(e);\n" +
                "} catch( IOException e) {System.out.println(\"Error reading program file: \" + e.getMessage());\n" +
                "}";*/
    }

    public boolean writeToDisk(String text, String filePath) {

        try{

            FileWriter writer = new FileWriter(filePath);
            writer.write(text);
            writer.close();
            return true;

        }
        catch(Exception ex){
            return false;
        }
    }
}