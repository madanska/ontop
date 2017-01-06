package it.unibz.inf.ontop.owlrefplatform.questdb;

/*
 * #%L
 * ontop-quest-db
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

import java.io.Serializable;

import com.google.inject.Injector;
import it.unibz.inf.ontop.injection.NativeQueryLanguageComponentFactory;
import it.unibz.inf.ontop.injection.QuestConfiguration;
import it.unibz.inf.ontop.injection.OBDAFactoryWithException;
import it.unibz.inf.ontop.owlrefplatform.core.IQuestConnection;
import it.unibz.inf.ontop.owlrefplatform.core.QuestDBConnection;
import it.unibz.inf.ontop.injection.QuestCorePreferences;
import it.unibz.inf.ontop.injection.QuestComponentFactory;
import it.unibz.inf.ontop.model.OBDAException;

public abstract class QuestDBAbstractStore implements Serializable {

	private static final long serialVersionUID = -8088123404566560283L;

	private final Injector injector;
	private final QuestComponentFactory componentFactory;
	private final NativeQueryLanguageComponentFactory nativeQLFactory;
	private final OBDAFactoryWithException obdaFactory;

	protected String name;

	public QuestDBAbstractStore(String name, QuestConfiguration configuration) {
		this.name = name;

        /**
         * Setup the dependency injection for the QuestComponentFactory
         */
        injector = configuration.getInjector();
        nativeQLFactory = injector.getInstance(NativeQueryLanguageComponentFactory.class);
        componentFactory = injector.getInstance(QuestComponentFactory.class);
        obdaFactory = injector.getInstance(OBDAFactoryWithException.class);
    }

	public String getName() {
		return name;
	}
	
	/* Move to query time ? */
	public abstract QuestCorePreferences getPreferences();

	public QuestDBConnection getConnection() throws OBDAException {
	//	System.out.println("getquestdbconn..");
		return new QuestDBConnection(getQuestConnection(), nativeQLFactory);
	}
	
	public abstract IQuestConnection getQuestConnection();

    protected QuestComponentFactory getComponentFactory() {
        return componentFactory;
    }

    protected NativeQueryLanguageComponentFactory getNativeQLFactory() {
        return nativeQLFactory;
    }

    protected OBDAFactoryWithException getOBDAFactory() {
        return obdaFactory;
    }

	protected Injector getInjector() {
		return injector;
	}

}
