package it.unibz.inf.ontop.answering.logging.impl;

import com.google.inject.Inject;
import it.unibz.inf.ontop.answering.logging.QueryLogger;
import it.unibz.inf.ontop.injection.OntopReformulationSettings;

import java.io.PrintStream;
import java.util.UUID;

public class QueryLoggerImpl implements QueryLogger {
    private final UUID queryId;
    private final long creationTime;
    private final PrintStream outputStream;
    private final OntopReformulationSettings settings;
    private final boolean disabled;
    private long reformulationTime;
    private long unblockedResulSetTime;

    @Inject
    protected QueryLoggerImpl(OntopReformulationSettings settings) {
        this(System.out, settings);
    }

    protected QueryLoggerImpl(PrintStream outputStream, OntopReformulationSettings settings) {
        this.disabled = !settings.isQueryLoggingEnabled();
        this.outputStream = outputStream;
        this.settings = settings;
        this.queryId = UUID.randomUUID();
        creationTime = System.currentTimeMillis();
        reformulationTime = -1;
        unblockedResulSetTime = -1;
    }

    @Override
    public void declareReformulationFinishedAndSerialize(boolean wasCached) {
        if (disabled)
            return;

        reformulationTime = System.currentTimeMillis();
        // TODO: use a proper framework
        String json = String.format(
                "{\"queryId\": %s, \"reformulationDuration\": %d, \"reformulationCacheHit\": %b}",
                queryId,
                reformulationTime - creationTime,
                wasCached);
        outputStream.println(json);
    }

    @Override
    public void declareResultSetUnblockedAndSerialize() {
        if (disabled)
            return;
        unblockedResulSetTime = System.currentTimeMillis();
        if (reformulationTime == -1)
            throw new IllegalStateException("Reformulation should have been declared as finished");

        // TODO: use a proper framework
        String json = String.format(
                "{\"queryId\": %s, \"executionBeforeUnblockingDuration\": %d}",
                queryId,
                unblockedResulSetTime - reformulationTime);
        outputStream.println(json);
    }

    @Override
    public void declareLastResultRetrievedAndSerialize(long resultCount) {
        if (disabled)
            return;

        long lastResultFetchedTime = System.currentTimeMillis();
        if (unblockedResulSetTime == -1)
            throw new IllegalStateException("Result set should have been declared as unblocked");

        // TODO: use a proper framework
        String json = String.format(
                "{\"queryId\": %s, \"executionAndFetchingDuration\": %d, \"totalDuration\": %d, \"resultCount\": %d}",
                queryId,
                lastResultFetchedTime - reformulationTime,
                lastResultFetchedTime - creationTime,
                resultCount);
        outputStream.println(json);
    }
}