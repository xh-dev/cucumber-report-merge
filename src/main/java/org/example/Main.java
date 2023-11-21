package org.example;

import org.example.cmd.CmdBackup;
import org.example.cmd.CmdExport;
import org.example.cmd.CmdMerge;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        mixinStandardHelpOptions = true,
        name = "cucumber-report-handler",
        subcommands = {
                CmdBackup.class,
                CmdMerge.class,
                CmdExport.class
        },
        description = "A simple program to backup and generate report"
)
public class Main implements Callable<Integer> {

    public static void main(String[] args) throws IOException {
//        Resolver.resolve(new File("C:\\Users\\XH20258\\Downloads\\temp\\cucumber-reports\\cucumber.json"));
//        Resolver.resolve(new File("/home/xeth/Downloads/temp/cucumber-reports/cucumber.json"));
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        CommandLine.usage(this, System.out);
        return 0;
    }
}