package Convert.Model;

import java.io.File;

public abstract class Iso {


    private File InputFile;
    private String Name;
    public String Type;



    public void setInputFile(File inputFile) {
        InputFile = inputFile;
    }

    public File getInputFile() {
        return InputFile;
    }

    protected String getName() {
        return Name;
    }

    protected void setName(String name) {
        Name = name;
    }

    protected String getType() {
        return Type;
    }

    protected void setType(String type) {
        Type = type;
    }

}
