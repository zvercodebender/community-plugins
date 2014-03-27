package com.xebialabs.deployit.plugins.byoc.util;

import java.io.InputStream;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;

public class StdoutProcessOutputHandler extends ProcessOutputHandler {

    public StdoutProcessOutputHandler(InputStream is, ExecutionContext ctx) {
        super(ctx, is);
    }
    
    public StdoutProcessOutputHandler(ExecutionContext ctx) {
        super(ctx);
    }

    protected void handleLine(ExecutionContext ctx, String line) {
        ctx.logOutput(line);
    }
}
