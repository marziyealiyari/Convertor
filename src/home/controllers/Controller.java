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
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.converter.impl.AnselToUnicode;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.ControlFieldImpl;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.List;

import static Utilities.Utilities.*;

public class Controller implements Initializable {


    @FXML
    private static TextField counter_recordPublic, counter_recordPublic2;
    @FXML
    public static int counters_public;
    @FXML
    public static boolean check = true;
    @FXML
    public TextField txtInputFile, txtMappingPath, centerName, txtMappingPathsim, centerNamesim;
    @FXML
    public TextField counter_record, counter_record2;
    public TextField username, password, tablename, mapp;
    @FXML
    public HBox Type, Typesim;
    @FXML
    public RadioButton simorgh, digilib;
    @FXML
    private int counters;
    public String labelString;
    @FXML
    private Pane IsoConvertPane1, IsoConvertPane2, IsoConvertPane3, Pane4, Pane5;
    @FXML
    private Button digiCancel, simorghCancel, btnConvertSim;
    @FXML
    private ComboBox<Object> types;
    @FXML
    private ComboBox typesmap;


    public Controller() {
    }

    public static void Convert(String inputPath, String mappingPath, List<String> docType, String centerName, String counter, String isotype) {
        BufferedReader br = null;
        String item = docType.get(0);
        List<Record> convertedList;
        OutputStreamWriter writer = null;
        String outputPath = null;
        try {
            //  for (String items : docType)
            writer = new OutputStreamWriter(new FileOutputStream(inputPath.substring(0, inputPath.lastIndexOf('\\') + 1).concat(item.concat("_Converted.prs"))));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String ref;
        String iso, file_out;
        counters_public = 0;
        if (Objects.equals(isotype, "digilib")) {
            try {
                assert br != null;
                while (br.ready()) {
                    convertedList = ConvertByMappingFile(DigilibIso.getInstance(item, br).getIsoRecords(), getMapping(mappingPath, item));
                    file_out = "";
                    for (Record record : convertedList) {
                        iso = makeIso(record, docType, centerName, isotype);
                        file_out = file_out + iso;
                    }
                    writer.write(file_out);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(isotype, "simorgh")) {
            check = false;
            try {
                assert br != null;
                while (br.ready()) {
                    convertedList = ConvertByMappingFile(Nosa.getInstance(item, br).getIsoRecords(), getMapping(mappingPath, item));
                    file_out = "";
                    for (Record record : convertedList) {
                        iso = makeIso(record, docType, centerName, isotype);
                        file_out = file_out + iso;
                    }
                    writer.write(file_out);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String makeIso(Record record, List<String> docType, String centerName, String isoType) {
        String iso, tag, length, offset, data, data2 = "", temp, simStr;
        referenced = "";
        String newStr;
        byte[] bytes2;
        int L, count = 0;
        StringBuilder newtemp = new StringBuilder();
        for (DataField df : record.getDataFields()) {
            tag = df.toString().substring(0, 3);
            data = df.toString().substring(4);
            List<Subfield> subfields = df.getSubfields();
            for (Object subfield1 : subfields) {
                Subfield subfield = (Subfield) subfield1;
                char code = subfield.getCode();
                String data1 = subfield.getData();

                if (Objects.equals(isoType, "digilib")) {
                    // مرجع
                    if (!record.toString().contains("è\u200F¤")) {
                        referenced = "ô";
                    }
                    //مرجع
//check farsi number & center
                    if (docType.get(0).charAt(1) == 'F') {
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


                if (Objects.equals(isoType, "simorgh")) {
                    if ((tag.equals("915")) || (tag.equals("200")) & subfield.getCode() == 'b' || (tag.equals("801") && subfield.getCode() == 'b')) {
                        if (tag.equals("801")) data1 = centerName;
                        String oldStr = data1;
                        byte[] bytes = oldStr.getBytes(StandardCharsets.UTF_8);
                        newStr = new String(bytes);
                        sign = new StringBuilder(newStr);
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
                        sign = new StringBuilder(newStr);
                    }
                }

                //Digilib
                if (Objects.equals(isoType, "digilib")) {
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
                        switch (data1) {
                            case "\u200C‘•\u200E":
                                sign.replace(0, data1.length(), "p");
                                break;
                            case "\u200C‘•\u200E - ‘َî—¤ّ÷\u200Fî\u200E":
                                sign.replace(0, data1.length(), "pe");
                                break;
                            case "‘َî—¤ّ÷\u200Fî\u200E":
                                sign.replace(0, data1.length(), "e");
                                break;
                            case "‘َî—¤ّ÷\u200Fî\u200E - َّ\u200D êھ¤¢ù":
                                sign.replace(0, data1.length(), "ec");
                                break;
                            case "\u200C‘•\u200E - َّ\u200D êھ¤¢ù":
                                sign.replace(0, data1.length(), "pc");
                                break;
                            default:
                                sign.replace(0, data1.length(), "p");
                                break;
                        }
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
        if (Objects.equals(isoType, "digilib")) {
            counters_public++;
            counter_recordPublic.setText(String.valueOf(counters_public));
        } else if (Objects.equals(isoType, "simorgh")) {
            counters_public++;
            counter_recordPublic2.setText(String.valueOf(counters_public));

        }


        return iso;

    }


    @FXML


    public ComboBox<Object> getTypes() {
        return types;
    }

    @FXML
    void btnConvertIsoHandler(MouseEvent mouseEvent) {

    }

    @FXML
    void btnExitHandler(MouseEvent mouseEvent) {
        //  System.exit(0);
        Runtime.getRuntime().halt(0);
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
    public void btnIsoInputFileHandler1(MouseEvent mouseEvent) {
        mapp.setText(Utilities.InputDialog("TEXT files (*.txt)", "*.txt;*.iso;*.prs").getPath());
    }

    @FXML
    public void btnNextIsoConvert(MouseEvent mouseEvent) {
        Form form = new Form();
        if (form.checkForm(txtInputFile)) {
            if (digilib.isSelected()) {
                String[][] typeArray = DigilibIso.findAllTypes(txtInputFile.getText());
                List<String> typeList = new ArrayList<>();
                for (int i = 0; i < typeArray[1].length; i++)
                    if (typeArray[1][i].equals("1")) {
                        if (Type.getChildren().size() > 0)
                            Type.getChildren().remove(0);
                        Type.getChildren().add(new CheckBox(typeArray[0][i]));
                    }
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
        List<String> typeList = new ArrayList<>();
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
    void btnCancelDB(MouseEvent event) {
        Pane4.visibleProperty().set(false);
        Pane4.disableProperty().set(true);
        IsoConvertPane1.visibleProperty().set(true);
        IsoConvertPane1.disableProperty().set(false);
    }

    @FXML
    void DBConect(MouseEvent event) throws SQLException {
        String url = "jdbc:sqlserver://localhost:1433";
        String user = username.getText();
        String pw = password.getText();
        try {
            Connection con = DriverManager.getConnection(url, user, pw);
            if (con != null) {
                JOptionPane.showMessageDialog(null, "DB Connection is OK", "", JOptionPane.INFORMATION_MESSAGE);
                con.close();
            } else
                JOptionPane.showMessageDialog(null, "NO DB Connection!!!", "", JOptionPane.INFORMATION_MESSAGE);
            Connection conn = DriverManager.getConnection(url, user, pw);
            String sql;
            switch (sql = "SELECT * FROM [alzahra].[dbo].[" + tablename.getText() + "]") {
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String owner = rs.getString("doc");
                System.out.printf(owner);
            }
            rs.close();
            stmt.close();
            assert con != null;
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
        IsoConvertPane2.visibleProperty().set(false);
        IsoConvertPane2.disableProperty().set(true);
        IsoConvertPane3.visibleProperty().set(false);
        IsoConvertPane3.disableProperty().set(true);
        Pane5.visibleProperty().set(false);
        Pane5.disableProperty().set(true);

    }

    @FXML
    void mapping(MouseEvent event) {
        Pane5.visibleProperty().set(true);
        Pane5.disableProperty().set(false);
        IsoConvertPane1.visibleProperty().set(false);
        IsoConvertPane1.disableProperty().set(true);
        IsoConvertPane2.visibleProperty().set(false);
        IsoConvertPane2.disableProperty().set(true);
        IsoConvertPane3.visibleProperty().set(false);
        IsoConvertPane3.disableProperty().set(true);
        Pane4.visibleProperty().set(false);
        Pane4.disableProperty().set(true);
    }

    @FXML
    void prelevelmap() {
        Pane5.visibleProperty().set(false);
        Pane5.disableProperty().set(true);
        IsoConvertPane1.visibleProperty().set(true);
        IsoConvertPane1.disableProperty().set(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Controller.counter_recordPublic = counter_record;
        Controller.counters_public = counters;
        Controller.counter_recordPublic2 = counter_record2;
    }

    @FXML
    public void makemapping() throws IOException {
        try {
            InputStream input = new FileInputStream(mapp.getText());
            OutputStream out = new FileOutputStream(mapp.getText().replace(mapp.getText().substring(mapp.getText().lastIndexOf('.'), mapp.getText().length()), ".xml"));
            MarcReader reader = new MarcStreamReader(input);
            MarcWriter writer = new MarcXmlWriter(out, true);
            AnselToUnicode converter = new AnselToUnicode();
            writer.setConverter(converter);
            while (reader.hasNext()) {
                Record record = reader.next();
                for (DataField df : record.getDataFields()) {
                    for (Subfield d : df.getSubfields()) {
                        d.setData(d.getData().replace(d.getData(), "#Field^SubField#"));
                    }
                }
                record.addVariableField(new ControlFieldImpl("001",typesmap.getValue().toString()));
                writer.write(record);
            }
            writer.close();
            out.close();
            JOptionPane.showMessageDialog(null, "Done", "", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception c) {
            System.out.println(c.getMessage());
        }
    }
}


