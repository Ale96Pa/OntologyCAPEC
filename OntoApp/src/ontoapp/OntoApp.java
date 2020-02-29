package ontoapp;

import org.apache.jena.ontology.OntModel;



public class OntoApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        OntoCapecManagement onto = new OntoCapecManagement();
        OntModel modelCapec = onto.createModel();
        onto.makeQuery(modelCapec);
        //onto.reasoningTasks(modelCapec);
    }
    
}
