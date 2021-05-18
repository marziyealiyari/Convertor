package Utilities;

import Convert.Model.IsoField;
import Convert.Model.IsoRecord;
import Convert.Model.IsoSubField;
import javafx.stage.FileChooser;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.impl.ControlFieldImpl;
import org.marc4j.marc.impl.DataFieldImpl;
import org.marc4j.marc.impl.RecordImpl;
import org.marc4j.marc.impl.SubfieldImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static home.controllers.Controller.check;

public class Utilities {
    public static String referenced = "";


    public static File InputDialog(String fileType, String extension) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(fileType, extension);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(null);
    }

    public static List<Record> ConvertByMappingFile(List<IsoRecord> isoRecords, Record mappingRecord) {
        List<DataField> tempDatafield;
        List<DataField> tempDatafield2;
        List<Record> convertedList = new ArrayList<>();
        Record tempRecord;
        for (IsoRecord record : isoRecords) {
            tempRecord = new RecordImpl();
            tempRecord.setLeader(mappingRecord.getLeader());
            for (ControlField cf : mappingRecord.getControlFields()) {
                ControlField temp = new ControlFieldImpl(cf.getTag(), cf.getData());
                tempRecord.addVariableField(temp);
            }
            for (int i = 0; i < record.getStr_page().size(); i++) {
                tempDatafield = getMapping(record.getStr_page().get(i), mappingRecord);
                tempDatafield = fill(tempDatafield, record.getStr_page().get(i));
                for (DataField DF : tempDatafield) {
                    if (DF.getIndicator1() == '0' && fieldFind(tempRecord, DF.getTag())) {
                        for (DataField df : tempRecord.getDataFields()) {
                            if (df.getTag().equals(DF.getTag())) {
                                for (Subfield sf : DF.getSubfields()) {
                                    if (sf.getId() != null) {
                                        for (int x = 0; x < df.getSubfields(sf.getCode()).size(); x++) {
                                            if (df.getSubfields(sf.getCode()).get(x).getId() == null) {
                                                df.getSubfields(sf.getCode()).get(x).setData(sf.getData());
                                                df.getSubfields(sf.getCode()).get(x).setId((long) 1);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        tempRecord.addVariableField(DF);

                    }
                }
            }
            tempDatafield2 = fixed(mappingRecord);
            for (DataField DF : tempDatafield2) {
                tempRecord.addVariableField(DF);

            }


            removeEmptySubfield(tempRecord);
          if (check) checkData(tempRecord);
            convertedList.add(tempRecord);
        }
        return convertedList;
    }

    private static void checkData(Record tempRecord) {
        for (DataField df : tempRecord.getDataFields()) {
            for (Subfield sf : df.getSubfields()) {
                brev_eng(sf, tempRecord.getControlFields().get(0).getData().charAt(1));

            }
        }
    }

    private static void brev_eng(Subfield sf, char c) {
        StringBuilder sb = new StringBuilder(sf.getData());
        reverseFarsiNum(sb);
        if (textLanguage(sb).contains("FT") || textLanguage(sb).contains("FN")) {
            String delimiter1 = "()[]<>";
            String delimiter2 = ")(][><";
            reverseFarsi(sb, c);
            sb.reverse();
            sb = new StringBuilder(sb.toString().replace("^a^a", "^a"));
            sb = new StringBuilder(sb.toString().replace("^b^b", "^b"));
            sb = new StringBuilder(sb.toString().replace("^c^c", "^c"));
            sb = new StringBuilder(sb.toString().replace("^d^d", "^d"));
            sb = new StringBuilder(sb.toString().replace("^a^", "^"));
            sb = new StringBuilder(sb.toString().replace("^b^", "^b"));
            sb = new StringBuilder(sb.toString().replace("^c^", "^c"));
            sb = new StringBuilder(sb.toString().replace("^d^", "^d"));
            sb = new StringBuilder(sb.toString().replace("½", "ø"));
            sb = new StringBuilder(sb.toString().replace("  ", " "));
            sb = new StringBuilder(sb.toString().replace("{{", "{"));
            sb = new StringBuilder(sb.toString().replace("} {", " "));
            sb = new StringBuilder(sb.toString().replace("}{", ""));
            sb = new StringBuilder(sb.toString().replace("}}", "}"));
            sb = new StringBuilder(sb.toString().replace("} ({", " ("));
            sb = new StringBuilder(sb.toString().replace("}( {", "( "));
            sb = new StringBuilder(sb.toString().replace("}(({", "("));
            sb = new StringBuilder(sb.toString().replace("}[({", "(["));
            sb = new StringBuilder(sb.toString().replace("}[/{", "/["));
            sb = new StringBuilder(sb.toString().replace("}- [", "[ -"));
            sb = new StringBuilder(sb.toString().replace("}-[{", "[-"));
            sb = new StringBuilder(sb.toString().replace("}. ({", "(."));
            sb = new StringBuilder(sb.toString().replace("\u00AD", " ")); //char255
            sb = new StringBuilder(sb.toString().replace("}( /{", "/ ("));
            sb = new StringBuilder(sb.toString().replace("ß", "˜"));
            sb = new StringBuilder(sb.toString().replace("Š ] ", "Š ]"));
            sb = new StringBuilder(sb.toString().trim());
            //reverseLatin(sb);
            //reverseFarsiNum(sb);
            for (int i = 1; i < sb.length(); i++) {
                sb.replace(i, i + 1, Character.toString((delimiter1.indexOf(sb.charAt(i)) > -1) ? delimiter2.charAt(delimiter1.indexOf(sb.charAt(i))) : sb.charAt(i)));
            }
            int j = sb.indexOf("\u000E");  //char 16
            while (j > 1) {
                int k = sb.indexOf("\u0010");  //char 17
                for (int i = j; i <= k; i++) {
                    sb.replace(j, k, Character.toString((delimiter1.indexOf(sb.charAt(i)) > 0) ? delimiter2.charAt(delimiter1.indexOf(sb.charAt(i))) : sb.charAt(i)));
                }
                sb.delete(k, k + 1);
                sb.delete(j, j + 1);
                j = sb.indexOf("\u000E");  //char 16
            }
            sb = new StringBuilder(sb.toString().replace("\u000E", "")); //char 16
            sb = new StringBuilder(sb.toString().replace("\u0010", "")); //char 17
            sb = new StringBuilder(sb.toString().replace("ÖÕ", ""));
            sb = new StringBuilder(sb.toString().replace("Ö Õ", ""));
            sb = new StringBuilder(sb.toString().replace("ÕÕ", "Õ"));
            sb = new StringBuilder(sb.toString().replace("ÖÖ", "Ö"));
            sb = new StringBuilder(sb.toString().replace("ÖÛÕ", ""));
            sb = new StringBuilder(sb.toString().replace("ÛÕÛÕ", "ÛÕ"));
            sb = new StringBuilder(sb.toString().replace("\u001D\u001E", ""));
        } else {
            if ((sb.indexOf("}") >= 0) & (sb.indexOf("{") >= 0)) {
                sb.delete(sb.lastIndexOf("{"), sb.lastIndexOf("{") + 1);
                sb.delete(sb.indexOf("}"), sb.indexOf("}") + 1);
            }
//            changeLatinToFarsi(sb);

        }
        sf.setData(sb.toString());
    }

//    private static void reverseLatin(StringBuilder sb) {
//        String delimiter1="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        String delimiter2=" ()[]<>,./-:0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        String delimiter3=".<[()]>0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        String delimiter6="([<";
//        if (!sb.toString().equals(""))
//        {
//        int j=0;
//        while (j<sb.length()){
//            while (j < sb.length() && delimiter1.indexOf(sb.charAt(j))>0) ++j;
//            if (j > 0 && (sb.charAt(j-1)=='^' || sb.charAt(j-1) == '\u0019')) ++j;
//            if (j < sb.length() && delimiter1.indexOf(sb.charAt(j))>0)
//            {                                            //
//                if (j > 1 && (sb.charAt(j-1)==')' || sb.charAt(j-1)== ']')) --j;
//                int i=j;
//                int k=(int)sb.charAt(i);
//                while ((i < sb.length() && sb.charAt(i + 1)!='^' && (int)sb.charAt(i) > 31 && (int)sb.charAt(i) < 129) || (int)sb.charAt(i) == 214) ++i;
//                //while (i>j) and (pos(st[i],delimiter3)=0) do dec(i);
//                if ((i > j) && (pos(st[i], delimiter6) > 0)) then dec (i);
//                while (st[i] = ']') and(position(st, '[', i + 1) > 0) do dec(i);
//            while (st[i] = ')') and(position(st, '(', i + 1) > 0) do dec(i);
//            while (st[i] = '>') and(position(st, '<', i + 1) > 0) do dec(i);
//            while (st <>'')and(st[i] = ' ') do dec(i);
//                //if (i<length(st)) and ((st[i+1]='(') or (st[i+1]='[')) Then inc(i);
//                if (i < length(st) - 1) and(st[i + 1] = ' ') and((st[i + 2] = '(')or(st[i + 2] = '[')) Then inc (i, 2);
//                while (i > j) and(st[i] < > '(')and(st[i] < > '[')and((st[i - 1] = ' ')or(st[i] = ':')or(st[i] = ']'))
//                or
//                        ((ord(st[i]) < 33)or(ord(st[i]) > 128)) do dec(i);
//                if (i > j) and(st[i] = ' ') Then dec (i);
//                if (i > j) and(st[i] = '-') Then dec (i);
//                if (i = Length(st) - 1) and(st[i + 1] = '/') then inc (i);
//                st_temp:='';
//                if i >= j Then
//                {
//                    for L:=j to i do st_temp:=st[L] + st_temp;
//                    //delimiter4:='()[]<>';
//                    //delimiter5:=')(][><';
//                    //for m:=1 to length(st_temp) do if Pos(st_temp[m],delimiter4)>0 then st_temp[m]:=delimiter5[Pos(st_temp[m],delimiter4)];
//                    delete(st, j, i - j + 1);
//                    insert(chr(16) + chr(219) + chr(213) + st_temp + chr(214) + chr(17), st, j);
//                    j:=i + 6;
//                }
//                Else inc (j);
//            }
//        }
//            }
//    }

    private static void reverseFarsi(StringBuilder sb, char c) {
        StringBuilder temp = new StringBuilder();
        int j = (c == 'F') ? sb.indexOf("}") : sb.indexOf("{");
        int k = (c == 'F') ? sb.indexOf("{") : sb.indexOf("}");
        if (j > 0 && k > -1) {
            temp.delete(0, temp.length());
            while (j < k && k > -1) {
                if (j > 1) {
                    if (!temp.toString().equals(""))
                        temp = new StringBuilder(sb.substring(0, j)).reverse().insert(j, temp);
                    else
                        temp = new StringBuilder(sb.substring(0, j)).reverse();
                }
                String st_temp = sb.substring(j + 1, k);
                sb.delete(0, k + 1);
                if (temp.toString().equals(""))
                    temp = new StringBuilder(st_temp);
                else temp.insert(0, st_temp);
                j = (c == 'F') ? sb.indexOf("}") : sb.indexOf("{");
                k = (c == 'F') ? sb.indexOf("{") : sb.indexOf("}");
            }
            if (!sb.toString().equals("")) {
                temp.insert(0, sb.reverse());
                sb.delete(0, sb.length());
            }
            sb.insert(0, temp);
        } else sb.reverse();
//        sb.insert(0,temp);
    }

    public static String textLanguage(StringBuilder sb) {
        String farsiNum = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089";
        String latinNum = "0123456789";
        String farsiChar = "\u008D\u0090\u0093\u0095\u0097\u0099\u009B\u009D\u009F¡¢£¤¥¦¨ª¬®¯àãçêìîðóõ÷øúùþ\u008E\u0091\u0092\u0094\u0096\u0098\u009A\u009C\u009E §©«\u00ADäèáåâæéëíïñôöûýüò";
        String latinChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String type = "";
        for (int i = 0; i < sb.length(); i++) {
            //String a=Character.toString(sb.charAt(i));
            //int k=latinChar.indexOf(a);
            //if(k>0)
            //    a=a;
            type = type.concat((!type.contains("FT") && farsiChar.contains(Character.toString(sb.charAt(i)))) ? "FT/" : "");
            type = type.concat((!type.contains("FN") && farsiNum.contains(Character.toString(sb.charAt(i)))) ? "FN/" : "");
            type = type.concat((!type.contains("LT") && latinChar.contains(Character.toString(sb.charAt(i)))) ? "LT/" : "");
            type = type.concat((!type.contains("LN") && latinNum.contains(Character.toString(sb.charAt(i)))) ? "LN/" : "");
        }
        return type;
    }

    private static void reverseFarsiNum(StringBuilder sb) {
        int i = 0;
        int farsi[] = {8364, 1662, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 47, 1657};
        StringBuilder temp1;


        while (i < sb.length()) {
            while (i < sb.length()) {
                char characters = sb.charAt(i);
                int ascii = (int) characters;
                if (!contains(farsi, ascii)) i++;
                else break;
            }
            int j = i;
            while (j < sb.length()) {
                char characters2 = sb.charAt(j);
                int ascii2 = (int) characters2;
                if (contains(farsi, ascii2)) j++;
                else break;
            }
            if (i >= 0 & j > 0 & j > i + 1) {
                temp1 = new StringBuilder(sb.substring(i, j)).reverse();
                sb.replace(i, j, temp1.toString());
                i = j;
            }
            i++;
        }
    }

    public static void changeLatinToFarsi(StringBuilder sb) {
        int num[][] = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, {8364, 1662, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240}};
        for (int i = 0; i < sb.length(); i++) {
            if (contains(num[0], Character.getNumericValue(sb.charAt(i)))) {
                int characters = num[1][Character.getNumericValue(sb.charAt(i))];
                char ascii = (char) characters;
                sb.replace(i, i + 1, String.valueOf(ascii));
            }
        }
    }

    //0 = 'آ' 1570
//        1 = 'ب' 1576
//        2 = 'پ' 1662
//        3 = 'ت' 1578
//        4 = 'ث' 1579
//        5 = 'ج' 1580
//        6 = 'چ' 1670
//        7 = 'ح' 1581
//        8 = 'خ' 1582
//        9 = 'د' 1583
//        10 = 'ذ' 1584
//        11 = 'ر' 1585
//        12 = 'ز' 1586
//        13 = 'ژ' 1688
//        14 = 'س' 1587
//        15 = 'ش' 1588
//        16 = 'ص' 1589
//        17 = 'ض' 1590
//        18 = 'ط' 1591
//        19 = 'ظ' 1592
//        20 = 'ع' 1593
//        21 = 'غ' 1594
//        22 = 'ف' 1601
//        23 = 'ق' 1602
//        24 = 'ک' 1705
//        25 = 'گ' 1711
//        26 = 'ل' 1604
//        27 = 'م' 1605
//        28 = 'ن' 1606
//        29 = 'و' 1608
//        30 = 'ه' 1607
//        31 = 'ي' 1740
    public static void changeFarsiString(StringBuilder sb) {
        int farsi[][] = new int[][]{
                {1575, 1570, 1576, 1662, 1578, 1579, 1580, 1670, 1581, 1582, 1583, 1584, 1585, 1586, 1688, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1601, 1602, 1708, 1604, 1605, 1608, 1607, 1740, 1575, 1711, 1705, 1606},
         /*separate*/{1711, 1670, 8217, 8221, 8211, 1705, 1681, 339, 8205, 160, 162, 163, 164, 165, 166, 167, 169, 171, 173, 175, 224, 1606, 1607, 233, 235, 1610, 1612, 244, 1617, 249, 8206, 1711, 239, 1610, 1616},
         /*Sticky*/  {8216, 8216, 8220, 8226, 8212, 8482, 8250, 8204, 1722, 1548, 162, 163, 164, 165, 166, 168, 1726, 172, 174, 175, 224, 1605, 231, 234, 1609, 238, 1614, 1615, 1617, 1618, 8207, 1711, 1611, 238, 247}


        };
        String parantez = "[]{}()<>";
        int x = 0;
        int y;
        int charnump;
        for (int i = 0; i < sb.length(); i++) {
            char charutf = (sb.charAt(i));
            int charnum = (int) charutf;
            if (contains(farsi[0], charnum)) {
                for (y = 0; y < farsi[0].length; y++) {

                    if (farsi[0][y] == charnum) {
                        if (i < sb.length() - 1) {
                            if (sb.charAt(i + 1) == ' ' || parantez.contains(Character.toString(sb.charAt(i + 1))))
                                charnump = farsi[1][y];
                            else charnump = farsi[2][y];
                        } else {
                            charnump = farsi[1][y];

                        }
                        char charutfp = (char) (charnump);
                        sb.replace(i, i + 1, String.valueOf(charutfp));
                        break;
                    }
                }
            }
        }
    }


    private static void removeEmptySubfield(Record tempRecord) {
        for (DataField df : tempRecord.getDataFields()) {
            int i = 0;
            while (i < df.getSubfields().size()) {

                while ((i < df.getSubfields().size()) && (df.getSubfields().get(i).getData().contains("^"))) {
                    String temp = df.getSubfields().get(i).getData();
                    int j = temp.indexOf("#");
                    int k = temp.indexOf("#", j + 1);
                    if (k >= 0) {
                        // temp=temp;

                        temp = temp.replace(temp.substring(j, k + 1), "");
                        df.getSubfields().get(i).setData(temp);

                        if (df.getSubfields().get(i).getData().trim().equals("")) {
                            df.removeSubfield(df.getSubfields().get(i));
                            i = (i > 0) ? --i : 0;
                        }
                    } else i++;
                }
                i++;
            }
        }
    }

    private static boolean contains(final int[] array, final int v) {

        boolean result = false;

        for (int i : array) {
            if (i == v) {
                result = true;
                break;
            }
        }

        return result;
    }


//    public static boolean containss(final int[] array, final int v) {
//
//        boolean result = false;
//
//        for (int i : array) {
//            if (i==v) {
//                result = true;
//                break;
//            }
//        }

//        return result;
//    }

    private static boolean fieldFind(Record tempRecord, String tag) {

        for (DataField df : tempRecord.getDataFields()) {
            if (df.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }


    private static List<DataField> fill(List<DataField> tempDatafield, IsoField isoField) {
        String temp;
        String access = "";
        String oldStr, newStr = "";
        // String reference="";
        for (IsoSubField isf : isoField.getSubfiled()) {
            temp = "#".concat(Integer.toString(Integer.parseInt(isoField.getTag())).concat("^").concat(isf.getSubfield()).toLowerCase()).concat("#");
            for (DataField df : tempDatafield) {
                for (Subfield sf : df.getSubfields()) {
                    if (sf.getData().contains(temp)) {
                        if (temp.contains("28^a") || temp.contains("44^a") || temp.contains("46^a") || temp.contains("46^c"))
                            sf.setData(sf.getData().replace(temp, "-- " + isf.getData()));
                        else sf.setData(sf.getData().replace(temp, isf.getData()));
                        sf.setId((long) 1);
                        df.setId((long) 1);

                        //cater
                        if (temp.equals("#25^c#")) {
                            StringBuilder cater = new StringBuilder(sf.getData());
                            sf.setData(sf.getData().replace(isf.getData(), cater.reverse()));
                            sf.setData(sf.getData().replace("éَگ", "گَé"));

                        }
                        //cater
                        //chap
                        if (temp.contains("32^i")) {
                            if (sf.getData().contains("1")) oldStr = "چاپی";
                            else if (sf.getData().contains("2")) oldStr = "چاپی - الکترونیکی";
                            else if (sf.getData().contains("3")) oldStr = "الکترونیکی";
                            else if (sf.getData().contains("4")) oldStr = "الکترونیکی - لوح فشرده";
                            else if (sf.getData().contains("5")) oldStr = "چاپی - لوح فشرده";
                            else oldStr = "چاپی";
//                            byte[] bytes = oldStr.getBytes();
//                            try {
//                                newStr = new String(bytes,"UTF-8");
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
                            sf.setData(oldStr);
                            StringBuilder chap = new StringBuilder(sf.getData());
                            changeFarsiString(chap);
                            sf.setData(chap.toString());
                            //chap

                        }


                        //digital access
                        if (temp.contains("60^e")) {
                            if (sf.getData().contains("0")) access = "عادی";
                            else if (sf.getData().contains("1")) access = "محرمانه";
                            else if (sf.getData().contains("2")) access = "خیلی محرمانه";
                            else if (sf.getData().contains("3")) access = "سری";
                            else if (sf.getData().contains("4")) access = "فوق سری";
                            sf.setData(access);
                            StringBuilder accessDigital = new StringBuilder(sf.getData());
                            changeFarsiString(accessDigital);
                            sf.setData(accessDigital.toString());
                        }


                        if (temp.contains("60^g")) {
                            if (sf.getData().contains("1")) access = "متن";
                            else if (sf.getData().contains("2")) access = "عکس";
                            else if (sf.getData().contains("3")) access = "صوت";
                            else if (sf.getData().contains("4")) access = "فیلم";
                            else if (sf.getData().contains("5")) access = "ناهمگون";
                            sf.setData(access);
                            StringBuilder accessDigital = new StringBuilder(sf.getData());
                            changeFarsiString(accessDigital);
                            sf.setData(accessDigital.toString());
                        }


                        if (temp.contains("60^f")) {
                            if (sf.getData().contains("0")) access = "عادی";
                            else if (sf.getData().contains("1")) access = "محرمانه";
                            else if (sf.getData().contains("2")) access = "خیلی محرمانه";
                            else if (sf.getData().contains("3")) access = "سری";
                            else if (sf.getData().contains("4")) access = "فوق سری";
                            sf.setData(access);
                            StringBuilder accessDigital = new StringBuilder(sf.getData());
                            changeFarsiString(accessDigital);
                            sf.setData(accessDigital.toString());
                        }

                        if (temp.contains("60^h")) {
                            if (sf.getData().contains("1")) access = "اسکن شده";
                            else if (sf.getData().contains("2")) access = "دیجیتال";
                            else if (sf.getData().contains("3")) access = "کاتالوگ";
                            else if (sf.getData().contains("4")) access = "پوستر";
                            else if (sf.getData().contains("5")) access = "طرح روی جلد";
                            else if (sf.getData().contains("6")) access = "سخنرانی";
                            else if (sf.getData().contains("7")) access = "آهنگ";
                            else if (sf.getData().contains("8")) access = "کوتاه";
                            else if (sf.getData().contains("9")) access = "بلند";
                            else if (sf.getData().contains("10")) access = "مستند";
                            else if (sf.getData().contains("11")) access = "تست";

                            sf.setData(access);
                            StringBuilder accessDigital = new StringBuilder(sf.getData());
                            changeFarsiString(accessDigital);
                            sf.setData(accessDigital.toString());
                        }
                        //digital access


                        if (temp.contains("57^k")) {
                            if (!sf.getData().contains("è\u200F¤")) {
                                referenced = "ô";
                            }
                        }


                    }
                }
            }
        }
        return tempDatafield;
    }

    private static List<DataField> getMapping(IsoField isoField, Record mappingRecord) {
        List<DataField> mapping = new ArrayList<>();
        for (DataField df : mappingRecord.getDataFields()) {
            for (Subfield sf : df.getSubfields()) {
                for (IsoSubField isf : isoField.getSubfiled()) {
                    if (sf.getData().contains("#".concat(Integer.toString(Integer.parseInt(isoField.getTag()))).concat("^").concat(isf.getSubfield()).concat("#").toLowerCase())) {
                        DataField temp = new DataFieldImpl(df.getTag(), df.getIndicator1(), df.getIndicator2());
                        for (Subfield sf2 : df.getSubfields()) {
                            Subfield s = new SubfieldImpl();
                            s.setCode(sf2.getCode());
                            s.setData(sf2.getData());
                            String s1 = mapping.toString();
                            String s2 = df.toString();
                            if (!s1.contains(s2)) {
                                temp.addSubfield(s);
                            }
                        }
                        if (!temp.getSubfields().isEmpty()) {
                            mapping.add(temp);
//                            mappingRecord.removeVariableField(df);

                        }
                    }

                }
            }
        }
        return mapping;
    }


    private static List<DataField> fixed(Record mappingRecord) {
        List<DataField> mapping = new ArrayList<>();
        for (DataField df : mappingRecord.getDataFields()) {
            for (Subfield sf : df.getSubfields()) {
                //   for (IsoSubField isf : isoField.getSubfiled()) {
                //fixed

                if (!df.toString().contains("#")) {
                    DataField temp = new DataFieldImpl(df.getTag(), df.getIndicator1(), df.getIndicator2());
                    for (Subfield sf2 : df.getSubfields()) {
                        Subfield s = new SubfieldImpl();
                        s.setCode(sf2.getCode());
                        s.setData(sf2.getData());
                        String s1 = mapping.toString();
                        String s2 = df.toString();
                        if (!s1.contains(s2)) {
                            temp.addSubfield(s);
                        }
                    }
                    if (!temp.getSubfields().isEmpty()) {
                        mapping.add(temp);


                    }
                }

                //fixed
            }

        }


        return mapping;
    }


    private static boolean tagRepeated(List<DataField> dataFields, DataField tempDF) {
        if (tempDF.getIndicator2() != 0) {
            return false;
        }
        for (DataField DF : dataFields) {
            if (DF.getTag().equals(tempDF.getTag())) {
                return true;
            }
        }
        return false;
    }

    public static Record getMapping(String mappingPath, String item) {
        InputStream mapping = null;
        Record mapRecord = null;
        try {
            mapping = new FileInputStream(mappingPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        MarcXmlReader reader = new MarcXmlReader(mapping);
        while (reader.hasNext()) {
            mapRecord = reader.next();
            String s = mapRecord.getControlFields().get(0).getData();
            if (mapRecord.getControlFields().get(0).getData().equals(item)) {
                break;
            } else {
                mapRecord = null;
            }
        }
        return mapRecord;
    }

}
