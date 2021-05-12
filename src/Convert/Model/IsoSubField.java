package Convert.Model;

public class IsoSubField {
    String subfield,data;

    public IsoSubField(String subfield, String data) {
        this.subfield = subfield;
        this.data = data;
    }

    public String getSubfield() {
        return subfield;
    }

    public void setSubfield(String subfield) {
        this.subfield = subfield;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
