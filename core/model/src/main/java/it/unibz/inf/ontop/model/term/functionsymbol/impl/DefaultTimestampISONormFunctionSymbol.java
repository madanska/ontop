package it.unibz.inf.ontop.model.term.functionsymbol.impl;

import it.unibz.inf.ontop.model.type.DBTermType;

import java.util.Optional;

/**
 * TODO: declare as injective, non-post-processable
 */
public class DefaultTimestampISONormFunctionSymbol extends AbstractDBTypeConversionFunctionSymbolImpl {

    private final DBTermType timestampType;

    protected DefaultTimestampISONormFunctionSymbol(DBTermType timestampType, DBTermType dbStringType) {
        super("isoTimestamp", timestampType, dbStringType);
        this.timestampType = timestampType;
    }

    @Override
    public Optional<DBTermType> getInputType() {
        return Optional.of(timestampType);
    }

    @Override
    public boolean isTemporary() {
        return false;
    }
}
