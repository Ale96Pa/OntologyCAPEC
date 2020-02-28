package ontoapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DatasetManagement {
    
    private String datasetPath = "D:\\projects\\krst\\Project\\application\\OntoApp\\src\\ontoapp\\dataset\\capec.csv";
    
    public void manageCapecDataset(){
        
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new FileReader(datasetPath));
            while ((line = br.readLine()) != null) {
                
                String[] data = line.split(",");
                
                String ID = data[0];
                String name = data[1];
                String abstraction = data[2];
                String status = data[3];
                String description = data[4];
                String likelihood = data[6];
                String severity = data[7];
                String relatedPattern = data[8];
                String exeFlow = data[9];
                String prereq = data[10];
                String skill = data[11];
                String resource = data[12];
                String consequence = data[14];
                String mitigation = data[15];
                String vulnerability = data[17];
            /*    
                System.out.println("ID: "+ ID+ " \nName: "+ name+ " \nabstraction: "+ abstraction
                + " \nstatus: "+ status + " \ndescription: "+ description+ " \nlikelihood: "+ likelihood+ 
                        " \nseverity: "+ severity + " \nrelatedPatt: "+ relatedPattern+ " \nexeFlow: "+ exeFlow+ 
                        " \nprereq: "+ prereq+ " \nskill: "+ skill+ " \nres: "+ resource+ " \ncons: "+ consequence
                + " \nmitigation: "+ mitigation + " \nvulnerability: "+ vulnerability);         
            */           
            }
        } 
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        finally {
            if (br != null) {
                try {br.close();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
    }
}
