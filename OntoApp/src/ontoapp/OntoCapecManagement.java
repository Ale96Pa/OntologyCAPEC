package ontoapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Integer.parseInt;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import jena.query;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.util.iterator.ExtendedIterator;


public class OntoCapecManagement {
    
    private String ontologyPath = "src\\dataset\\capecOntologySmall.owl";
    private String datasetPath = "src\\dataset\\capec.csv";
    private String myns = "http://krstProj.com/capec#" ;
    
    public String wellFormedUri(String str){
        str = str.replace("%", "%25");
        str = str.replace("[", "(");
        str = str.replace("]", ")");
        str = str.replace("#", "%23");
        return str;
    }
    
    public OntModel createModel(){
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        
        /************
         * CLASSES  *
         ***********/
        // Create classes
        OntClass attackPattern = m.createClass(myns + "AttackPattern");
        OntClass attacker = m.createClass(myns + "Attacker");
        OntClass attack = m.createClass(myns + "Attack");
        
        OntClass id = m.createClass(myns + "Id");
        OntClass name = m.createClass(myns + "Name");
        OntClass abstraction = m.createClass(myns + "Abstraction");
        OntClass status = m.createClass(myns + "Status");
        OntClass likelihood = m.createClass(myns + "Likelihood");
        OntClass severity = m.createClass(myns + "Severity");
        
        OntClass prereq = m.createClass(myns + "Prerequisite");
        OntClass skill = m.createClass(myns + "Skill");
        OntClass resource = m.createClass(myns + "Resource");
        OntClass vulnerability = m.createClass(myns + "Vulnerability");
        
        OntClass exeFlow = m.createClass(myns + "ExecutionFlow");
        OntClass consequence = m.createClass(myns + "Consequence");
        OntClass mitigation = m.createClass(myns + "MitigationAction");
        
        // Create classes hierarchy
        attacker.addSubClass(prereq);
        attacker.addSubClass(resource);
        attacker.addSubClass(skill);
        
        attack.addSubClass(exeFlow);
        attack.addSubClass(consequence);
        attack.addSubClass(mitigation);
        
        attackPattern.addSubClass(id);
        
        /*******************
         * OBJECT PROPERTY *
         ******************/
        // Create ObjectProperty for attackPattern
        ObjectProperty hasName = m.createObjectProperty(myns +"hasName");
        hasName.addDomain(attackPattern);
        hasName.addRange(name);
        ObjectProperty hasAbstraction = m.createObjectProperty(myns +"hasAbstraction");
        hasAbstraction.addDomain(attackPattern);
        hasAbstraction.addRange(abstraction);
        ObjectProperty hasStatus = m.createObjectProperty(myns +"hasStatus");
        hasStatus.addDomain(attackPattern);
        hasStatus.addRange(status);
        ObjectProperty hasSeverity = m.createObjectProperty(myns +"hasSeverity");
        hasSeverity.addDomain(attackPattern);
        hasSeverity.addRange(severity);
        ObjectProperty hasLikelihood = m.createObjectProperty(myns +"hasLikelihood");
        hasLikelihood.addDomain(attackPattern);
        hasLikelihood.addRange(likelihood);
        
        // Create ObjectProperty for attacker
        ObjectProperty uses = m.createObjectProperty(myns + "uses");
        uses.addDomain(attacker);
        uses.addRange(resource);
        ObjectProperty needs = m.createObjectProperty(myns + "need");
        needs.addDomain(attacker);
        needs.addRange(skill);
        ObjectProperty precondition = m.createObjectProperty(myns + "precondition");
        precondition.addDomain(attacker);
        precondition.addRange(prereq);
        
        // Create ObjectProperty for attack
        ObjectProperty implies = m.createObjectProperty(myns + "implies");
        implies.addDomain(attack);
        implies.addRange(consequence);
        ObjectProperty executes = m.createObjectProperty(myns + "executes");
        executes.addDomain(attack);
        executes.addRange(exeFlow);
        ObjectProperty reduces = m.createObjectProperty(myns + "reduces");
        reduces.addDomain(mitigation);
        reduces.addRange(attack);
        
        // Connection of ObjectProperty between main classes
        ObjectProperty hasKnowledge = m.createObjectProperty(myns + "hasKnowledge");
        hasKnowledge.addDomain(attacker);
        hasKnowledge.addRange(vulnerability);
        ObjectProperty exploits = m.createObjectProperty(myns + "exploits");
        exploits.addDomain(attack);
        exploits.addRange(vulnerability);
        ObjectProperty makes = m.createObjectProperty(myns + "makes");
        makes.addDomain(attacker);
        makes.addRange(attack);
        ObjectProperty relatedTo = m.createObjectProperty(myns + "relatedTo");
        relatedTo.addDomain(attack);
        relatedTo.addRange(attackPattern);
        ObjectProperty relatedPattern = m.createSymmetricProperty(myns + "relatedPattern"); // symmetric
        relatedPattern.addDomain(attackPattern);
        relatedPattern.addRange(attackPattern);
        
        /****************
         * DISJOINTNESS *
         ***************/
        status.addDisjointWith(abstraction);
//        mitigation.addDisjointWith(exeFlow);
//        mitigation.addDisjointWith(consequence);
//        resource.addDisjointWith(skill);
        
        /***************
         * INDIVIDUALS *
         **************/
        int counter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(datasetPath));
            br.readLine(); // this will read the first line
            String line=null;
            while ((line = br.readLine()) != null) {
                
                String[] data = line.split(",");
                
                String id_ = data[0];
                String name_ = data[1];
                String abstraction_ = data[2];
                String status_ = data[3];
                String likelihood_ = data[6];
                String severity_ = data[7];
                String relatedPattern_ = data[8];
                String exeFlow_ = data[9];
                String prereq_ = data[10];
                String skill_ = data[11];
                String resource_ = data[12];
                String consequence_ = data[14];
                String mitigation_ = data[15];
                String vulnerability_ = data[17];
               
                exeFlow_ = wellFormedUri(exeFlow_);
                prereq_ = wellFormedUri(prereq_);
                mitigation_ = wellFormedUri(mitigation_);
                
                Individual attackerP = attacker.createIndividual(myns+"attacker"+counter);
                Individual attackActP = attack.createIndividual(myns+"attack"+counter); 
                Individual attackP = id.createIndividual(myns+id_);
                Individual nameP = name.createIndividual(myns+name_);
                Individual abstP = abstraction.createIndividual(myns+abstraction_);
                Individual statP = status.createIndividual(myns+status_);
                Individual likeP = likelihood.createIndividual(myns+likelihood_);
                Individual sevP = severity.createIndividual(myns+severity_);
                Individual relP = attackPattern.createIndividual(myns+relatedPattern_);
                Individual flowP = exeFlow.createIndividual(myns+exeFlow_);
                Individual prereqP = prereq.createIndividual(myns+prereq_);
                Individual skillP = skill.createIndividual(myns+skill_);
                Individual resP = resource.createIndividual(myns+resource_);
                Individual consP = consequence.createIndividual(myns+consequence_);
                Individual mitigP = mitigation.createIndividual(myns+mitigation_);
                Individual vulnP = vulnerability.createIndividual(myns+vulnerability_);
                
                // Object Property assertions
                m.add(attackP, hasName, nameP);
                m.add(attackP, hasAbstraction, abstP);
                m.add(attackP, hasStatus, statP);
                m.add(attackP, hasSeverity, sevP);
                m.add(attackP, hasLikelihood, likeP);
                m.add(attackP, relatedPattern, relP);
                
                m.add(attackActP, implies, consP);
                m.add(attackActP, executes, flowP);
                m.add(mitigP, reduces, attackActP);
                
                m.add(attackerP, uses, resP);
                m.add(attackerP, needs, skillP);
               m.add(attackerP, precondition, prereqP);
                
                m.add(attackerP, hasKnowledge, vulnP);
                m.add(attackerP, makes, attackActP);
                m.add(attackActP, exploits, vulnP);
                m.add(attackActP, relatedTo, attackP);
                
                m.add(attackerP, relatedTo, attackP);

                counter++;
                if(counter == 200 && ontologyPath.contains("Small")){break;}
            }
            System.out.println("Parsed " +counter+ " data");
        }
        
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        finally {
            if (br != null) {
                try {br.close();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
        
       // Write the model on owl file with rdf/xml format
        FileWriter out = null;
        try {
          // rdf/xml format for owl file
          out = new FileWriter(ontologyPath);
          m.write( out, "RDF/XML-ABBREV" );
        }
        catch (IOException ex) {
            Logger.getLogger(OntoCapecManagement.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
          if (out != null) {
            try {out.close();} catch (IOException ex){ex.printStackTrace();}
          }
        }
        return m;
    }
    
    public void makeQuery(OntModel m, String query){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String inputSparql = "";
            if(query == "" || query == null){
                System.out.println("*** SPARQL example: SELECT ?subject ?object "
                        + "WHERE { ?subject myns:hasName ?object }\n"
                        + "Write SPARQL query as in the example"
                        + ":");
                inputSparql = reader.readLine();
            } else {
                inputSparql = query;
            }
            
            String q = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                        "PREFIX myns: <http://krstProj.com/capec#>" +
                        inputSparql;
            Query qry = QueryFactory.create(q);
            QueryExecution qe = QueryExecutionFactory.create(qry, m);
            ResultSet rs;
            boolean boolAsk;
            try{
                rs = qe.execSelect();
                String select = inputSparql.split("WHERE")[0];
                String[] vars = select.split("\\?");

                int numVar = vars.length;
                while(rs.hasNext())
                {
                    QuerySolution sol = rs.nextSolution();
                    String[] results = new String[numVar-1];
                    for(int i=1; i<numVar; i++){
                        RDFNode result = sol.get(vars[i].replace(" ", "")); 
                        results[i-1] = result.toString().split("#")[1];
                        //System.out.println(str.toString().split("#")[1] + "  " + thing.toString().split("#")[1]);
                    }
                    System.out.println(Arrays.toString(results));
                }
            } catch(QueryExecException e){
                boolAsk = qe.execAsk();
                System.out.println(boolAsk);
            }
            qe.close();             

        } catch (IOException ex) {
            Logger.getLogger(OntoCapecManagement.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    // 1. Instances do not respect ontology (Ontology consistency)
    // 2. Class that cannot have any instance (Concept consistency)
    public void detectInconsistency(OntModel m){
        
        // Example violation 1 (ontology consistency)
        OntClass conf1 = m.createClass(myns+"Conf1");
        OntClass conf2 = m.createClass(myns+"Conf2");
        conf1.addDisjointWith(conf2);
        IntersectionClass conflict = m.createIntersectionClass( myns + "Conflict", 
                m.createList( new RDFNode[] {conf1, conf2} ) );
        
        // Example violation 2 (concept consistency)
 /*       OntClass resource = m.getOntClass(myns + "Resource");
        OntClass skill = m.getOntClass(myns + "Skill");
        resource.addDisjointWith(skill);
 */       
        // Start reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, m);
        
        // Check validity
        ValidityReport validity = inf.validate();
        if (validity.isValid()){
            System.out.println("OK");
        }
        else{
            System.out.println("Conflicts");
            for (Iterator i = validity.getReports(); i.hasNext(); ){
                ValidityReport.Report report =(ValidityReport.Report)i.next();
                System.out.println(" - " + report);
            }
        }
    }
    
    // 1. Find all subclasses in the model (Classification of T-Box)
    // 2. Check if concept C is subsumed by concept D (Concept subsumption)
    public void findSubclass(OntModel m, String C, String D){
        
        if(C == "" || D == ""){
            // Example point 1 (T-Box classification)
            makeQuery(m, "SELECT ?super ?sub WHERE { ?sub rdfs:subClassOf ?super }");
        } else{
            // Example point 2 (Concept subsumption)
            makeQuery(m, "ASK WHERE{ myns:"+ C + " rdfs:subClassOf myns:"+ D + " }");
        }
        
    }
    
    // 1. Return all members of a concept C (instance retrieval)
    // 2. Check if the instance a is instance of concept C (instance checking)
    public void instanceChecking(OntModel m, String a, String C){
        if(a == ""){
            // Example point 1 (instance retrieval)
            OntClass conceptC = m.getOntClass(myns + C);
            ExtendedIterator instances = conceptC.listInstances();
            while (instances.hasNext()){
                Individual thisInstance = (Individual) instances.next();
                System.out.println("Found instance: " + thisInstance.toString().split("#")[1]);
            }
        } else{
            // Example point 2 (instance checking)
            makeQuery(m, "ASK WHERE{ myns:"+ a + " rdf:type myns:"+ C + " }");
            
        }
        
    }

}
