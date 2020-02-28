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
        mitigation.addDisjointWith(exeFlow);
        mitigation.addDisjointWith(consequence);
        resource.addDisjointWith(skill);
        
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
    
    public void makeQuery(){

    }
    
    public void reasoningTasks(OntModel m){
        
        //OntClass cl = m.getOntClass(myns + "Name");
        for (ExtendedIterator i = m.listClasses(); i.hasNext();){
            OntClass c = (OntClass) i.next();
            System.out.println(c.getLocalName() + " ");
        }
    }
}
