package org.obda.owlrefplatform.core.abox.tests;
import inf.unibz.it.obda.api.datasource.JDBCConnectionManager;
import inf.unibz.it.obda.domain.DataSource;
import inf.unibz.it.obda.domain.OBDAMappingAxiom;
import inf.unibz.it.obda.model.rdbms.impl.RDBMSSQLQuery;
import inf.unibz.it.obda.model.rdbms.impl.RDBMSsourceParameterConstants;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.obda.owlrefplatform.core.abox.ABoxToDBDumper;
import org.obda.owlrefplatform.core.abox.DirectMappingGenerator;
import org.obda.owlrefplatform.core.abox.URIIdentyfier;
import org.obda.owlrefplatform.core.abox.URIType;
import org.obda.query.domain.Atom;
import org.obda.query.domain.CQIE;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;


public class DirectMappingGeneratorTest extends TestCase{

private String file = "src/test/resources/test/ontologies/aboxgeneration/ABoxDumpTest.owl";
	
	private OWLOntologyManager manager = null;
    private OWLOntology ontology = null;
	
	  public void test_1() throws Exception{
			
			 manager = OWLManager.createOWLOntologyManager();
		     ontology = manager.loadOntologyFromPhysicalURI((new File(file)).toURI());

		     String driver = "org.h2.Driver";
	         String url = "jdbc:h2:mem:aboxdump";
	         String username = "sa";
	         String password = "";

	         DataSource source = new DataSource(URI.create("http://www.obda.org/ABOXDUMP"));
	         source.setParameter(RDBMSsourceParameterConstants.DATABASE_DRIVER, driver);
	         source.setParameter(RDBMSsourceParameterConstants.DATABASE_PASSWORD, password);
	         source.setParameter(RDBMSsourceParameterConstants.DATABASE_URL, url);
	         source.setParameter(RDBMSsourceParameterConstants.DATABASE_USERNAME, username);
	         source.setParameter(RDBMSsourceParameterConstants.IS_IN_MEMORY, "true");
	         source.setParameter(RDBMSsourceParameterConstants.USE_DATASOURCE_FOR_ABOXDUMP, "true");
		     
	 		 Connection conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(source);

	         
		     ABoxToDBDumper dumper = new ABoxToDBDumper(source);
		     Set<OWLOntology> vex = new HashSet<OWLOntology>();
		     vex.add(ontology);
		     dumper.materialize(vex,true);
		     Map<URIIdentyfier, String> mapper = dumper.getMapper();
		     
		     DirectMappingGenerator mapGen = new DirectMappingGenerator();
		     Set<OBDAMappingAxiom> mappings = mapGen.getMappings(vex, mapper);
		     assertEquals(4, mappings.size());
		     
		     Iterator<OBDAMappingAxiom> map_it = mappings.iterator();
		     while(map_it.hasNext()){
		    	 OBDAMappingAxiom ax = map_it.next();
		    	 RDBMSSQLQuery body = (RDBMSSQLQuery) ax.getSourceQuery();
		    	 CQIE head = (CQIE) ax.getTargetQuery();
		    	 Atom a = head.getBody().get(0);
		    	 if(a.getArity() == 1){
		    		 
		    		 URIIdentyfier id = new URIIdentyfier(a.getPredicate().getName(), URIType.CONCEPT);
		    		 String tablename = mapper.get(id);
		    		 String expectedSQL = "SELECT term0 as x FROM " + tablename;
		    		 assertEquals(expectedSQL.toLowerCase(), body.toString().toLowerCase());
		    	 }else{
		    		 URIIdentyfier id = new URIIdentyfier(a.getPredicate().getName(), URIType.OBJECTPROPERTY);
		    		 String tablename = mapper.get(id);
		    		 if(tablename == null){
		    			 id = new URIIdentyfier(a.getPredicate().getName(), URIType.DATAPROPERTY);
		    			 tablename = mapper.get(id);
		    		 }
		    		 String expectedSQL = "SELECT term0 as x, term1 as y FROM " + tablename;
		    		 assertEquals(expectedSQL.toLowerCase(), body.toString().toLowerCase());
		    	 }
		    	 
		     }
	  }
}
