package Convert.Model;

import java.util.ArrayList;
import java.util.List;

public class IsoRecord {

    List<IsoField> str_page = new ArrayList<IsoField>();

    public IsoRecord() {
    }

    public IsoRecord(String iso) {
        this.str_page = readAll(iso);
    }

    public IsoRecord(String iso, String type) {
        this.str_page = readOnType(iso, type);
    }

    public List<IsoField> getStr_page() {
        return str_page;
    }

    public void setStr_page(List<IsoField> str_page) {
        this.str_page = str_page;
    }

    private List<IsoField> readAll(String iso) {
        List<IsoField> isoFields = new ArrayList<IsoField>();
        List<IsoSubField> subfields = new ArrayList<IsoSubField>();
        IsoField tempIsoField = null;
        IsoSubField tempSubfiled = null;

        if (DigilibIso.checkIso(iso)) {
            int i = 0, j = iso.indexOf("#");
            String dirStr, tagStr, lenStr, offsetStr, data, subfield;
            String[] str_page = new String[60];
            int k = iso.substring(1, iso.indexOf("#")).length();
            while ((i * 12) + 24 < k) {
                subfields.clear();
                dirStr = iso.substring((i * 12) + 24, (i * 12) + 36);
                tagStr = dirStr.substring(0, 3);
                lenStr = dirStr.substring(3, 7);
                offsetStr = dirStr.substring(7, 12);
                    data = iso.substring(j + Integer.parseInt(offsetStr) + 1, j + Integer.parseInt(offsetStr) + Integer.parseInt(lenStr));
                while (!data.equals("")) {
                    String st = "";
                    int m = data.indexOf("^", 1);
                    if (m > 0) {
                        st = data.substring(0, m);
                        data = new StringBuilder(data).delete(0, m).toString();
                    } else {
                        st = data.substring(0);
                        data = new StringBuilder(data).delete(0, data.length()).toString();
                    }

                    if (st.startsWith("^"))
                        subfields.add(new IsoSubField(st.substring(1, 2), st.substring(2)));
                    else subfields.add(new IsoSubField("*", st));
                }
                isoFields.add(new IsoField(tagStr, subfields));
                i++;
            }
        }

        return isoFields;
    }

    private List<IsoField> readOnType(String iso, String type) {
        List<IsoField> isoFields = new ArrayList<IsoField>();
        List<IsoSubField> subfields = new ArrayList<IsoSubField>();
        IsoField tempIsoField = null;
        IsoSubField tempSubfiled = null;
        if (Nosa.checkIso(iso)) {
            int i = 0, j = iso.indexOf("\u001E");
            String dirStr, tagStr, lenStr, offsetStr, data, subfield;
            String[] str_page = new String[60];
            int k = iso.substring(1, iso.indexOf("\u001E")).length();
            while ((i * 13) + 24 < k) {
                subfields.clear();
                dirStr = iso.substring((i * 13) + 24, (i * 13) + 37);
                tagStr = dirStr.substring(0, 3);
                lenStr = dirStr.substring(3, 8);
                offsetStr = dirStr.substring(8, 13);
                data = iso.substring(j + Integer.parseInt(offsetStr) + 1, j + Integer.parseInt(offsetStr) + Integer.parseInt(lenStr));
                while (!data.equals("")) {
                    String st = "";
                    int m = data.indexOf("$", 1);
                    if (m > 0) {
                        st = data.substring(0, m);
                        data = new StringBuilder(data).delete(0, m).toString();
                    } else {
                        st = data.substring(0);
                        data = new StringBuilder(data).delete(0, data.length()).toString();
                    }

                    if (st.startsWith("$"))
                        if (st.length() > 2)
                            subfields.add(new IsoSubField(st.substring(1, 2), st.substring(2)));
                        else subfields.add(new IsoSubField("*", st));

                }
                isoFields.add(new IsoField(tagStr, subfields));
                i++;
            }
        }

        return isoFields;
    }
}
