package ontoapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;


public class OntoCapecManagement {
    
    private String ontologyPath = "src\\dataset\\capecOntology.owl";
    private String datasetPath = "src\\dataset\\capec.csv";
    
    public void createModel(){
        
        // Initialize the model for the ontology
        OntModel m = ModelFactory.createOntologyModel();
        String myns = "http://krstProj.com/capec#" ;
        
        /************
         * CLASSES  *
         ***********/
        // Create classes
        OntClass attackPattern = m.createClass(myns + "AttackPattern");
        OntClass attackAbility = m.createClass(myns + "AttackAbility");
        OntClass attackAction = m.createClass(myns + "AttackAction");
        OntClass attacker = m.createClass(myns + "Attacker");
        OntClass attack = m.createClass(myns + "Attack");
        
        OntClass id = m.createClass(myns + "Id");
        OntClass name = m.createClass(myns + "Name");
        OntClass abstraction = m.createClass(myns + "Abstraction");
        OntClass status = m.createClass(myns + "Status");
        OntClass description = m.createClass(myns + "Description");
        OntClass likelihood = m.createClass(myns + "Likelihood");
        OntClass severity = m.createClass(myns + "Severity");
        //OntClass relatedPattern = m.createClass(myns + "Vehicle");
        
        OntClass prereq = m.createClass(myns + "Prerequisite");
        OntClass skill = m.createClass(myns + "Skill");
        OntClass resource = m.createClass(myns + "Resource");
        OntClass vulnerability = m.createClass(myns + "Vulnerability");
        
        OntClass exeFlow = m.createClass(myns + "ExecutionFlow");
        OntClass consequence = m.createClass(myns + "Consequence");
        OntClass mitigation = m.createClass(myns + "MitigationAction");
        
        // Create classes hierarchy
        attackAbility.addSubClass(prereq);
        attackAbility.addSubClass(resource);
        attackAbility.addSubClass(skill);
        
        attackAction.addSubClass(exeFlow);
        attackAction.addSubClass(consequence);
        attackAction.addSubClass(mitigation);
        
        attackPattern.addSubClass(id);
        
        /*******************
         * OBJECT PROPERTY *
         ******************/
        // Create ObjectProperty for attackPattern
        /*ObjectProperty hasId = m.createObjectProperty(myns +"hasId");
        hasId.addDomain(attackPattern);
        hasId.addRange(id);*/
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
        mitigation.addDisjointWith(exeFlow);
        mitigation.addDisjointWith(consequence);
        resource.addDisjointWith(skill);
        
        /***************
         * INDIVIDUALS *
         **************/
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
                String description_ = data[4];
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
                
                Individual attackP = id.createIndividual(myns+id_);
                Individual nameP = name.createIndividual(myns+name_);/*
                Individual abstP = name.createIndividual(myns+abstraction_);
                Individual statP = name.createIndividual(myns+status_);
                Individual descP = name.createIndividual(myns+description_);
                Individual likeP = name.createIndividual(myns+likelihood_);
                Individual sevP = name.createIndividual(myns+severity_);
                Individual relP = name.createIndividual(myns+relatedPattern_);
                Individual flowP = name.createIndividual(myns+exeFlow_);
                Individual prereqP = name.createIndividual(myns+prereq_);
                Individual skillP = name.createIndividual(myns+skill_);
                Individual resP = name.createIndividual(myns+resource_);
                Individual consP = name.createIndividual(myns+consequence_);
                Individual mitigP = name.createIndividual(myns+mitigation_);
                Individual vulnP = name.createIndividual(myns+vulnerability_);*/
                
                // Object Property assertions
                m.add(attackP, hasName, nameP);
                
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
    }
}
