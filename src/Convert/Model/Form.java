package Convert.Model;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.File;


public class Form {

    public boolean checkForm(TextField txtInputFile){
        File file = new File(txtInputFile.getText());
        return file.exists();
    }
}
