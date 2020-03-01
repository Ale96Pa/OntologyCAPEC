/**
 * Main class with all directions to understand and get the ontology
 */
package ontoapp;

import java.util.ArrayList;
import org.apache.jena.ontology.OntModel;

public class OntoApp {
    
    public static void startQuery(OntoCapecReasoner reasoner, OntModel modelCapec, int index){
        ArrayList<String> header = new ArrayList<>();
        //System.out.println("\nA - QUERY ANSWERING: return likelihood of attack patterns with HIGH severity.");
        header.add("A - QUERY ANSWERING: return likelihood of attack patterns with HIGH severity.");
        String queryExample = "SELECT ?attackPatternName ?likelihood "
                            + "WHERE {?attackPattern myns:hasSeverity myns:High. "
                            + "?attackPattern myns:hasLikelihood ?likelihood. "
                            + "?attackPattern myns:hasName ?attackPatternName}";
        ArrayList<String> res = reasoner.makeQuery(modelCapec, queryExample);
        header.addAll(res);
        GuiResult guiQuery = new GuiResult(header, "Query Answering", index);
    }
    
    public static void startConsistency(OntoCapecReasoner reasoner, OntModel modelCapec, int index){
        ArrayList<String> headerA = new ArrayList<>();
        ArrayList<String> headerB = new ArrayList<>();
        ArrayList<String> headerC = new ArrayList<>();
        //System.out.println("\nA - CONSISTENCY: is the model fully consistent?");
        headerA.add("A - CONSISTENCY: is the model fully consistent?");
        ArrayList<String> res1 = reasoner.detectInconsistency(modelCapec, false, false);
        headerA.addAll(res1);
        //System.out.println("\nB - CONCEPT INCONSISTENCY: are all concept consistent?");
        headerB.add("\nB - CONCEPT INCONSISTENCY: are all concept consistent?");
        ArrayList<String> res2 = reasoner.detectInconsistency(modelCapec, false, true);
        headerB.addAll(res2);
        //System.out.println("\nC - ONTOLOGY INCONSISTENCY: is the ontology consistent?");
        headerC.add("\nC - ONTOLOGY INCONSISTENCY: is the ontology consistent?");
        ArrayList<String> res3 = reasoner.detectInconsistency(modelCapec, true, false);
        headerC.addAll(res3);
        
        headerA.addAll(headerB);
        headerA.addAll(headerC);
        GuiResult guiConsistency = new GuiResult(headerA, "Consistency Problem", index);
    }
    
    public static void startSubsumption(OntoCapecReasoner reasoner, OntModel modelCapec, int index){
        ArrayList<String> headerA = new ArrayList<>();
        ArrayList<String> headerB = new ArrayList<>();
        ArrayList<String> headerC = new ArrayList<>();
        
        //System.out.println("\nA - T-Box CLASSIFICATION: return all subclasses");
        headerA.add("A - T-Box CLASSIFICATION: return all subclasses");
        ArrayList<String> res1 = reasoner.findSubclass(modelCapec, "", "");
        headerA.addAll(res1);
        
        //System.out.println("\nB - CONCEPT SUBSUMPTION: is concept 'Skill' subsumed by 'Attacker' ?");
        headerB.add("\nB - CONCEPT SUBSUMPTION: is concept 'Skill' subsumed by 'Attacker' ?");
        ArrayList<String> res2 = reasoner.findSubclass(modelCapec, "Skill", "Attacker");
        headerB.addAll(res2);
        
        //System.out.println("\nC - CONCEPT SUBSUMPTION: is concept 'Status' subsumed by 'Severity' ?");
        headerC.add("\nC - CONCEPT SUBSUMPTION: is concept 'Status' subsumed by 'Severity' ?");
        ArrayList<String> res3 = reasoner.findSubclass(modelCapec, "Status", "Severity");
        headerC.addAll(res3);
        
        headerA.addAll(headerB);
        headerA.addAll(headerC);
        GuiResult guiSubsumption = new GuiResult(headerA, "Concept Subsumption", index);
    }
    
    public static void startInstanceChecking(OntoCapecReasoner reasoner, OntModel modelCapec, int index){
        ArrayList<String> headerA = new ArrayList<>();
        ArrayList<String> headerB = new ArrayList<>();
        ArrayList<String> headerC = new ArrayList<>();
        
        //System.out.println("\nA - INSTANCE CHECKING: is individual 'Standard' instance of 'Abstraction'?");
        headerA.add("A - INSTANCE CHECKING: is individual 'Standard' instance of 'Abstraction'?");
        ArrayList<String> res1 = reasoner.instanceChecking(modelCapec, "Standard", "Abstraction");
        headerA.addAll(res1);
        
        //System.out.println("\nB - INSTANCE CHECKING: is individual 'Standard' instance of 'Status'?");
        headerB.add("\nB - INSTANCE CHECKING: is individual 'Standard' instance of 'Status'?");
        ArrayList<String> res2 = reasoner.instanceChecking(modelCapec, "Standard", "Status");
        headerB.addAll(res2);
        
        //System.out.println("\nC - INSTANCE RETRIEVAL: which are the instances of concept 'Name' ?");
        headerC.add("\nC - INSTANCE RETRIEVAL: which are the instances of concept 'Name' ?");
        ArrayList<String> res3 = reasoner.instanceChecking(modelCapec, "", "Name");
        headerC.addAll(res3);
        
        headerA.addAll(headerB);
        headerA.addAll(headerC);
        GuiResult guiChecking = new GuiResult(headerA, "Instance Checking", index);
    }

    public static void main(String[] args) {

        OntoCapecModel onto = new OntoCapecModel();
        OntoCapecReasoner reasoner = new OntoCapecReasoner();
        
        OntModel modelCapec = onto.createModel();
        System.out.println("Ontology created in file: "+onto.ontologyPath +"\n");
        
        startQuery(reasoner, modelCapec, 0);
        startSubsumption(reasoner, modelCapec, 1);
        startInstanceChecking(reasoner, modelCapec, 2);
        startConsistency(reasoner, modelCapec, 3);
    }
}
