package com.vikas.NaiveByaesHadoop;
/*
 * Decompiled with CFR 0_102.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli2.Argument
 *  org.apache.commons.cli2.CommandLine
 *  org.apache.commons.cli2.Group
 *  org.apache.commons.cli2.Option
 *  org.apache.commons.cli2.OptionException
 *  org.apache.commons.cli2.builder.ArgumentBuilder
 *  org.apache.commons.cli2.builder.DefaultOptionBuilder
 *  org.apache.commons.cli2.builder.GroupBuilder
 *  org.apache.commons.cli2.commandline.Parser
 *  org.apache.commons.cli2.option.DefaultOption
 *  org.apache.hadoop.conf.Configuration
 *  org.apache.hadoop.fs.Path
 *  org.apache.hadoop.util.Tool
 *  org.apache.hadoop.util.ToolRunner
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.standard.StandardAnalyzer
 *  org.apache.mahout.common.AbstractJob
 *  org.apache.mahout.common.CommandLineUtil
 *  org.apache.mahout.common.HadoopUtil
 *  org.apache.mahout.common.Pair
 *  org.apache.mahout.common.commandline.DefaultOptionCreator
 *  org.apache.mahout.common.lucene.AnalyzerUtils
 *  org.apache.mahout.math.hadoop.stats.BasicStats
 *  org.apache.mahout.vectorizer.DictionaryVectorizer
 *  org.apache.mahout.vectorizer.DocumentProcessor
 *  org.apache.mahout.vectorizer.HighDFWordsPruner
 *  org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles1
 *  org.apache.mahout.vectorizer.tfidf.TFIDFConverter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */


import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.CommandLineUtil;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.lucene.AnalyzerUtils;
import org.apache.mahout.math.hadoop.stats.BasicStats;
import org.apache.mahout.vectorizer.DictionaryVectorizer;
import org.apache.mahout.vectorizer.DocumentProcessor;
import org.apache.mahout.vectorizer.HighDFWordsPruner;
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SparseVectorsFromSequenceFiles1
extends AbstractJob {
    private static final Logger log = LoggerFactory.getLogger((Class)SparseVectorsFromSequenceFiles1.class);

    public static void main(String[] args) throws Exception {
        ToolRunner.run((Tool)new SparseVectorsFromSequenceFiles1(), (String[])args);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public int run(String[] args) throws Exception {
        DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        ArgumentBuilder abuilder = new ArgumentBuilder();
        GroupBuilder gbuilder = new GroupBuilder();
        DefaultOption inputDirOpt = DefaultOptionCreator.inputOption().create();
        DefaultOption outputDirOpt = DefaultOptionCreator.outputOption().create();
        DefaultOption minSupportOpt = obuilder.withLongName("minSupport").withArgument(abuilder.withName("minSupport").withMinimum(1).withMaximum(1).create()).withDescription("(Optional) Minimum Support. Default Value: 2").withShortName("s").create();
        DefaultOption analyzerNameOpt = obuilder.withLongName("analyzerName").withArgument(abuilder.withName("analyzerName").withMinimum(1).withMaximum(1).create()).withDescription("The class name of the analyzer").withShortName("a").create();
        DefaultOption chunkSizeOpt = obuilder.withLongName("chunkSize").withArgument(abuilder.withName("chunkSize").withMinimum(1).withMaximum(1).create()).withDescription("The chunkSize in MegaBytes. Default Value: 100MB").withShortName("chunk").create();
        DefaultOption weightOpt = obuilder.withLongName("weight").withRequired(false).withArgument(abuilder.withName("weight").withMinimum(1).withMaximum(1).create()).withDescription("The kind of weight to use. Currently TF or TFIDF. Default: TFIDF").withShortName("wt").create();
        DefaultOption minDFOpt = obuilder.withLongName("minDF").withRequired(false).withArgument(abuilder.withName("minDF").withMinimum(1).withMaximum(1).create()).withDescription("The minimum document frequency.  Default is 1").withShortName("md").create();
        DefaultOption maxDFPercentOpt = obuilder.withLongName("maxDFPercent").withRequired(false).withArgument(abuilder.withName("maxDFPercent").withMinimum(1).withMaximum(1).create()).withDescription("The max percentage of docs for the DF.  Can be used to remove really high frequency terms. Expressed as an integer between 0 and 100. Default is 99.  If maxDFSigma is also set, it will override this value.").withShortName("x").create();
        DefaultOption maxDFSigmaOpt = obuilder.withLongName("maxDFSigma").withRequired(false).withArgument(abuilder.withName("maxDFSigma").withMinimum(1).withMaximum(1).create()).withDescription("What portion of the tf (tf-idf) vectors to be used, expressed in times the standard deviation (sigma) of the document frequencies of these vectors. Can be used to remove really high frequency terms. Expressed as a double value. Good value to be specified is 3.0. In case the value is less than 0 no vectors will be filtered out. Default is -1.0.  Overrides maxDFPercent").withShortName("xs").create();
        DefaultOption minLLROpt = obuilder.withLongName("minLLR").withRequired(false).withArgument(abuilder.withName("minLLR").withMinimum(1).withMaximum(1).create()).withDescription("(Optional)The minimum Log Likelihood Ratio(Float)  Default is 1.0").withShortName("ml").create();
        DefaultOption numReduceTasksOpt = obuilder.withLongName("numReducers").withArgument(abuilder.withName("numReducers").withMinimum(1).withMaximum(1).create()).withDescription("(Optional) Number of reduce tasks. Default Value: 1").withShortName("nr").create();
        DefaultOption powerOpt = obuilder.withLongName("norm").withRequired(false).withArgument(abuilder.withName("norm").withMinimum(1).withMaximum(1).create()).withDescription("The norm to use, expressed as either a float or \"INF\" if you want to use the Infinite norm.  Must be greater or equal to 0.  The default is not to normalize").withShortName("n").create();
        DefaultOption logNormalizeOpt = obuilder.withLongName("logNormalize").withRequired(false).withDescription("(Optional) Whether output vectors should be logNormalize. If set true else false").withShortName("lnorm").create();
        DefaultOption maxNGramSizeOpt = obuilder.withLongName("maxNGramSize").withRequired(false).withArgument(abuilder.withName("ngramSize").withMinimum(1).withMaximum(1).create()).withDescription("(Optional) The maximum size of ngrams to create (2 = bigrams, 3 = trigrams, etc) Default Value:1").withShortName("ng").create();
        DefaultOption sequentialAccessVectorOpt = obuilder.withLongName("sequentialAccessVector").withRequired(false).withDescription("(Optional) Whether output vectors should be SequentialAccessVectors. If set true else false").withShortName("seq").create();
        DefaultOption namedVectorOpt = obuilder.withLongName("namedVector").withRequired(false).withDescription("(Optional) Whether output vectors should be NamedVectors. If set true else false").withShortName("nv").create();
        DefaultOption overwriteOutput = obuilder.withLongName("overwrite").withRequired(false).withDescription("If set, overwrite the output directory").withShortName("ow").create();
        DefaultOption helpOpt = obuilder.withLongName("help").withDescription("Print out help").withShortName("h").create();
        Group group = gbuilder.withName("Options").withOption((Option)minSupportOpt).withOption((Option)analyzerNameOpt).withOption((Option)chunkSizeOpt).withOption((Option)outputDirOpt).withOption((Option)inputDirOpt).withOption((Option)minDFOpt).withOption((Option)maxDFSigmaOpt).withOption((Option)maxDFPercentOpt).withOption((Option)weightOpt).withOption((Option)powerOpt).withOption((Option)minLLROpt).withOption((Option)numReduceTasksOpt).withOption((Option)maxNGramSizeOpt).withOption((Option)overwriteOutput).withOption((Option)helpOpt).withOption((Option)sequentialAccessVectorOpt).withOption((Option)namedVectorOpt).withOption((Option)logNormalizeOpt).create();
        try {
            boolean processIdf;
            Parser parser = new Parser();
            parser.setGroup(group);
            parser.setHelpOption((Option)helpOpt);
            CommandLine cmdLine = parser.parse(args);
            if (cmdLine.hasOption((Option)helpOpt)) {
                CommandLineUtil.printHelp((Group)group);
                return -1;
            }
            Path inputDir = new Path((String)cmdLine.getValue((Option)inputDirOpt));
            Path outputDir = new Path((String)cmdLine.getValue((Option)outputDirOpt));
            int chunkSize = 100;
            if (cmdLine.hasOption((Option)chunkSizeOpt)) {
                chunkSize = Integer.parseInt((String)cmdLine.getValue((Option)chunkSizeOpt));
            }
            int minSupport = 2;
            if (cmdLine.hasOption((Option)minSupportOpt)) {
                String minSupportString = (String)cmdLine.getValue((Option)minSupportOpt);
                minSupport = Integer.parseInt(minSupportString);
            }
            int maxNGramSize = 1;
            if (cmdLine.hasOption((Option)maxNGramSizeOpt)) {
                try {
                    maxNGramSize = Integer.parseInt(cmdLine.getValue((Option)maxNGramSizeOpt).toString());
                }
                catch (NumberFormatException ex) {
                    log.warn("Could not parse ngram size option");
                }
            }
            log.info("Maximum n-gram size is: {}", (Object)maxNGramSize);
            if (cmdLine.hasOption((Option)overwriteOutput)) {
                HadoopUtil.delete((Configuration)this.getConf(), (Path[])new Path[]{outputDir});
            }
            float minLLRValue = 1.0f;
            if (cmdLine.hasOption((Option)minLLROpt)) {
                minLLRValue = Float.parseFloat(cmdLine.getValue((Option)minLLROpt).toString());
            }
            log.info("Minimum LLR value: {}", (Object)Float.valueOf(minLLRValue));
            int reduceTasks = 1;
            if (cmdLine.hasOption((Option)numReduceTasksOpt)) {
                reduceTasks = Integer.parseInt(cmdLine.getValue((Option)numReduceTasksOpt).toString());
            }
            log.info("Number of reduce tasks: {}", (Object)reduceTasks);
            Class analyzerClass = StandardAnalyzer.class;
            if (cmdLine.hasOption((Option)analyzerNameOpt)) {
                String className = cmdLine.getValue((Option)analyzerNameOpt).toString();
                analyzerClass = Class.forName(className).asSubclass(Analyzer.class);
                AnalyzerUtils.createAnalyzer(analyzerClass);
            }
            if (cmdLine.hasOption((Option)weightOpt)) {
                String wString = cmdLine.getValue((Option)weightOpt).toString();
                if ("tf".equalsIgnoreCase(wString)) {
                    processIdf = false;
                } else {
                    if (!"tfidf".equalsIgnoreCase(wString)) throw new OptionException((Option)weightOpt);
                    processIdf = true;
                }
            } else {
                processIdf = true;
            }
            int minDf = 1;
            if (cmdLine.hasOption((Option)minDFOpt)) {
                minDf = Integer.parseInt(cmdLine.getValue((Option)minDFOpt).toString());
            }
            int maxDFPercent = 99;
            if (cmdLine.hasOption((Option)maxDFPercentOpt)) {
                maxDFPercent = Integer.parseInt(cmdLine.getValue((Option)maxDFPercentOpt).toString());
            }
            double maxDFSigma = -1.0;
            if (cmdLine.hasOption((Option)maxDFSigmaOpt)) {
                maxDFSigma = Double.parseDouble(cmdLine.getValue((Option)maxDFSigmaOpt).toString());
            }
            float norm = -1.0f;
            if (cmdLine.hasOption((Option)powerOpt)) {
                String power = cmdLine.getValue((Option)powerOpt).toString();
                norm = "INF".equals(power) ? 999999 : Float.parseFloat(power);
            }
            boolean logNormalize = false;
            if (cmdLine.hasOption((Option)logNormalizeOpt)) {
                logNormalize = true;
            }
            log.info("Tokenizing documents in {}", (Object)inputDir);
            Configuration conf = this.getConf();
            conf.addResource(new Path("/usr/local/hadoop/etc/hadoop/core-site.xml"));
            Path tokenizedPath = new Path(outputDir, "tokenized-documents");
            DocumentProcessor.tokenizeDocuments((Path)inputDir, analyzerClass, (Path)tokenizedPath, (Configuration)conf);
            boolean sequentialAccessOutput = false;
            if (cmdLine.hasOption((Option)sequentialAccessVectorOpt)) {
                sequentialAccessOutput = true;
            }
            boolean namedVectors = false;
            if (cmdLine.hasOption((Option)namedVectorOpt)) {
                namedVectors = true;
            }
            boolean shouldPrune = maxDFSigma >= 0.0 || (double)maxDFPercent > 0.0;
            String tfDirName = shouldPrune ? "tf-vectors-toprune" : "tf-vectors";
            log.info("Creating Term Frequency Vectors");
            if (processIdf) {
                DictionaryVectorizer.createTermFrequencyVectors((Path)tokenizedPath, (Path)outputDir, (String)tfDirName, (Configuration)conf, (int)minSupport, (int)maxNGramSize, (float)minLLRValue, (float)-1.0f, (boolean)false, (int)reduceTasks, (int)chunkSize, (boolean)sequentialAccessOutput, (boolean)namedVectors);
            } else {
                DictionaryVectorizer.createTermFrequencyVectors((Path)tokenizedPath, (Path)outputDir, (String)tfDirName, (Configuration)conf, (int)minSupport, (int)maxNGramSize, (float)minLLRValue, (float)norm, (boolean)logNormalize, (int)reduceTasks, (int)chunkSize, (boolean)sequentialAccessOutput, (boolean)namedVectors);
            }
            Pair docFrequenciesFeatures = null;
            if (shouldPrune || processIdf) {
                log.info("Calculating IDF");
                docFrequenciesFeatures = TFIDFConverter.calculateDF((Path)new Path(outputDir, tfDirName), (Path)outputDir, (Configuration)conf, (int)chunkSize);
            }
            long maxDF = maxDFPercent;
            if (shouldPrune) {
                long vectorCount = ((Long[])docFrequenciesFeatures.getFirst())[1];
                if (maxDFSigma >= 0.0) {
                    Path dfDir = new Path(outputDir, "df-count");
                    Path stdCalcDir = new Path(outputDir, "stdcalc");
                    double stdDev = BasicStats.stdDevForGivenMean((Path)dfDir, (Path)stdCalcDir, (double)0.0, (Configuration)conf);
                    maxDF = (int)(100.0 * maxDFSigma * stdDev / (double)vectorCount);
                }
                long maxDFThreshold = (long)((float)vectorCount * ((float)maxDF / 100.0f));
                Path tfDir = new Path(outputDir, tfDirName);
                Path prunedTFDir = new Path(outputDir, "tf-vectors");
                Path prunedPartialTFDir = new Path(outputDir, "tf-vectors-partial");
                log.info("Pruning");
                if (processIdf) {
                    HighDFWordsPruner.pruneVectors((Path)tfDir, (Path)prunedTFDir, (Path)prunedPartialTFDir, (long)maxDFThreshold, (long)minDf, (Configuration)conf, (Pair)docFrequenciesFeatures, (float)-1.0f, (boolean)false, (int)reduceTasks);
                } else {
                    HighDFWordsPruner.pruneVectors((Path)tfDir, (Path)prunedTFDir, (Path)prunedPartialTFDir, (long)maxDFThreshold, (long)minDf, (Configuration)conf, (Pair)docFrequenciesFeatures, (float)norm, (boolean)logNormalize, (int)reduceTasks);
                }
                HadoopUtil.delete((Configuration)new Configuration(conf), (Path[])new Path[]{tfDir});
            }
            if (!processIdf) return 0;
            TFIDFConverter.processTfIdf((Path)new Path(outputDir, "tf-vectors"), (Path)outputDir, (Configuration)conf, (Pair)docFrequenciesFeatures, (int)minDf, (long)maxDF, (float)norm, (boolean)logNormalize, (boolean)sequentialAccessOutput, (boolean)namedVectors, (int)reduceTasks);
            return 0;
        }
        catch (OptionException e) {
            log.error("Exception", (Throwable)e);
            CommandLineUtil.printHelp((Group)group);
        }
        return 0;
    }
}

