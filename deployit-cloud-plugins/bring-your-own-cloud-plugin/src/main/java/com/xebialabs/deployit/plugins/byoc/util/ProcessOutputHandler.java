package com.xebialabs.deployit.plugins.byoc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.xebialabs.deployit.plugin.api.flow.ExecutionContext;

public abstract class ProcessOutputHandler extends Thread {

        private InputStream is;
        private ExecutionContext ctx;

        public ProcessOutputHandler(ExecutionContext ctx) {
            this(ctx, null);
        }

        public ProcessOutputHandler(ExecutionContext ctx, InputStream is) {
            this.is = is;
            this.ctx = ctx;
        }
        
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                while ( (line = br.readLine()) != null) {
                    handleLine(ctx, line);    
                }
            } catch (IOException ioe) {
                ctx.logError("Error reading stdout", ioe);  
            }
        }

        protected void handleLine(ExecutionContext ctx, String line) {
        }

        public void startWithStream(InputStream inputStream) {
            this.is = inputStream;
            start();
        }
}
