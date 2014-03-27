package com.xebialabs.deployit.plugins.byoc.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;

public class CommandRunner {
    public static final Logger LOGGER = LoggerFactory.getLogger(CommandRunner.class);

    private final String[] command;
    private final ProcessOutputHandler stdout;
    private final ProcessOutputHandler stderr;
    private final String workingDir;

    public CommandRunner(String workingDir, String[] command, ProcessOutputHandler stdout, ProcessOutputHandler stderr) {
        this.workingDir = workingDir;
        this.command = command;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int run(ExecutionContext context) throws Exception {
        LOGGER.debug("Invoking command '%s' in directory '%s'", command, workingDir);

        context.logOutput("Invoking command in directory " + workingDir);
        try {
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec(command, null, new File(workingDir));

            stdout.startWithStream(p.getInputStream());
            stderr.startWithStream(p.getErrorStream());
        
            return p.waitFor();
        } catch(Throwable t) {
            context.logError("Error running command: " + t);
            return -1;
        }
    }
}
