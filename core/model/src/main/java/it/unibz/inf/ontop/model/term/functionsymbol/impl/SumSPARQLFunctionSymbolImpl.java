package it.unibz.inf.ontop.model.term.functionsymbol.impl;

import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.SPARQLAggregationFunctionSymbol;
import it.unibz.inf.ontop.model.type.*;
import it.unibz.inf.ontop.model.vocabulary.SPARQL;


@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SumSPARQLFunctionSymbolImpl extends SumLikeSPARQLAggregationFunctionSymbolImpl implements SPARQLAggregationFunctionSymbol {

    protected SumSPARQLFunctionSymbolImpl(boolean isDistinct, RDFTermType rootRdfTermType) {
        super("SP_SUM", SPARQL.SUM, isDistinct, rootRdfTermType);
    }

    @Override
    protected ImmutableFunctionalTerm createAggregate(ConcreteNumericRDFDatatype rdfType, ImmutableTerm dbTerm,
                                                      TermFactory termFactory) {

        DBTermType dbType = rdfType.getClosestDBType(termFactory.getTypeFactory().getDBTypeFactory());
        return termFactory.getDBSum(dbTerm, dbType, isDistinct());
    }

    @Override
    protected ImmutableTerm combineAggregates(ImmutableTerm aggregate1, ImmutableTerm aggregate2, TermFactory termFactory) {
        DBTermType dbDecimalType = termFactory.getTypeFactory().getDBTypeFactory().getDBDecimalType();
        return termFactory.getDBBinaryNumericFunctionalTerm(SPARQL.NUMERIC_ADD, dbDecimalType, aggregate1, aggregate2);
    }

    @Override
    protected ImmutableTerm getNeutralElement(TermFactory termFactory) {
        return termFactory.getDBIntegerConstant(0);
    }
}
