package home.controllers;


import Convert.Model.DigilibIso;
import Convert.Model.Form;
import Convert.Model.Nosa;
import Utilities.Utilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static Utilities.Utilities.*;

public class Controller implements Initializable {


    @FXML
    public static TextField counter_recordPublic, counter_recordPublic2;
    @FXML
    public static int counters_public;
    @FXML
    public static boolean check = true;
    @FXML
    public TextField txtInputFile, txtMappingPath, centerName, txtMappingPathsim, centerNamesim;
    @FXML
    public TextField counter_record, counter_record2;
    public TextField username,password,tablename;
    @FXML
    public HBox Type, Typesim;
    @FXML
    public RadioButton simorgh, digilib;
    @FXML
    public int counters;
    public String labelString;
    @FXML
    private Pane IsoConvertPane3;
    @FXML
    private Pane Pane4;
    @FXML
    private Pane IsoConvertPane1;
    @FXML
    private Pane IsoConvertPane2;
    @FXML
    private Button digiCancel, simorghCancel, btnConvertSim;
    @FXML
    private ComboBox types;

    public Controller() {
    }

    public static void Convert(String inputPath, String mappingPath, List<String> docType, String centerName, String counter, String isotype) {
        List<Record> convertedList = null;
        OutputStreamWriter writer = null;
        String outputPath;
        String ref;
        String iso = "", file_out = "";
        counters_public = 0;
        for (String item : docType) {
            //int j=inputPath.lastIndexOf('\\');
            //outputPath=inputPath.substring(1, inputPath.lastIndexOf('\\'));
            outputPath = inputPath.substring(0, inputPath.lastIndexOf('\\') + 1).concat(item.concat("_Converted.prs"));
            if (isotype == "digilib")
                convertedList = ConvertByMappingFile(DigilibIso.getInstance(new File(inputPath), item).getIsoRecords(), getMapping(mappingPath, item));
            else if (isotype == "simorgh") {
                check = false;
                convertedList = ConvertByMappingFile(Nosa.getInstance(new File(inputPath), item).getIsoRecords(), getMapping(mappingPath, item));
            }
            for (Record record : convertedList) {
                iso = makeIso(record, docType, centerName, isotype);
                file_out = file_out + iso;
            }
            try {
                writer = new OutputStreamWriter(new FileOutputStream(outputPath));
                if (new File(outputPath).exists())
                    writer.write(file_out.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @FXML
    public static String makeIso(Record record, List docType, String centerName, String isoType) {
        String iso = "", tag = "", length = "", offset = "", data = "", data2 = "", temp = "", simStr = "";
        referenced = "";
        String newStr = "";
        byte[] bytes2 = null;
        int L = 0, count = 0;
        StringBuilder newtemp = new StringBuilder();
        for (DataField df : record.getDataFields()) {
            tag = df.toString().substring(0, 3);
            data = df.toString().substring(4);
            DataField field = df;
            List subfields = field.getSubfields();
            Iterator i = subfields.iterator();
            while (i.hasNext()) {
                Subfield subfield = (Subfield) i.next();
                char code = subfield.getCode();
                String data1 = subfield.getData();

                if (isoType == "digilib") {
                    // مرجع
                    if (!record.toString().contains("è\u200F¤")) {
                        referenced = "ô";
                    }
                    //مرجع
//check farsi number & center
                    if (docType.get(0).toString().charAt(1) == 'F') {
                        StringBuilder checkFarsi = new StringBuilder(data1);
                        if (!tag.equals("100")) {
                            changeLatinToFarsi(checkFarsi);
                        }
                        data1 = checkFarsi.toString();
                    }
                    if ((tag.equals("801") && subfield.getCode() == 'b')) {
                        data1 = centerName;
                        StringBuilder centerNames = new StringBuilder(data1);
                        changeFarsiString(centerNames);
                        data1 = centerNames.toString();
                    }
                    if (tag.equals("200") && subfield.getCode() == 'b' || (tag.equals("915") && subfield.getCode() == 'a')) {
                        StringBuilder parsfix = new StringBuilder(data1);
                        changeFarsiString(parsfix);
                        data1 = parsfix.toString();
                    }
                }
//check farsi number & center

                //marking
                StringBuilder sign = new StringBuilder(data1);
                if (tag.equals("200") && subfield.getCode() == 'f')
                    if (data1.indexOf(0) != '/') sign.insert(0, "/ ");
                if (tag.equals("200") && subfield.getCode() == 'e')
                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
                if (tag.equals("210") && subfield.getCode() == 'c')
                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
                if (tag.equals("215") && subfield.getCode() == 'c')
                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
                if (tag.equals("215") && subfield.getCode() == 'a')
                    if (data1.contains(":"))
                        sign.insert(sign.indexOf(":"), "\u001Fc");


                if (isoType == "simorgh") {
                    if ((tag.equals("915")) || (tag.equals("200")) & subfield.getCode() == 'b' || (tag.equals("801") && subfield.getCode() == 'b')) {
                        if (tag.equals("801")) data1 = centerName;
                        String oldStr = data1;
                        byte[] bytes = oldStr.getBytes(StandardCharsets.UTF_8);
                        newStr = new String(bytes);
                        StringBuilder sign3 = new StringBuilder(newStr);
                        sign = sign3;
                    } else {
                        bytes2 = data1.getBytes();
                        simStr = new String(bytes2, StandardCharsets.UTF_8);

                        StringBuilder sign2 = new StringBuilder(simStr);

                        if (tag.equals("200") && subfield.getCode() == 'g') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '؛')
                                    sign2.insert(0, "؛ ");
                                else {
                                    if (simStr.indexOf(0) != ';')
                                        sign2.insert(0, "; ");
                                }
                        }
                        if (tag.equals("210") && subfield.getCode() == 'd') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '،')
                                    sign2.insert(0, "، ");
                                else {
                                    if (simStr.indexOf(0) != ',')
                                        sign2.insert(0, ", ");
                                }
                        }

                        if (tag.equals("225") && subfield.getCode() == 'v') {
                            if (textLanguage(sign2).contains("F"))
                                if (simStr.indexOf(0) != '؛')
                                    sign2.insert(0, "؛ ");
                            if (textLanguage(sign2).contains("L"))
                                if (simStr.indexOf(0) != ';')
                                    sign2.insert(0, "; ");
                        }
                        if (tag.equals("700") && subfield.getCode() == 'f') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '،')
                                    sign2.insert(0, "، ");
                                else {
                                    if (simStr.indexOf(0) != ',')
                                        sign2.insert(0, ", ");
                                }
                        }
                        if (tag.equals("700") && subfield.getCode() == '4') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '،')
                                    sign2.insert(0, "، ");
                                else {
                                    if (simStr.indexOf(0) != ',')
                                        sign2.insert(0, ", ");
                                }

                        }
                        if (tag.equals("702") && subfield.getCode() == 'f') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '،')
                                    sign2.insert(0, "، ");
                                else {
                                    if (simStr.indexOf(0) != ',')
                                        sign2.insert(0, ", ");
                                }
                        }
                        if (tag.equals("702") && subfield.getCode() == '4') {
                            if (docType.toString().indexOf('L') <= 0)
                                if (simStr.indexOf(0) != '،')
                                    sign2.insert(0, "، ");
                                else {
                                    if (simStr.indexOf(0) != ',')
                                        sign2.insert(0, ", ");
                                }
                        }


                        String newstr2 = new String(sign2);
                        byte[] bytes = newstr2.getBytes(StandardCharsets.UTF_8);
                        newStr = new String(bytes);
                        StringBuilder sign3 = new StringBuilder(newStr);
                        sign = sign3;
                    }
                }

                //Digilib
                if (isoType == "digilib") {
                    if (tag.equals("200") && subfield.getCode() == 'g') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ف') sign.insert(0, "ف ");
                            else {
                                if (data1.indexOf(0) != ';')
                                    sign.insert(0, "; ");
                            }

                    }

                    if (tag.equals("210") && subfield.getCode() == 'd') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
                            else {
                                if (data1.indexOf(0) != ',')
                                    sign.insert(0, ", ");
                            }
                    }

                    if (tag.equals("225") && subfield.getCode() == 'v') {
                        if (textLanguage(sign).contains("F"))
                            if (data1.indexOf(0) != 'ف') sign.insert(0, "ف ");
                        if (textLanguage(sign).contains("L"))
                            if (data1.indexOf(0) != ';') sign.insert(0, "; ");
                    }
                    if (tag.equals("700") && subfield.getCode() == 'f') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
                            else {
                                if (data1.indexOf(0) != ',')
                                    sign.insert(0, ", ");
                            }
                    }
                    if (tag.equals("700") && subfield.getCode() == '4') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
                            else {
                                if (data1.indexOf(0) != ',')
                                    sign.insert(0, ", ");
                            }

                    }
                    if (tag.equals("702") && subfield.getCode() == 'f') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
                            else {
                                if (data1.indexOf(0) != ',')
                                    sign.insert(0, ", ");
                            }
                    }
                    if (tag.equals("702") && subfield.getCode() == '4') {
                        if (docType.toString().indexOf('L') <= 0)
                            if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
                            else {
                                if (data1.indexOf(0) != ',')
                                    sign.insert(0, ", ");
                            }
                    }
                    if (tag.equals("215") && subfield.getCode() == 'a')
                        if (data1.contains(":"))
                            sign.insert(sign.indexOf(":"), "\u001Fc");
                    //marking


                    //chap
                    if (tag.equals("923") && subfield.getCode() == 'a') {
                        if (data1.equals("\u200C‘•\u200E"))
                            sign.replace(0, data1.length(), "p");
                        else if (data1.equals("\u200C‘•\u200E - ‘َî—¤ّ÷\u200Fî\u200E"))
                            sign.replace(0, data1.length(), "pe");
                        else if (data1.equals("‘َî—¤ّ÷\u200Fî\u200E"))
                            sign.replace(0, data1.length(), "e");
                        else if (data1.equals("‘َî—¤ّ÷\u200Fî\u200E - َّ\u200D êھ¤¢ù"))
                            sign.replace(0, data1.length(), "ec");
                        else if (data1.equals("\u200C‘•\u200E - َّ\u200D êھ¤¢ù"))
                            sign.replace(0, data1.length(), "pc");
                        else sign.replace(0, data1.length(), "p");
                    }
                    //chap

                    // acquisition
                    if (tag.equals("999") && subfield.getCode() == 'd') {
                        if (data1.contains("،¤\u200F¢گ¤\u200E") || data1.contains("پ"))   //1
                            sign.replace(0, data1.length(), "a\u001Fd0a\u001Fd1a\u001Fd2a");
                        else if (data1.contains("\u0090û¢\u0090þü") || data1.contains("‚"))   //2
                            sign.replace(0, data1.length(), "b\u001Fd0a\u001Fd1a\u001Fd2a");
                        else if (data1.contains("øìêü") || data1.contains("ƒ"))   //3
                            sign.replace(0, data1.length(), "j\u001Fd0a\u001Fd1a\u001Fd2a");
                        else if (data1.contains("—øóþ¢") || data1.contains("„"))   //4
                            sign.replace(0, data1.length(), "i\u001Fd0a\u001Fd1a\u001Fd2a");
                        else if (data1.contains("…"))   //5
                            sign.replace(0, data1.length(), "z\u001Fd0a\u001Fd1a\u001Fd2a");
                        else
                            sign.replace(0, data1.length(), "a\u001Fd0a\u001Fd1a\u001Fd2a");
                    }
                    // acquisition

                    //reference
                    if (tag.equals("999") && subfield.getCode() == 'a') {
                        if (data1.contains("è\u200F¤ُ¤›â"))   //غیرمرجع
                            sign.replace(0, data1.length(), "1");
                        else {
                            sign.replace(0, data1.length(), "0");
                        }
                    }


                    if ((tag.equals("680") && subfield.getCode() == 'b') || (tag.equals("676") && subfield.getCode() == 'b') || (tag.equals("686") && subfield.getCode() == 'b')) {
                        if (!data1.equals(""))
                            if (referenced.equals("ô")) {
                                sign.insert(sign.length(), "\u001F9" + referenced);
                            }

                    }
                    if ((tag.equals("680") && subfields.size() == 1 && subfield.getCode() == 'a') || (tag.equals("676") && subfields.size() == 1 && subfield.getCode() == 'a') || (tag.equals("686") && subfields.size() == 1 && subfield.getCode() == 'a')) {
                        if (!data1.equals(""))
                            if (referenced.equals("ô")) {
                                sign.insert(sign.length(), "\u001F9" + referenced);
                            }

                    }
                    //reference
                    if (tag.equals("410") && subfield.getCode() == 't') {
                        if (data1.indexOf("ف") > 0) {
                            sign.insert(data1.indexOf("ف"), "\u001Fv");
                            sign.insert(data1.indexOf("ف") + 3, " ");

                        } else if (data1.indexOf(";") > 0) {
                            sign.insert(data1.indexOf(";"), "\u001Fv");
                            sign.insert(data1.indexOf(";") + 3, " ");

                        }
                    }
                }
                //Digilib
                data1 = sign.toString();
                data2 = data2 + "\u001F" + code + data1;
            }

            //subfield
            data = "  " + data2;
            data2 = "";
            length = String.valueOf(data.length());
            while (length.length() < 4) {
                length = "0" + length;
            }
            L = newtemp.length();
            if (L > 0) {
                L = L - count * 12;

            } else {
                L = 0;
            }
            offset = String.valueOf(L);
            while (offset.length() < 5) {
                offset = "0" + offset;

            }

            temp = tag + length + offset;
            newtemp.insert(count * 12, temp);
            newtemp.insert(newtemp.length(), data);
            count++;

        }
        iso = newtemp.toString() + "\u001E\u001D";
        length = String.valueOf(iso.length() + 24);
        while (length.length() < 5) {
            length = "0" + length;
        }
        L = count * 12 + 25;
        offset = String.valueOf(L);
        while (offset.length() < 4) {

            offset = "0" + offset;

        }
        iso = length + "nam00220" + offset + "1n04500" + iso;
        if (isoType == "digilib") {
            counters_public++;
            counter_recordPublic.setText(String.valueOf(counters_public));
        } else if (isoType == "simorgh") {
            counters_public++;
            counter_recordPublic2.setText(String.valueOf(counters_public));
        }
        return iso;
    }


    @FXML


    public ComboBox getTypes() {
        return types;
    }

    @FXML
    void btnConvertIsoHandler(MouseEvent mouseEvent) {

    }

    @FXML
    void btnExitHandler(MouseEvent mouseEvent) {
        System.exit(0);
    }

    @FXML
    public void btnMappingInputFileHandler(MouseEvent mouseEvent) {

        txtMappingPath.setText(Utilities.InputDialog("XML files (*.xml)", "*.xml").getPath());
    }

    @FXML
    public void btnIsoInputFileHandler(MouseEvent mouseEvent) {
        txtInputFile.setText(Utilities.InputDialog("TEXT files (*.txt)", "*.txt;*.iso;*.prs").getPath());
    }

    @FXML
    public void btnNextIsoConvert(MouseEvent mouseEvent) {
        Form form = new Form();
        if (form.checkForm(txtInputFile)) {
            if (digilib.isSelected()) {
                String[][] typeArray = DigilibIso.findAllTypes(txtInputFile.getText());
                List<String> typeList = new ArrayList<String>();
                for (int i = 0; i < typeArray[1].length; i++)
                    if (typeArray[1][i].equals("1")) {
                        if (Type.getChildren().size() > 0)
                            Type.getChildren().remove(0);
                        Type.getChildren().add(new CheckBox(typeArray[0][i]));
                    }
            } else if (simorgh.isSelected()) {
                //  Type.getChildren().add(new CheckBox(types.getValue()));

            }

            if (Type.getChildren().isEmpty())
                Type.getChildren().add(new Label("مدرکی وجود ندارد."));
            IsoConvertPane1.disableProperty().set(true);
            IsoConvertPane1.visibleProperty().set(false);
            if (digilib.isSelected()) {
                IsoConvertPane2.visibleProperty().set(true);
                IsoConvertPane2.disableProperty().set(false);
            }
            if (simorgh.isSelected()) {
                IsoConvertPane3.visibleProperty().set(true);
                IsoConvertPane3.disableProperty().set(false);

            }
        }
    }

    @FXML
    void btnConvertHandler(MouseEvent event) {
        List<String> typeList = new ArrayList<String>();
        String reference = "";
        if (digilib.isSelected()) {
            for (int i = 0; i < Type.getChildren().size(); i++)
                if (((CheckBox) Type.getChildren().get(i)).isSelected())
                    typeList.add(((CheckBox) Type.getChildren().get(i)).getText());
            Controller.Convert(txtInputFile.textProperty().get(), txtMappingPath.textProperty().get(), typeList, centerName.getText(), counter_record.getText(), "digilib");
        }
        if (simorgh.isSelected()) {
            typeList.add(String.valueOf(types.getValue()));
            Controller.Convert(txtInputFile.textProperty().get(), txtMappingPath.textProperty().get(), typeList, centerNamesim.getText(), counter_record.getText(), "simorgh");
        }
        JLabel label = new JLabel(counters_public + " records converted successfully");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        JOptionPane.showMessageDialog(null, label, "", JOptionPane.INFORMATION_MESSAGE);
    }


    @FXML
    void btnCancel(MouseEvent event) {
        IsoConvertPane1.visibleProperty().set(true);
        IsoConvertPane1.disableProperty().set(false);
        if (digiCancel.isFocused()) {
            IsoConvertPane2.visibleProperty().set(false);
            IsoConvertPane2.disableProperty().set(true);
        } else if (simorghCancel.isFocused()) {
            IsoConvertPane3.visibleProperty().set(false);
            IsoConvertPane3.disableProperty().set(true);
        }
    }

    @FXML
    void DBConect(MouseEvent event) throws SQLException {
        String url= "jdbc:sqlserver://localhost:1433";
        String user =username.getText() ;
        String pw = password.getText();
        try {
        Connection con = DriverManager.getConnection(url, user, pw);
        if (con != null) {
            JOptionPane.showMessageDialog(null, "DB Connection is OK", "", JOptionPane.INFORMATION_MESSAGE);
            con.close();
        }
        else
            JOptionPane.showMessageDialog(null, "NO DB Connection!!!", "", JOptionPane.INFORMATION_MESSAGE);
            Connection conn = DriverManager.getConnection(url, user, pw);
            String sql;
            switch (sql = "SELECT * FROM [alzahra].[dbo].["+ tablename.getText()+"]") {
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String owner = rs.getString("doc");
                System.out.printf(owner);
            }
            rs.close();
            stmt.close();
            con.close();

        } catch (Exception ee) {
            JOptionPane.showMessageDialog(null, ee.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    @FXML
    void DBConnection(MouseEvent event) {
        Pane4.visibleProperty().set(true);
        Pane4.disableProperty().set(false);
        IsoConvertPane1.visibleProperty().set(false);
        IsoConvertPane1.disableProperty().set(true);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Controller.counter_recordPublic = counter_record;
        Controller.counters_public = counters;
        Controller.counter_recordPublic2 = counter_record2;
    }


}

