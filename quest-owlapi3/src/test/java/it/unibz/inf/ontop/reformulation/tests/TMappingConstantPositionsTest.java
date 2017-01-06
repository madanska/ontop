package it.unibz.inf.ontop.reformulation.tests;

/*
 * #%L
 * ontop-quest-owlapi
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import it.unibz.inf.ontop.injection.QuestConfiguration;
import it.unibz.inf.ontop.owlrefplatform.core.QuestConstants;
import it.unibz.inf.ontop.injection.QuestCorePreferences;
import it.unibz.inf.ontop.owlrefplatform.owlapi.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/***
 */

public class TMappingConstantPositionsTest extends TestCase {
	private Connection conn;

	final String owlfile = "src/test/resources/test/tmapping-positions.owl";
	final String obdafile = "src/test/resources/test/tmapping-positions.obda";

	@Override
	public void setUp() throws Exception {
		
		
		/*
		 * Initializing and H2 database with the stock exchange data
		 */
		// String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:questjunitdb";
		String username = "sa";
		String password = "";

		conn = DriverManager.getConnection(url, username, password);
		Statement st = conn.createStatement();

		FileReader reader = new FileReader("src/test/resources/test/tmapping-positions-create-h2.sql");
		BufferedReader in = new BufferedReader(reader);
		StringBuilder bf = new StringBuilder();
		String line = in.readLine();
		while (line != null) {
			bf.append(line);
			line = in.readLine();
		}
		in.close();

		st.executeUpdate(bf.toString());
		conn.commit();

	}

	@Override
	public void tearDown() throws Exception {
	
			dropTables();
			conn.close();
		
	}

	private void dropTables() throws SQLException, IOException {

		Statement st = conn.createStatement();

		FileReader reader = new FileReader("src/test/resources/test/tmapping-positions-drop-h2.sql");
		BufferedReader in = new BufferedReader(reader);
		StringBuilder bf = new StringBuilder();
		String line = in.readLine();
		while (line != null) {
			bf.append(line);
			line = in.readLine();
		}
		in.close();

		st.executeUpdate(bf.toString());
		st.close();
		conn.commit();
	}

	private void runTests(Properties p) throws Exception {

		// Creating a new instance of the reasoner
		QuestOWLFactory factory = new QuestOWLFactory();
        QuestConfiguration config = QuestConfiguration.defaultBuilder()
				.nativeOntopMappingFile(obdafile)
				.ontologyFile(owlfile)
				.properties(p)
				.build();
        QuestOWL reasoner = factory.createReasoner(config);

		//System.out.println(reasoner.getQuestInstance().getUnfolder().getRules());
		
		// Now we are ready for querying
		QuestOWLConnection conn = reasoner.getConnection();
		QuestOWLStatement st = conn.createStatement();

		String query = "PREFIX : <http://it.unibz.inf/obda/test/simple#> SELECT * WHERE { ?x a :A. }";
		try {
			QuestOWLResultSet rs = st.executeTuple(query);
			assertTrue(rs.nextRow());
			assertTrue(rs.nextRow());
			assertTrue(rs.nextRow());
			assertFalse(rs.nextRow());
		} 
		catch (Exception e) {
			throw e;
		} 
		finally {
			st.close();
		}
	}

	@Test
	public void testViEqSig() throws Exception {

		Properties p = new Properties();
		p.put(QuestCorePreferences.ABOX_MODE, QuestConstants.VIRTUAL);
		p.put(QuestCorePreferences.OPTIMIZE_EQUIVALENCES, "true");

		runTests(p);
	}

	public void testClassicEqSig() throws Exception {

		Properties p = new Properties();
		p.put(QuestCorePreferences.ABOX_MODE, QuestConstants.CLASSIC);
		p.put(QuestCorePreferences.OPTIMIZE_EQUIVALENCES, "true");
		p.put(QuestCorePreferences.OBTAIN_FROM_MAPPINGS, "true");

		try {
			runTests(p);
			fail("Was expecting an IllegalConfigurationException " +
					"(mappings are currently forbidden in the classic mode)");
		} catch (IllegalConfigurationException e) {
		}
	}


}
