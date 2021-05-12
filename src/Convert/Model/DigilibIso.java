package Convert.Model;

import Convert.IsoIF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DigilibIso extends Iso implements IsoIF {


    private static IsoIF instance = null;
    private List<IsoRecord> isoRecords = null;
    public static  String countPlus;

    //Cunstructors
    private DigilibIso() {
    }

    private DigilibIso(File input) {
        this.setInputFile(input);
        try {
            isoRecords = read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public DigilibIso(File input, String docType) {
        this.setInputFile(input);
        try {
            isoRecords = read(docType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static DigilibIso getInstance(File input) {
        if (instance == null)
            instance = new DigilibIso(input);
        return (DigilibIso) instance;
    }

    public static DigilibIso getInstance(File input, String docType) {
        if (instance == null)
            instance = new DigilibIso(input, docType);
        return (DigilibIso) instance;
    }

    public static boolean checkIso(String item) {
        int i, recordLength, isolength, isoDir;
        i = item.indexOf("#");
        isolength = Integer.parseInt(item.substring(1, 5));
        recordLength = item.length();
        if (item.substring(20, 22).equals("45"))
            if (isolength == recordLength)
                return true;
        return false;
    }

    public static String[][] findAllTypes(String path) {
        String data = "";
        String data1 = "";
        File iso = new File(path);
        boolean flag = false;

        String[][] typeList = {{"BF", "BL", "AF", "AL", "TF", "TL", "PF", "PL", "VF", "VL"}
                , {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"}};
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(iso)));
            data = br.readLine();
//            data1 = br.readLine();
            while (data != null) {
                for (int i = 0; i < typeList[1].length; i++)
                    if ((data1.indexOf("#}".concat(typeList[0][i]).concat("{#")) >= 0 || data1.indexOf("#{".concat(typeList[0][i]).concat("}#")) >= 0) && typeList[1][i] != "1") {
                        typeList[1][i] = "1";
                        flag = true;
                    }
                if (flag == true) break;
                data = br.readLine();
                data1 = data1 + data;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeList;
    }

//    public static void Convert(String inputPath, String mappingPath, List<String> docType, String centerName, String counter) {
//        List<Record> convertedList;
//        FileOutputStream writer = null;
//        String outputPath;
//        String ref;
//        String iso = "", file_out = "";
//        for (String item : docType) {
//            //int j=inputPath.lastIndexOf('\\');
//            //outputPath=inputPath.substring(1, inputPath.lastIndexOf('\\'));
//            outputPath = inputPath.substring(0, inputPath.lastIndexOf('\\') + 1).concat(item.concat("_Converted.prs"));
//            convertedList = ConvertByMappingFile(DigilibIso.getInstance(new File(inputPath), item).getIsoRecords(), getMapping(mappingPath, item));
//
//            for (Record record : convertedList) {
//                iso = makeIso(record, docType, centerName, counter);
//                file_out = file_out + iso;
//
//                //
//
//
//
//
////
//            }
//
//
//            try {
//                writer = new FileOutputStream(outputPath);
//
//                if (new File(outputPath).exists())
//                    writer.write(file_out.toString().getBytes());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

//    public static String makeIso(Record record, List docType, String centerName, String counter) {
//        String iso = "", tag = "", length = "", offset = "", data = "", data2 = "", temp = "";
//        referenced = "";
//        int L = 0, count = 0;
//        StringBuilder newtemp = new StringBuilder();
//        for (DataField df : record.getDataFields()) {
//            tag = df.toString().substring(0, 3);
//            data = df.toString().substring(4);
//            DataField field = df;
//            List subfields = field.getSubfields();
//            Iterator i = subfields.iterator();
//            while (i.hasNext()) {
//                Subfield subfield = (Subfield) i.next();
//                char code = subfield.getCode();
//                String data1 = subfield.getData();
//
//
//                // مرجع
//                if (!record.toString().contains("è\u200F¤")) {
//                    referenced = "ô";
//                }
//                //مرجع
//
//
////check farsi number & center
//
//                if (docType.get(0).toString().charAt(1) == 'F') {
//                    StringBuilder checkFarsi = new StringBuilder(data1);
//                    if (!tag.equals("100")) {
//                        changeLatinToFarsi(checkFarsi);
//                    }
//                    data1 = checkFarsi.toString();
//                }
//                if ((tag.equals("801") && subfield.getCode() == 'b')) {
//                    data1 = centerName;
//                    StringBuilder centerNames = new StringBuilder(data1);
//                    changeFarsiString(centerNames);
//                    data1 = centerNames.toString();
//                }
//                if (tag.equals("200") && subfield.getCode() == 'b' || (tag.equals("915") && subfield.getCode() == 'a')) {
//                    StringBuilder parsfix = new StringBuilder(data1);
//                    changeFarsiString(parsfix);
//                    data1 = parsfix.toString();
//                }
//
////check farsi number & center
//                //marking
//                StringBuilder sign = new StringBuilder(data1);
//                if (tag.equals("200") && subfield.getCode() == 'f')
//                    if (data1.indexOf(0) != '/') sign.insert(0, "/ ");
//                if (tag.equals("200") && subfield.getCode() == 'g')
//                    if (data1.indexOf(0) != 'ف') sign.insert(0, "ف ");
//                if (tag.equals("200") && subfield.getCode() == 'e')
//                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
//                if (tag.equals("210") && subfield.getCode() == 'c')
//                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
//                if (tag.equals("210") && subfield.getCode() == 'd')
//                    if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
//                if (tag.equals("215") && subfield.getCode() == 'c')
//                    if (data1.indexOf(0) != ':') sign.insert(0, ": ");
//                if (tag.equals("225") && subfield.getCode() == 'v') {
//                    if (textLanguage(sign).contains("F"))
//                        if (data1.indexOf(0) != 'ف') sign.insert(0, "ف ");
//                    if (textLanguage(sign).contains("L"))
//                        if (data1.indexOf(0) != ';') sign.insert(0, "; ");
//                }
//                if (tag.equals("700") && subfield.getCode() == 'f')
//                    if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
//                if (tag.equals("700") && subfield.getCode() == '4')
//                    if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
//                if (tag.equals("702") && subfield.getCode() == 'f')
//                    if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
//                if (tag.equals("702") && subfield.getCode() == '4')
//                    if (data1.indexOf(0) != 'ٹ') sign.insert(0, "ٹ ");
//                if (tag.equals("215") && subfield.getCode() == 'a')
//                    if (data1.contains(":")) sign.insert(sign.indexOf(":"), "\u001Fc");
//                //marking
//
//                //chap
//                if (tag.equals("923") && subfield.getCode() == 'a') {
//                    if (data1.equals("\u200C‘•\u200E")) sign.replace(0, data1.length(), "p");
//                    else if (data1.equals("\u200C‘•\u200E - ‘َî—¤ّ÷\u200Fî\u200E"))
//                        sign.replace(0, data1.length(), "pe");
//                    else if (data1.equals("‘َî—¤ّ÷\u200Fî\u200E")) sign.replace(0, data1.length(), "e");
//                    else if (data1.equals("‘َî—¤ّ÷\u200Fî\u200E - َّ\u200D êھ¤¢ù"))
//                        sign.replace(0, data1.length(), "ec");
//                    else if (data1.equals("\u200C‘•\u200E - َّ\u200D êھ¤¢ù")) sign.replace(0, data1.length(), "pc");
//                    else sign.replace(0, data1.length(), "p");
//                }
//                //chap
//
//                // acquisition
//                if (tag.equals("999") && subfield.getCode() == 'd') {
//                    if (data1.contains("،¤\u200F¢گ¤\u200E") || data1.contains("پ"))   //1
//                        sign.replace(0, data1.length(), "a\u001Fd0a\u001Fd1a\u001Fd2a");
//                    else if (data1.contains("\u0090û¢\u0090þü") || data1.contains("‚"))   //2
//                        sign.replace(0, data1.length(), "b\u001Fd0a\u001Fd1a\u001Fd2a");
//                    else if (data1.contains("øìêü") || data1.contains("ƒ"))   //3
//                        sign.replace(0, data1.length(), "j\u001Fd0a\u001Fd1a\u001Fd2a");
//                    else if (data1.contains("—øóþ¢") || data1.contains("„"))   //4
//                        sign.replace(0, data1.length(), "i\u001Fd0a\u001Fd1a\u001Fd2a");
//                    else if (data1.contains("…"))   //5
//                        sign.replace(0, data1.length(), "z\u001Fd0a\u001Fd1a\u001Fd2a");
//                    else sign.replace(0, data1.length(), "a\u001Fd0a\u001Fd1a\u001Fd2a");
//                }
//                // acquisition
//
//                //reference
//                if (tag.equals("999") && subfield.getCode() == 'a') {
//                    if (data1.contains("è\u200F¤ُ¤›â"))   //غیرمرجع
//                        sign.replace(0, data1.length(), "1");
//                    else {
//                        sign.replace(0, data1.length(), "0");
//                    }
//                }
//
//
//                if ((tag.equals("680") && subfield.getCode() == 'b') || (tag.equals("676") && subfield.getCode() == 'b') || (tag.equals("686") && subfield.getCode() == 'b')) {
//                    if (!data1.equals(""))
//                        if (referenced.equals("ô")) {
//                            sign.insert(sign.length(), "\u001F9" + referenced);
//                        }
//
//                }
//                if ((tag.equals("680") && subfields.size() == 1 && subfield.getCode() == 'a') || (tag.equals("676") && subfields.size() == 1 && subfield.getCode() == 'a') || (tag.equals("686") && subfields.size() == 1 && subfield.getCode() == 'a')) {
//                    if (!data1.equals(""))
//                        if (referenced.equals("ô")) {
//                            sign.insert(sign.length(), "\u001F9" + referenced);
//                        }
//
//                }
//
//
//                //reference
//
//
//                if (tag.equals("410") && subfield.getCode() == 't') {
//                    if (data1.indexOf("ف") > 0) {
//                        sign.insert(data1.indexOf("ف"), "\u001Fv");
//                        sign.insert(data1.indexOf("ف") + 3, " ");
//
//                    } else if (data1.indexOf(";") > 0) {
//                        sign.insert(data1.indexOf(";"), "\u001Fv");
//                        sign.insert(data1.indexOf(";") + 3, " ");
//
//                    }
//                }
//
//
//                data1 = sign.toString();
//                data2 = data2 + "\u001F" + code + data1;
//            }
//            //subfield
//            data = "  " + data2;
//            data2 = "";
//            length = String.valueOf(data.length());
//            while (length.length() < 4) {
//                length = "0" + length;
//            }
//            L = newtemp.length();
//            if (L > 0) {
//                L = L - count * 12;
//
//            } else {
//                L = 0;
//            }
//            offset = String.valueOf(L);
//            while (offset.length() < 5) {
//                offset = "0" + offset;
//
//            }
//
//            temp = tag + length + offset;
//            newtemp.insert(count * 12, temp);
//            newtemp.insert(newtemp.length(), data);
//            count++;
//
//        }
//        iso = newtemp.toString() + "\u001E\u001D";
//        length = String.valueOf(iso.length() + 24);
//        while (length.length() < 5) {
//            length = "0" + length;
//        }
//        L = count * 12 + 25;
//        offset = String.valueOf(L);
//        while (offset.length() < 4) {
//            offset = "0" + offset;
//
//        }
//        iso = length + "nam00220" + offset + "1n04500" + iso;
//             countPlus=String.valueOf(Integer.valueOf(counter)+1);
//
//        return iso;
//    }


    //Getter and Setter
    public List<IsoRecord> getIsoRecords() {
        return isoRecords;
    }

    public void setIsoRecords(List<IsoRecord> isoRecords) {
        this.isoRecords = isoRecords;
    }

    //Methodes
    @Override
    public List read() throws FileNotFoundException {
        return read("");
    }

    public List read(String docType) throws FileNotFoundException {
        String data = "";
        File iso = this.getInputFile();
        List<IsoRecord> isoList = new ArrayList<IsoRecord>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(iso)));
            do {
                do
                    data = data.concat(br.readLine());

                while (!(data.endsWith("##")));
                if (docType.equals(""))
                    isoList.add(new IsoRecord(data));
                else if (data.indexOf("#}".concat(docType).concat("{#")) > 0 || data.indexOf("#{".concat(docType).concat("}#")) > 0)
                    isoList.add(new IsoRecord(data));
                data = br.readLine();
            } while (data != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isoList;
    }

    @Override
    public void write() {

    }


}
