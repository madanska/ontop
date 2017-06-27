package it.unibz.inf.ontop.injection;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.DuplicateMappingException;
import it.unibz.inf.ontop.mapping.MappingMetadata;
import it.unibz.inf.ontop.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.mapping.pp.SQLPPMapping;

/**
 * Some object instantiations may throw exceptions that
 * should be expected.
 *
 * This factory is dedicated to them.
 *
 * The drawback of this approach is that
 * no implementation of this factory can be automatically
 * generated by Guice.
 */
public interface SQLPPMappingFactory {

    SQLPPMapping createSQLPreProcessedMapping(ImmutableList<SQLPPTriplesMap> newMappings,
                                              MappingMetadata metadata) throws DuplicateMappingException;
}
