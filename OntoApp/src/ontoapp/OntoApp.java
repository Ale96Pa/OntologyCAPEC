package ontoapp;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.VCARD;


/**
 *
 * @author Alessandro
 */
public class OntoApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        DatasetManagement manager = new DatasetManagement();
        manager.manageCapecDataset();
        */
        
        OntoCapecManagement onto = new OntoCapecManagement();
        onto.createModel();
    }
    
}
