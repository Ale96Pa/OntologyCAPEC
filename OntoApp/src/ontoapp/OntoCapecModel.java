/**
 * This class manages the management of the model based on CAPEC dataset. It has
 * methods to create the model of the ontology and to fill with real data.
 */
package ontoapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;


public class OntoCapecModel {
    
    // Attributes representing constant elements in the ontology
    final String ontologyPath = "src\\dataset\\capecOntology.owl";
    final String datasetPath = "src\\dataset\\capec.csv";
    final String myns = "http://krstProj.com/capec#" ;

    // Attribute for customization
    boolean disjointness = false; // Set true if insert disjoint assertions (create conflicts because of null values)
    int numRows = 200; // Number of rows to read from CAPEC dataset (max 517, -1 for full dataset)
    String formatFile = "RDF/XML-ABBREV"; // Format of the output file
    
    // Getter of myns URI
    public String getMynsUri() {return myns;}
    
    /**
     * This method replaces the invalid characters for URI used in ontology
     * @param str: String to transform in legal format
     * @return well formatted string
     */
    public String wellFormedUri(String str){
        str = str.replace("%", "%25");
        str = str.replace("[", "(");
        str = str.replace("]", ")");
        str = str.replace("#", "%23");
        return str;
    }
    
    /**
     * This method creates the model of the ontology based on CAPEC dataset. 
     * It has:
     *      16 Classes
     *      16 Object Property
     * @return the model created (moreover it writes and generates an owl file)
     */
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
        
        /**********************
         * COMPLEX EXPRESSION *
         *********************/
        // Risk :::is::: >=2 exploits AND exist(hasConsequence).Consequence.
        SomeValuesFromRestriction existConsequence = 
                m.createSomeValuesFromRestriction(null, implies, consequence);
        
        MinCardinalityRestriction minExploit =
                m.createMinCardinalityRestriction(null, exploits, 2);
        
        IntersectionClass risk = m.createIntersectionClass(myns + "Risk", 
                    m.createList(new RDFNode[] {existConsequence, minExploit} ));
        
        /****************
         * DISJOINTNESS *
         ***************/
        status.addDisjointWith(abstraction);
        if(disjointness){
            mitigation.addDisjointWith(exeFlow);
            mitigation.addDisjointWith(consequence);
            resource.addDisjointWith(skill);
        }
        
        /***************
         * INDIVIDUALS *
         **************/
        int counter = 0; // T not load the whole file
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(datasetPath));
            br.readLine(); // skip the first line (header)
            String line;
            
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                // Elements in CAPEC dataset
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
                
                // Format string in appropriate way
                exeFlow_ = wellFormedUri(exeFlow_);
                prereq_ = wellFormedUri(prereq_);
                mitigation_ = wellFormedUri(mitigation_);
                
                // Create instances of classes
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
                
                // Object Property assertions (attack pattern)
                m.add(attackP, hasName, nameP);
                m.add(attackP, hasAbstraction, abstP);
                m.add(attackP, hasStatus, statP);
                m.add(attackP, hasSeverity, sevP);
                m.add(attackP, hasLikelihood, likeP);
                m.add(attackP, relatedPattern, relP);
                
                // Object Property assertions (attack)
                m.add(attackActP, implies, consP);
                m.add(attackActP, executes, flowP);
                m.add(mitigP, reduces, attackActP);
                
                // Object Property assertions (attacker)
                m.add(attackerP, uses, resP);
                m.add(attackerP, needs, skillP);
                m.add(attackerP, precondition, prereqP);
                
                // Object Property assertions (vulnerability and relations)
                m.add(attackerP, hasKnowledge, vulnP);
                m.add(attackerP, makes, attackActP);
                m.add(attackActP, exploits, vulnP);
                m.add(attackActP, relatedTo, attackP);
                m.add(attackerP, relatedTo, attackP);

                counter++;
                if(counter == numRows){break;}
            }
            System.out.println("Read, parsed and inserted " +counter+ " data");
        }
        
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        finally {
            if (br != null){
                try {br.close();}
                catch (IOException e) {e.printStackTrace();}
            }
        }
        
       // Write the model on owl file with rdf/xml format
        FileWriter out = null;
        try {
          out = new FileWriter(ontologyPath);
          m.write(out, formatFile);
        }
        catch (IOException ex) {Logger.getLogger(OntoCapecModel.class.getName()).log(Level.SEVERE, null, ex);}
        finally {
          if (out != null){
            try {out.close();}
            catch (IOException ex){ex.printStackTrace();}}
        }
        return m;
    }
}
