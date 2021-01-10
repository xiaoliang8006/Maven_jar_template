package com.demo;

import org.apache.commons.cli.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

class MyFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date now = new Date();
        String timeStr = sdf.format(now);
        return String.format("%s %s: %s\n", timeStr, record.getLevel(), record.getMessage());
    }
}

public final class MySparkRun {
    private static final Logger logger = Logger.getLogger("MySparkRun");
    public static String appName = "qb-user-profile@[type=experiment,name=ericoliang]";
    public static String inputHdfsPath;
    public static String outputHdfsPath;

    /**
     * 解析输入参数
     */
    public boolean createOptions(String[] args) {
        Options options = new Options();
        Option opt = new Option("h", "help", false, "Print help.");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("n", "app_name", true, "spark app name.");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("i", "input_path", true, "input path on hdfs.");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("o", "output_path", true, "output path on hdfs.");
        opt.setRequired(false);
        options.addOption(opt);

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        CommandLine cmd;
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                hf.printHelp("Updater", options, true);
            }

            if (cmd.hasOption("app_name")) {
                appName = cmd.getOptionValue("app_name");
            }
            inputHdfsPath = cmd.getOptionValue("input_path");
            outputHdfsPath = cmd.getOptionValue("output_path");
        } catch (ParseException e) {
            System.err.println("Error! Message:" + e.getMessage());
            hf.printHelp("Updater", options, true);
            return false;
        }

        logger.info("================================================================================");
        logger.info("Parameters of : input_path: " + MySparkRun.inputHdfsPath);
        logger.info("Parameters of : output_path: " + MySparkRun.outputHdfsPath);
        logger.info("================================================================================");
        return true;
    }

    /**
     * 运行任务
     */
    public static void run(String appName, String inputHdfsPath, String outputHdfsPath) {
        JavaSparkContext sc = new JavaSparkContext("yarn", appName);
        JavaRDD<String> lines = sc.textFile(inputHdfsPath, 1); //java.lang.String path, int minSplits
        //collect方法，用于将RDD类型转化为java基本类型，如下
        List<String> line = lines.collect();
        for(String val:line)
            System.out.println(val);

        ParseProtobuf parseExecutor = new ParseProtobuf();
        JavaRDD<String> results = lines.map(s -> parseExecutor.ParseProtobufStruct(s));
        // 保存函数，数据输出，spark为结果输出提供了很多接口
        // lines.saveAsNewAPIHadoopFile();
        // lines.saveAsHadoopFile();
        results.repartition(3).saveAsTextFile(outputHdfsPath);
    }

    public static void main(String[] args) throws Exception {
        logger.setLevel(Level.INFO);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new MyFormatter());
        logger.addHandler(consoleHandler);

        MySparkRun updaterObj = new MySparkRun();
        if (!updaterObj.createOptions(args)) {
            System.exit(-1);
        }
        run(appName,inputHdfsPath, outputHdfsPath);
        logger.info("Finished!");
        System.exit(0);
    }

}

