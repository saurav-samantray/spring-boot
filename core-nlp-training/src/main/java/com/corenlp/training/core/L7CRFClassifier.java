package com.corenlp.training.core;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.optimization.DiffFunction;
import edu.stanford.nlp.optimization.Evaluator;
import edu.stanford.nlp.optimization.Function;
import edu.stanford.nlp.optimization.HasEvaluators;
import edu.stanford.nlp.optimization.HybridMinimizer;
import edu.stanford.nlp.optimization.InefficientSGDMinimizer;
import edu.stanford.nlp.optimization.Minimizer;
import edu.stanford.nlp.optimization.QNMinimizer;
import edu.stanford.nlp.optimization.ResultStoringMonitor;
import edu.stanford.nlp.optimization.SGDMinimizer;
import edu.stanford.nlp.optimization.SGDToQNMinimizer;
import edu.stanford.nlp.optimization.SGDWithAdaGradAndFOBOS;
import edu.stanford.nlp.optimization.SMDMinimizer;
import edu.stanford.nlp.optimization.ScaledSGDMinimizer;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.ReflectionLoading;

public class L7CRFClassifier<IN extends CoreMap> extends CRFClassifier {
	
	private long jobId;

	protected L7CRFClassifier() {
		super(new SeqClassifierFlags());
	}

	public L7CRFClassifier(Properties props) {
		super(props);
	}

	public L7CRFClassifier(SeqClassifierFlags flags) {
		super(flags);
	}

	@Override	
	public Minimizer<DiffFunction> getMinimizer(int featurePruneIteration, Evaluator[] evaluators) {
	    Minimizer<DiffFunction> minimizer = null;
	    L7QNMinimizer qnMinimizer = null;

	    if (flags.useQN || flags.useSGDtoQN) {
	      // share code for creation of QNMinimizer
	      int qnMem;
	      if (featurePruneIteration == 0) {
	        qnMem = flags.QNsize;
	      } else {
	        qnMem = flags.QNsize2;
	      }

	      if (flags.interimOutputFreq != 0) {
	        Function monitor = new ResultStoringMonitor(flags.interimOutputFreq, flags.serializeTo);
	        qnMinimizer = new L7QNMinimizer(monitor, qnMem, flags.useRobustQN);
	      } else {
	        qnMinimizer = new L7QNMinimizer(qnMem, flags.useRobustQN);
	        qnMinimizer.outputToFile = true;
	        qnMinimizer.jobId = getJobId();
	      }

	      qnMinimizer.terminateOnMaxItr(flags.maxQNItr);
	      qnMinimizer.terminateOnEvalImprovement(flags.terminateOnEvalImprovement);
	      qnMinimizer.setTerminateOnEvalImprovementNumOfEpoch(flags.terminateOnEvalImprovementNumOfEpoch);
	      qnMinimizer.suppressTestPrompt(flags.suppressTestDebug);
	      if (flags.useOWLQN) {
	        qnMinimizer.useOWLQN(flags.useOWLQN, flags.priorLambda);
	      }
	    }

	    if (flags.useQN) {
	      minimizer = qnMinimizer;
	    } else if (flags.useInPlaceSGD) {
	      SGDMinimizer<DiffFunction> sgdMinimizer =
	              new SGDMinimizer<>(flags.sigma, flags.SGDPasses, flags.tuneSampleSize, flags.stochasticBatchSize);
	      if (flags.useSGDtoQN) {
	        minimizer = new HybridMinimizer(sgdMinimizer, qnMinimizer, flags.SGDPasses);
	      } else {
	        minimizer = sgdMinimizer;
	      }
	    } else if (flags.useAdaGradFOBOS) {
	      double lambda = 0.5 / (flags.sigma * flags.sigma);
	      minimizer = new SGDWithAdaGradAndFOBOS<>(
	              flags.initRate, lambda, flags.SGDPasses, flags.stochasticBatchSize,
	              flags.priorType, flags.priorAlpha, flags.useAdaDelta, flags.useAdaDiff, flags.adaGradEps, flags.adaDeltaRho);
	      ((SGDWithAdaGradAndFOBOS<?>) minimizer).terminateOnEvalImprovement(flags.terminateOnEvalImprovement);
	      ((SGDWithAdaGradAndFOBOS<?>) minimizer).terminateOnAvgImprovement(flags.terminateOnAvgImprovement, flags.tolerance);
	      ((SGDWithAdaGradAndFOBOS<?>) minimizer).setTerminateOnEvalImprovementNumOfEpoch(flags.terminateOnEvalImprovementNumOfEpoch);
	      ((SGDWithAdaGradAndFOBOS<?>) minimizer).suppressTestPrompt(flags.suppressTestDebug);
	    } else if (flags.useSGDtoQN) {
	      minimizer = new SGDToQNMinimizer(flags.initialGain, flags.stochasticBatchSize,
	                                       flags.SGDPasses, flags.QNPasses, flags.SGD2QNhessSamples,
	                                       flags.QNsize, flags.outputIterationsToFile);
	    } else if (flags.useSMD) {
	      minimizer = new SMDMinimizer<>(flags.initialGain, flags.stochasticBatchSize, flags.stochasticMethod,
	              flags.SGDPasses);
	    } else if (flags.useSGD) {
	      minimizer = new InefficientSGDMinimizer<>(flags.initialGain, flags.stochasticBatchSize);
	    } else if (flags.useScaledSGD) {
	      minimizer = new ScaledSGDMinimizer(flags.initialGain, flags.stochasticBatchSize, flags.SGDPasses,
	          flags.scaledSGDMethod);
	    } else if (flags.l1reg > 0.0) {
	      minimizer = ReflectionLoading.loadByReflection("edu.stanford.nlp.optimization.OWLQNMinimizer", flags.l1reg);
	    } else {
	      throw new RuntimeException("No minimizer assigned!");
	    }

	    if (minimizer instanceof HasEvaluators) {
	      if (minimizer instanceof QNMinimizer) {
	        ((QNMinimizer) minimizer).setEvaluators(flags.evaluateIters, flags.startEvaluateIters, evaluators);
	      } else
	        ((HasEvaluators) minimizer).setEvaluators(flags.evaluateIters, evaluators);
	    }

	    return minimizer;
	  }

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	
}
