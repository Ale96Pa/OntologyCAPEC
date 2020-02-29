/**
 * Main class with all directions to understand and get the ontology
 */
package ontoapp;

import org.apache.jena.ontology.OntModel;

public class OntoApp {

    public static void main(String[] args) {

        OntoCapecModel onto = new OntoCapecModel();
        System.out.println("START: creating ontology");
        OntModel modelCapec = onto.createModel();
        System.out.println("END: ontology created in file: "+onto.ontologyPath);
        
        OntoCapecReasoner reasoner = new OntoCapecReasoner();
        
        System.out.println("1 - QUERY ANSWERING: return likelihood of attack patterns with HIGH severity.");
        String queryExample = "SELECT ?attackPatternName ?likelihood "
                            + "WHERE {?attackPattern myns:hasSeverity myns:High. "
                            + "?attackPattern myns:hasLikelihood ?likelihood. "
                            + "?attackPattern myns:hasName ?attackPatternName}";
//        System.out.println("Query: '"+ queryExample + "'");
        reasoner.makeQuery(modelCapec, queryExample);
        System.out.println("\n/-----------------------------------------/\n");
        
        System.out.println("2A - CONSISTENCY: is the model fully consistent?");
        reasoner.detectInconsistency(modelCapec, false, false);
        System.out.println("2B - CONCEPT INCONSISTENCY: are all concept consistent?");
        reasoner.detectInconsistency(modelCapec, false, true);
        System.out.println("2C - ONTOLOGY INCONSISTENCY: is the ontology consistent?");
        reasoner.detectInconsistency(modelCapec, true, false);
        System.out.println("\n/-----------------------------------------/\n");

// TODO: mettere concetti per 3B
        //System.out.println("3A - T-Box CLASSIFICATION: return all subclasses");
        //reasoner.findSubclass(modelCapec, "", "");
/*TODO*///System.out.println("3B - CONCEPT SUBSUMPTION: is concept **** subsumed by *******?");
        //reasoner.findSubclass(modelCapec, "", "");
        //System.out.println("/-----------------------------------------/");

// TODO: mettere concetti per 4A
/*TODO*///System.out.println("4A - INSTANCE CHECKING: is individual ' ' instance of ' '?");
/*TODO*///reasoner.instanceChecking(modelCapec, "", null);
/*TODO*///System.out.println("4B - INSTANCE RETRIEVAL: which are the instances of concept ' '");
/*TODO*///reasoner.instanceChecking(modelCapec, "", "");        
    }
    
}
