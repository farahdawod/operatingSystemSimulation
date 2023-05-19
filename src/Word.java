import java.util.ArrayList;

public class Word {
    String typeOfDataStored;
    Object data;

    public void setTypeOfDataStored(String typeOfDataStored) {
        this.typeOfDataStored = typeOfDataStored;
    }

    public String getTypeOfDataStored() {
        return typeOfDataStored;
    }

    public Object getData() {
        return data;
    }

    public Word(String typeOfDataStored, Object data) {
        this.typeOfDataStored = typeOfDataStored;
        this.data = data;
    }
}
