package it.unibz.inf.ontop.answering.connection.impl;

import com.google.common.collect.ImmutableMultimap;
import it.unibz.inf.ontop.answering.connection.OntopStatement;
import it.unibz.inf.ontop.answering.logging.QueryLogger;
import it.unibz.inf.ontop.answering.reformulation.QueryReformulator;
import it.unibz.inf.ontop.answering.reformulation.input.*;
import it.unibz.inf.ontop.answering.resultset.*;
import it.unibz.inf.ontop.exception.*;
import it.unibz.inf.ontop.iq.IQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * Abstract implementation of OntopStatement.
 *
 */
public abstract class QuestStatement implements OntopStatement {

	private final QueryReformulator engine;
	private final QueryLogger.Factory queryLoggerFactory;

	private QueryExecutionThread executionThread;
	private boolean canceled = false;


	private static final Logger log = LoggerFactory.getLogger(QuestStatement.class);


	public QuestStatement(QueryReformulator queryProcessor) {
		this.engine = queryProcessor;
		this.queryLoggerFactory = queryProcessor.getQueryLoggerFactory();
	}

	/**
	 * TODO: explain
	 */
	@FunctionalInterface
	private interface Evaluator<R extends OBDAResultSet, Q extends InputQuery<R>> {

		R evaluate(Q inputQuery, IQ executableQuery, QueryLogger queryLogger)
				throws OntopQueryEvaluationException, OntopResultConversionException, OntopConnectionException;
	}

	/**
	 * Execution thread
	 */
	private class QueryExecutionThread<R extends OBDAResultSet, Q extends InputQuery<R>> extends Thread {

		private final Q inputQuery;
		private final QueryLogger queryLogger;
		private final QuestStatement.Evaluator<R, Q> evaluator;
		private final CountDownLatch monitor;
		private final IQ executableQuery;

		private R resultSet;	  // only for SELECT and ASK queries
		private Exception exception;
		private boolean executingTargetQuery;

		QueryExecutionThread(Q inputQuery, IQ executableQuery, QueryLogger queryLogger, Evaluator<R,Q> evaluator,
						CountDownLatch monitor) {
			this.executableQuery = executableQuery;
			this.inputQuery = inputQuery;
			this.queryLogger = queryLogger;
			this.evaluator = evaluator;
			this.monitor = monitor;
			this.exception = null;
			this.executingTargetQuery = false;
		}

		public boolean errorStatus() {
			return exception != null;
		}

		public Exception getException() {
			return exception;
		}

		public R getResultSet() {
			return resultSet;
		}

		public void cancel() throws OntopQueryEvaluationException {
			canceled = true;
			if (!executingTargetQuery) {
				this.stop();
			} else {
				cancelExecution();
			}
		}

		@Override
		public void run() {
			//                        FOR debugging H2 in-memory database
//			try {
//				org.h2.tools.Server.startWebServer(((QuestConnection)conn).getSQLConnection());
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
			try {
				/*
				 * Executes the target query.
				 */
				log.debug("Executing the query and get the result...");
				executingTargetQuery = true;
				resultSet = evaluator.evaluate(inputQuery, executableQuery, queryLogger);
				// NB: finished if the result set is blocking!
				log.debug("Result set unblocked.\n");
				/*
				 * TODO: re-handle the timeout exception.
				 */
			} catch (Exception e) {
				exception = e;
				log.error(e.getMessage(), e);
			} finally {
				monitor.countDown();
			}
		}
	}

	private TupleResultSet executeSelectQuery(SelectQuery inputQuery, IQ executableQuery, QueryLogger queryLogger)
			throws OntopQueryEvaluationException {
		return executeSelectQuery(executableQuery, queryLogger);
	}

	private BooleanResultSet executeBooleanQuery(AskQuery inputQuery, IQ executableQuery, QueryLogger queryLogger)
			throws OntopQueryEvaluationException {
		return executeBooleanQuery(executableQuery, queryLogger);
	}

	private GraphResultSet executeGraphQuery(GraphSPARQLQuery constructQuery, IQ executableQuery, QueryLogger queryLogger)
			throws OntopQueryEvaluationException, OntopResultConversionException, OntopConnectionException {
		return executeGraphQuery(constructQuery.getConstructTemplate(), executableQuery, queryLogger);
	}

	/**
	 * Cancel the processing of the target query.
	 */
	protected abstract void cancelExecution() throws OntopQueryEvaluationException;

	/**
	 * Calls the necessary tuple or graph query execution Implements describe
	 * uri or var logic Returns the result set for the given query
	 */
	@Override
	public <R extends OBDAResultSet> R execute(InputQuery<R> inputQuery) throws OntopConnectionException,
			                                                                            OntopReformulationException, OntopQueryEvaluationException, OntopResultConversionException {
		return execute(inputQuery, ImmutableMultimap.of());
	}

	@Override
	public <R extends OBDAResultSet> R execute(InputQuery<R> inputQuery, ImmutableMultimap<String, String> httpHeaders)
			throws OntopConnectionException, OntopReformulationException, OntopQueryEvaluationException, OntopResultConversionException {

		if (inputQuery instanceof SelectQuery) {
			return (R) executeInThread((SelectQuery) inputQuery, httpHeaders, this::executeSelectQuery);
		}
		else if (inputQuery instanceof AskQuery) {
			return (R) executeInThread((AskQuery) inputQuery, httpHeaders, this::executeBooleanQuery);
		}
		else if (inputQuery instanceof GraphSPARQLQuery) {
			return (R) executeInThread((GraphSPARQLQuery) inputQuery, httpHeaders, this::executeGraphQuery);
		}
		else {
			throw new OntopUnsupportedInputQueryException("Unsupported query type: " + inputQuery);
		}
	}

	/**
	 * Internal method to start a new query execution thread type defines the
	 * query type SELECT, ASK, CONSTRUCT, or DESCRIBE
	 */
	private <R extends OBDAResultSet, Q extends InputQuery<R>> R executeInThread(Q inputQuery, ImmutableMultimap<String, String> httpHeaders,
			Evaluator<R, Q> evaluator)
			throws OntopReformulationException, OntopQueryEvaluationException {
		QueryLogger queryLogger = queryLoggerFactory.create(httpHeaders);

		queryLogger.setSparqlQuery(inputQuery.getInputString());

		CountDownLatch monitor = new CountDownLatch(1);
		IQ executableQuery = engine.reformulateIntoNativeQuery(inputQuery, queryLogger);

		QueryExecutionThread<R, Q> executionthread = new QueryExecutionThread<>(inputQuery, executableQuery, queryLogger, evaluator,
				monitor);

		this.executionThread = executionthread;
		executionthread.start();
		try {
			monitor.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (executionthread.errorStatus()) {
			Exception ex = executionthread.getException();
			if (ex instanceof OntopReformulationException) {
				throw (OntopReformulationException) ex;
			}
			else if (ex instanceof OntopQueryEvaluationException) {
				queryLogger.declareEvaluationException(ex);
				throw (OntopQueryEvaluationException) ex;
			}
			else {
				queryLogger.declareEvaluationException(ex);
				throw new OntopQueryEvaluationException(ex);
			}
		}

		if (canceled) {
			canceled = false;
			throw new OntopQueryEvaluationException("Query execution was cancelled");
		}
		R resultSet = executionthread.getResultSet();
		return resultSet;
	}


	@Override
	public void cancel() throws OntopConnectionException {
		canceled = true;
		try {
			QuestStatement.this.executionThread.cancel();
		} catch (Exception e) {
			throw new OntopConnectionException(e);
		}
	}

	/**
	 * Called to check whether the statement was cancelled on purpose
	 */
	public boolean isCanceled(){
		return canceled;
	}

	@Override
	public String getRewritingRendering(InputQuery query) throws OntopReformulationException {
		return engine.getRewritingRendering(query);
	}


	@Override
	public IQ getExecutableQuery(InputQuery inputQuery) throws OntopReformulationException {
		return engine.reformulateIntoNativeQuery(inputQuery, queryLoggerFactory.create(ImmutableMultimap.of()));
	}

}
