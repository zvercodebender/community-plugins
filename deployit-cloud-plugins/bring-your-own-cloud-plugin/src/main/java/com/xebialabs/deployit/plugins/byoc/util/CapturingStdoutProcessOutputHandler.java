package com.xebialabs.deployit.plugins.byoc.util;

import java.io.InputStream;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;

public class CapturingStdoutProcessOutputHandler extends StdoutProcessOutputHandler {
    // needs to be synchronized!
    private final StringBuffer sb = new StringBuffer();
    
    public CapturingStdoutProcessOutputHandler(InputStream is, ExecutionContext ctx) {
        super(is, ctx);
    }

    public CapturingStdoutProcessOutputHandler(ExecutionContext context) {
        super(context);
    }

    protected void handleLine(ExecutionContext ctx, String line) {
        super.handleLine(ctx, line);
        sb.append(line);
    }
    
    public String getOutput() {
        return sb.toString();
    }
}
