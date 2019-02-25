package it.unibz.inf.ontop.model.term.functionsymbol.db.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.db.DBBooleanFunctionSymbol;
import it.unibz.inf.ontop.model.type.DBTermType;

import java.util.Optional;

public class BooleanDBIfElseNullFunctionSymbolImpl extends DefaultDBIfElseNullFunctionSymbol
        implements DBBooleanFunctionSymbol {

    protected BooleanDBIfElseNullFunctionSymbolImpl(DBTermType dbBooleanType) {
        super("BOOL_IF_ELSE_NULL", dbBooleanType, dbBooleanType);
    }

    @Override
    public boolean blocksNegation() {
        return false;
    }

    @Override
    public ImmutableExpression negate(ImmutableList<? extends ImmutableTerm> subTerms, TermFactory termFactory) {
        ImmutableExpression thenCondition = Optional.of(subTerms.get(1))
                .filter(t -> t instanceof ImmutableExpression)
                .map(t -> termFactory.getDBNot((ImmutableExpression) t))
                .orElseThrow(() -> new MinorOntopInternalBugException("BooleanDBIfElseNullFunctionSymbol was " +
                        "expecting its second sub-term to be an expression"));

        return termFactory.getImmutableExpression(this, subTerms.get(0), thenCondition);
    }

    @Override
    protected ImmutableTerm simplify(ImmutableExpression newCondition, ImmutableTerm newThenValue,
                                     TermFactory termFactory,
                                  VariableNullability variableNullability) {
        if (canBeReplacedByValue(newCondition, newThenValue, termFactory)) {
            return newThenValue;
        }

        if (newThenValue instanceof ImmutableExpression) {
            return termFactory.getImmutableExpression(this, newCondition, newThenValue);
        }
        else if (newThenValue instanceof DBConstant) {
            if (newThenValue.equals(termFactory.getDBBooleanConstant(true)))
                return newCondition;
            else if (newThenValue.equals(termFactory.getDBBooleanConstant(false)))
                return newCondition.negate(termFactory);
            else
                throw new MinorOntopInternalBugException("Was expecting the constant to be boolean");
        }
        else if ((newThenValue instanceof Constant) && newThenValue.isNull())
            return newThenValue;
        else
            throw new MinorOntopInternalBugException("Unexpected new \"then\" value for a boolean IF_ELSE_NULL: "
                    + newThenValue);
    }
}
