package org.example.cmd;

import org.example.reportResolver.Merge;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@CommandLine.Command(
        name = "merge"
)
public class CmdMerge implements Callable<Integer> {
    @CommandLine.Option(
            names = {"--source"}, description = "source file"
    )
    File source;
    @CommandLine.Option(
            names = {"--target"}, description = "target directory"
    )
    File target;

    @CommandLine.Option(
            names = {"--bk-dir"}, description = "backup directory"
    )
    File backupDir;

    @CommandLine.Option(
            names = {"--url"}, description = "url"
    )
    String url;

    @CommandLine.Option(
            names = {"--user"}, description = "user email"
    )
    String user;
    @Override
    public Integer call() throws Exception {
        if((!target.exists()) || (!target.isDirectory())){
            throw new RuntimeException("Target directory not exists or not directory: "+target);
        }
        for(File file : target.listFiles()){
            final var pattern = Pattern.compile("cucumber.json_\\d+");
            if(!pattern.matcher(file.getName()).matches())
                continue;
            Merge.merge(source, file, backupDir, url, user);
        }
        return 0;
    }
}
