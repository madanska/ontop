package it.unibz.inf.ontop.iq.node.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.node.ConstructionNode;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.substitution.Var2VarSubstitution;
import it.unibz.inf.ontop.utils.ImmutableCollectors;

import java.util.Map;
import java.util.Optional;

public abstract class CompositeQueryNodeImpl extends QueryNodeImpl {

    final SubstitutionFactory substitutionFactory;
    final IntermediateQueryFactory iqFactory;

    protected CompositeQueryNodeImpl(SubstitutionFactory substitutionFactory, IntermediateQueryFactory iqFactory) {
        super();
        this.substitutionFactory = substitutionFactory;
        this.iqFactory = iqFactory;
    }

    /**
     * Prevents creating construction nodes out of ascending substitutions
     *
     */
    protected AscendingSubstitutionNormalization normalizeAscendingSubstitution(
            ImmutableSubstitution<ImmutableTerm> ascendingSubstitution, ImmutableSet<Variable> projectedVariables) {

        if (!ascendingSubstitution.getDomain().stream().allMatch(projectedVariables::contains))
            throw new IllegalArgumentException("Invalid ascending substitution: " +
                    "its domain is not a subset of the projected variables");

        Var2VarSubstitution downRenamingSubstitution = substitutionFactory.getVar2VarSubstitution(
                ascendingSubstitution.getImmutableMap().entrySet().stream()
                        .filter(e -> e.getValue() instanceof Variable)
                        .map(e -> Maps.immutableEntry(e.getKey(), (Variable) e.getValue()))
                        .filter(e -> !projectedVariables.contains(e.getValue()))
                        .collect(ImmutableCollectors.toMap(
                                Map.Entry::getValue,
                                Map.Entry::getKey,
                                (v1, v2) -> v1)));

        ImmutableSubstitution<ImmutableTerm> newAscendingSubstitution = substitutionFactory.getSubstitution(
                downRenamingSubstitution.composeWith(ascendingSubstitution)
                        .getImmutableMap().entrySet().stream()
                            .filter(e -> projectedVariables.contains(e.getKey()))
                            .collect(ImmutableCollectors.toMap()));

        return new AscendingSubstitutionNormalization(newAscendingSubstitution, downRenamingSubstitution,
                projectedVariables);
    }


    protected class AscendingSubstitutionNormalization {

        private final ImmutableSubstitution<ImmutableTerm> ascendingSubstitution;
        private final Var2VarSubstitution downRenamingSubstitution;
        private final ImmutableSet<Variable> projectedVariables;

        private AscendingSubstitutionNormalization(ImmutableSubstitution<ImmutableTerm> ascendingSubstitution,
                                                   Var2VarSubstitution downRenamingSubstitution,
                                                   ImmutableSet<Variable> projectedVariables) {
            this.ascendingSubstitution = ascendingSubstitution;
            this.downRenamingSubstitution = downRenamingSubstitution;
            this.projectedVariables = projectedVariables;
        }

        Optional<ConstructionNode> generateTopConstructionNode() {
            return Optional.of(ascendingSubstitution)
                    .filter(s -> !s.isEmpty())
                    .map(s -> iqFactory.createConstructionNode(projectedVariables, s));

        }

        IQTree normalizeChild(IQTree child) {
            return downRenamingSubstitution.isEmpty()
                    ? child
                    : child.applyDescendingSubstitution(downRenamingSubstitution, Optional.empty());
        }

        ImmutableSubstitution<ImmutableTerm> getAscendingSubstitution() {
            return ascendingSubstitution;
        }
    }
}
