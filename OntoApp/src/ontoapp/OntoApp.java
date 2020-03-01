/**
 * Main class with all directions to understand and get the ontology
 */
package ontoapp;

import org.apache.jena.ontology.OntModel;

public class OntoApp {
    
    public static void startQuery(OntoCapecReasoner reasoner, OntModel modelCapec){
        System.out.println("\nA - QUERY ANSWERING: return likelihood of attack patterns with HIGH severity.");
        String queryExample = "SELECT ?attackPatternName ?likelihood "
                            + "WHERE {?attackPattern myns:hasSeverity myns:High. "
                            + "?attackPattern myns:hasLikelihood ?likelihood. "
                            + "?attackPattern myns:hasName ?attackPatternName}";
        reasoner.makeQuery(modelCapec, queryExample);
    }
    
    public static void startConsistency(OntoCapecReasoner reasoner, OntModel modelCapec){
        System.out.println("\nA - CONSISTENCY: is the model fully consistent?");
        reasoner.detectInconsistency(modelCapec, false, false);
        System.out.println("\nB - CONCEPT INCONSISTENCY: are all concept consistent?");
        reasoner.detectInconsistency(modelCapec, false, true);
        System.out.println("\nC - ONTOLOGY INCONSISTENCY: is the ontology consistent?");
        reasoner.detectInconsistency(modelCapec, true, false);
    }
    
    public static void startSubsumption(OntoCapecReasoner reasoner, OntModel modelCapec){
        System.out.println("\nA - T-Box CLASSIFICATION: return all subclasses");
        reasoner.findSubclass(modelCapec, "", "");
        System.out.println("\nB - CONCEPT SUBSUMPTION: is concept 'Skill' subsumed by 'Attacker' ?");
        reasoner.findSubclass(modelCapec, "Skill", "Attacker");
        System.out.println("\nC - CONCEPT SUBSUMPTION: is concept 'Status' subsumed by 'Severity' ?");
        reasoner.findSubclass(modelCapec, "Status", "Severity");
    }
    
    public static void startInstanceChecking(OntoCapecReasoner reasoner, OntModel modelCapec){
        System.out.println("\nA - INSTANCE CHECKING: is individual 'Standard' instance of 'Abstraction'?");
        reasoner.instanceChecking(modelCapec, "Standard", "Abstraction");
        System.out.println("\nB - INSTANCE CHECKING: is individual 'Standard' instance of 'Status'?");
        reasoner.instanceChecking(modelCapec, "Standard", "Status");
        System.out.println("\nC - INSTANCE RETRIEVAL: which are the instances of concept 'Name' ?");
        reasoner.instanceChecking(modelCapec, "", "Name");
    }

    public static void main(String[] args) {

        OntoCapecModel onto = new OntoCapecModel();
        OntoCapecReasoner reasoner = new OntoCapecReasoner();
        
        OntModel modelCapec = onto.createModel();
        System.out.println("Ontology created in file: "+onto.ontologyPath +"\n");
        
        startQuery(reasoner, modelCapec);
        startSubsumption(reasoner, modelCapec);
        startInstanceChecking(reasoner, modelCapec);
        startConsistency(reasoner, modelCapec);

    }
}
