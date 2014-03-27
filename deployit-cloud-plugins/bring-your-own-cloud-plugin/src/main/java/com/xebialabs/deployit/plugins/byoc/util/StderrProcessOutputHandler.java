package com.xebialabs.deployit.plugins.byoc.util;

import java.io.InputStream;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;

public class StderrProcessOutputHandler extends ProcessOutputHandler {

    public StderrProcessOutputHandler(InputStream is, ExecutionContext ctx) {
        super(ctx, is);
    }

    public StderrProcessOutputHandler(ExecutionContext ctx) {
        super(ctx);
    }
    
    protected void handleLine(ExecutionContext ctx, String line) {
        ctx.logError(line);
    }
}
