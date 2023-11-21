package org.example.cmd;

import org.example.reportResolver.Export;
import org.example.reportResolver.Merge;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@CommandLine.Command(
        name = "export"
)
public class CmdExport implements Callable<Integer> {
    @CommandLine.Option(
            names = {"--source"}, description = "source file"
    )
    File source;
    @CommandLine.Option(
            names = {"--destination"}, description = "destination file"
    )
    File target;

    @Override
    public Integer call() throws Exception {
        Export.export(source, target);
        return 0;
    }
}
