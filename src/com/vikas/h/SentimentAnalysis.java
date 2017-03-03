package com.vikas.h;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class SentimentAnalysis extends Configured implements Tool
{
public static File file;


public static void main(String[] args) throws Exception {
	  Configuration conf = new Configuration();
	  //  String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	 // int ret =ToolRunner.run(new SentimentAnalysis(), otherArgs);
    int ret = ToolRunner.run(new SentimentAnalysis(), args);
    System.exit(ret);
}

@Override
public int run(String[] args) throws Exception {
	/*System.out.println(args.length);
    if(args.length < 2) {
        System.out.println("Invalid input and output directories");
        return -1;
    }
    */
	//Path in = new Path(mrCluster.getTestWorkDir().getAbsolutePath(), "testCombinerShouldUpdateTheReporter-in");
    //Path out = new Path(mrCluster.getTestWorkDir().getAbsolutePath(),);
    JobConf conf = new JobConf(getConf(), SentimentAnalysis.class);
    conf.setJobName("sentimentanalysis");
    conf.setJarByClass(SentimentAnalysis.class);
    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(IntWritable.class);
    conf.setMapOutputKeyClass(Text.class);
    conf.setMapOutputValueClass(IntWritable.class);
    conf.setMapperClass(Map.class);
    //conf.setCombinerClass(Reduce.class);
    conf.setReducerClass(Reduce.class);
    conf.setInputFormat(org.apache.hadoop.mapred.TextInputFormat.class);
    conf.setOutputFormat(org.apache.hadoop.mapred.TextOutputFormat.class);
    FileInputFormat.setInputPaths(conf, new Path("/home/vikas/Desktop/aaa"));
    FileOutputFormat.setOutputPath(conf, new Path("/home/vikas/Desktop/aaa1"));
    file = new File("/home/vikas/Desktop/t");
    JobClient.runJob(conf);
    return 0;
}
}