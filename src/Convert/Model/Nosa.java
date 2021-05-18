package Convert.Model;

import Convert.IsoIF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static home.controllers.Controller.counters_public;


public class Nosa extends Iso implements IsoIF {


    public static String countPlus;
    private List isoRecords = null;

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


    private Nosa(String docType, BufferedReader br) {
        // this.setInputFile(input);
        try {
            isoRecords = read(docType, br);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public static Nosa getInstance(File input) {
//        if (instance == null)
//            instance = new Nosa(input);
//        return (Nosa) instance;
//    }

    public static Nosa getInstance(String docType, BufferedReader br) {
      //  if (instance == null)
        IsoIF instance = new Nosa(docType, br);
        return (Nosa) instance;
    }

    static boolean checkIso(String item) {
        int i, recordLength, isolength, isoDir;
        isolength=0;
        try {
           isolength = Integer.parseInt(item.substring(1, 5));
       }
      catch (NumberFormatException e) {
          System.out.println(counters_public);
      }
        recordLength = item.length();
        if (item.substring(20, 22).equals("55"))
            if (isolength == recordLength)
                return true;
        return false;
    }

    //Getter and Setter
    public List getIsoRecords() {
        return isoRecords;
    }

    public void setIsoRecords(List<IsoRecord> isoRecords) {
        this.isoRecords = isoRecords;
    }

    //Methodes
    //@Override
//    public List read() throws FileNotFoundException {
//        return read("");
//    }

    private List<IsoRecord> read(String docType, BufferedReader br) throws FileNotFoundException {
        String data = "";
        File iso = this.getInputFile();
        List<IsoRecord> isoList = new ArrayList<>();
        try {
            do {
                do
                    try {
                        data = data.concat(br.readLine());
                    } catch (OutOfMemoryError t) {
                        System.exit(0);
                    }
                while (!(data.endsWith("\u001E\u001D")));
                if (!docType.equals(""))
                    isoList.add(new IsoRecord(data, "nosa"));
                data = br.readLine();
            } while (data != null & isoList.size() != 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isoList;
    }

    @Override
    public List read() throws FileNotFoundException {
        return null;
    }

    @Override
    public void write() {

    }


}
