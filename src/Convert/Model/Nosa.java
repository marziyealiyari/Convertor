package Convert.Model;

import Convert.IsoIF;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Nosa extends Iso implements IsoIF {


    private static IsoIF instance = null;
    private List<IsoRecord> isoRecords = null;
    public static  String countPlus;

    //Cunstructors
    private Nosa() {
    }

    private Nosa(File input) {
        this.setInputFile(input);
        try {
            isoRecords = read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public Nosa(File input, String docType) {
        this.setInputFile(input);
        try {
            isoRecords = read(docType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Nosa getInstance(File input) {
        if (instance == null)
            instance = new Nosa(input);
        return (Nosa) instance;
    }

    public static Nosa getInstance(File input, String docType) {
        if (instance == null)
            instance = new Nosa(input, docType);
        return (Nosa) instance;
    }

    public static boolean checkIso(String item) {
        int i, recordLength, isolength, isoDir;
        i = item.indexOf("\u001E");
        isolength = Integer.parseInt(item.substring(1, 5));
        recordLength = item.length();
        if (item.substring(20, 22).equals("55"))
            if (isolength == recordLength)
                return true;
        return false;
    }
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
         //   InputStream br = new BufferedInputStream(new FileInputStream((iso)));
            do {
                do
                 data = data.concat(br.readLine());
                while (!(data.endsWith("\u001E\u001D")) );
                if (!docType.equals(""))
                    isoList.add(new IsoRecord(data,"nosa"));
                data = br.readLine();
            } while (data != null & isoList.size()!=1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isoList;
    }

    @Override
    public void write() {

    }


}
