/**
 * This class manages all the reasoning tasks makable in OWL Full, that are:
 *      +) Query answering;
 *      +) Consistency checking (ontology and concepts);
 *      +) Instance checking and retrieval;
 *      +) Concept subsumption (and classification).
 */
package ontoapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.iterator.ExtendedIterator;


public class OntoCapecReasoner {
    
    // Constant attributes
    final String myns = new OntoCapecModel().getMynsUri();
    final String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                        "PREFIX myns: <http://krstProj.com/capec#>";
    
    // Attributes for customization
    String queryExample = "SELECT ?sub ?obj WHERE { ?subj myns:hasName ?obj }";
    
    /**
     * Method for query answering reasoning task
     * @param m: model in which execute SPARQL query
     * @param query: it can be a STRING representing the query or NULL if the 
     * query is written by user interaction.
     * @return: the overall result in ArrayList
     */
    public ArrayList<String> makeQuery(OntModel m, String query){
        ArrayList<String> resultsOfQuery = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String inputSparql;
            
            // Check if query must be inserteb by user or not
            if("".equals(query) || query == null){
                System.out.println("---/ Example: " + queryExample + " /---\n"
                        + "Write SPARQL query as in the example :");
                inputSparql = reader.readLine();
            } else {
                inputSparql = query;
            }
            
            // Parameter for query management
            String fullQuery = prefix + inputSparql;
            Query qry = QueryFactory.create(fullQuery);
            QueryExecution queryExec = QueryExecutionFactory.create(qry, m);
            ResultSet resultSet;
            boolean boolAsk;
            
            // Try: case in which the query is a SELECT query
            try{
                resultSet = queryExec.execSelect();
                // Extract variables in the query
                String[] vars = inputSparql.split("WHERE")[0].split("\\?"); 
                int numVar = vars.length;
                
                // Initialize the print of the result
                String varsStringPrint="";
                for(int i=1; i<numVar; i++){
                        varsStringPrint = varsStringPrint + "|" + vars[i].replace(" ", "");
                    }
//                System.out.println("RESULT in format: ["+varsStringPrint+"]");
                resultsOfQuery.add("RESULT in format: ["+varsStringPrint+"]\n");
                
                // Parse the result
                while(resultSet.hasNext()){
                    QuerySolution sol = resultSet.nextSolution();
                    String[] results = new String[numVar-1];
                    for(int i=1; i<numVar; i++){
                        RDFNode res = sol.get(vars[i].replace(" ", "")); 
                        results[i-1] = res.toString().split("#")[1];
                    }
                    // Print the result
//                    System.out.println(Arrays.toString(results));
                    resultsOfQuery.add(Arrays.toString(results));
                }
            }
            // Catch: the try fails, so it is a boolean query through ASK
            catch(QueryExecException e){
                boolAsk = queryExec.execAsk();
//                System.out.println("::: BOOLEAN CHECKING SOLUTION :::");
//                System.out.println(boolAsk);
                resultsOfQuery.add("BOOLEAN CHECKING SOLUTION: " + boolAsk);
            }
            queryExec.close();             
        } catch (IOException ex) {
            Logger.getLogger(OntoCapecModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resultsOfQuery;
    }
    
    /**
     * This methods is able to generate and detect inconsistency of two types:
     * 1. Instances do not respect ontology (Ontology consistency)
     * 2. Class that cannot have any instance (Concept consistency)
     * 
     * @param m: model in which to check consistency
     * @param ontoInc: true to generate inconsistency in the ontology
     * @param conceptInc: true to generate inconsistency in the concepts
     * @return: the overall result in ArrayList
     */
    public ArrayList<String> detectInconsistency(OntModel m, boolean ontoInc, boolean conceptInc){
        
        ArrayList<String> results = new ArrayList<>();
        /* Example violation 1 (ontology consistency):
        create two disjoint classes and intersect them, the result is inconsitent */
        if(ontoInc){
            OntClass conf1 = m.createClass(myns+"Conf1");
            OntClass conf2 = m.createClass(myns+"Conf2");
            conf1.addDisjointWith(conf2);
            IntersectionClass conflict = m.createIntersectionClass( myns + "Conflict", 
                    m.createList( new RDFNode[] {conf1, conf2} ) );
        }
        
        /* Example violation 2 (concept consistency):
        classes have common instances, so disjointnes create inconsistency */
        if(conceptInc){
            OntClass resource = m.getOntClass(myns + "Resource");
            OntClass skill = m.getOntClass(myns + "Skill");
            resource.addDisjointWith(skill);
        }
        
        // Start reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, m);
        
        // Check validity of consistency
        ValidityReport validity = inf.validate();
        if (validity.isValid()){
//            System.out.println("The model is Consistent");
            results.add("The model is Consistent");
        }
        else{
//            System.out.println("The model is Inconsistent:");
            results.add("The model is Inconsistent:");
            for (Iterator i = validity.getReports(); i.hasNext(); ){
                ValidityReport.Report report =(ValidityReport.Report)i.next();
//                System.out.println(" - " + report);
                results.add(" - " + report);
            }
        }
        return results;
    }
    
    
    /**
     * This methods can reason about subsumption in two ways:
     * 1. Find all subclasses in the model (Classification of T-Box)
     * 2. Check if concept C is subsumed by concept D (Concept subsumption)
     * @param m: model in which reason
     * @param C: first concept C to check if it is subsumed by D (or empty for classification)
     * @param D: second concept D to check if it subsumes C (or empty for classification)
     * @return: the overall result in ArrayList
     */
    public ArrayList<String> findSubclass(OntModel m, String C, String D){
        ArrayList<String> results = new ArrayList<>();
        if("".equals(C) || "".equals(D)){
            // Example 1: T-Box classification
            results = makeQuery(m, "SELECT ?superclass ?subclass WHERE { ?subclass rdfs:subClassOf ?superclass }");
        } else{
            // Example 2: Concept subsumption
            results = makeQuery(m, "ASK WHERE{ myns:"+ C + " rdfs:subClassOf myns:"+ D + " }");
        }
        return results;
    }
    

    /**
     * This method performs instance checking reasoning task in two ways:
     * 1. Return all members of a concept C (instance retrieval)
     * 2. Check if the instance a is instance of concept C (instance checking)
     * @param m: model on which reason
     * @param a: instance to check (or empty for retrieval)
     * @param C: concept C to instance check
     * @return: the overall result in ArrayList
     */
    public ArrayList<String> instanceChecking(OntModel m, String a, String C){
        ArrayList<String> results = new ArrayList<>();
        if("".equals(a)){
            // Example 1: instance retrieval
            OntClass conceptC = m.getOntClass(myns + C);
            ExtendedIterator instances = conceptC.listInstances();
            while (instances.hasNext()){
                Individual thisInstance = (Individual) instances.next();
//                System.out.println(thisInstance.toString().split("#")[1]);
                results.add(thisInstance.toString().split("#")[1]);
            }
        } else{
            // Example 2: instance checking
            results = makeQuery(m, "ASK WHERE{ myns:"+ a + " rdf:type myns:"+ C + " }");
        }
        return results;
    }
}
