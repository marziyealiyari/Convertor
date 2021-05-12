package Convert.Model;

import java.util.ArrayList;
import java.util.List;

public class IsoField {
    private String tag;
    private List<IsoSubField> subfiled;

    public IsoField(String tag, List<IsoSubField> subfiled) {
        this.tag = tag;
       setSubfiled(subfiled);

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<IsoSubField> getSubfiled() {
        return subfiled;
    }

    public void setSubfiled(List<IsoSubField> subfiled) {
        this.subfiled=new ArrayList<>();
        for (IsoSubField s:subfiled) {
          this.subfiled.add(s);
        }
    }
}
