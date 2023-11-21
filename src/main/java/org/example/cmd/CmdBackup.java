package org.example.cmd;

import org.example.reportResolver.Backup;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "backup"
)
public class CmdBackup implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-in"}, description = "in file"
    )
    File in;

    @CommandLine.Option(
            names = {"-out"}, description = "out directory"
    )
    File out;
    @Override
    public Integer call() throws Exception {
        Backup.copy(in, out);
        return 0;
    }
}
