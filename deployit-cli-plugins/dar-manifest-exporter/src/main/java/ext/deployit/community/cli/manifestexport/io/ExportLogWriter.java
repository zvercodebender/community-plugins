package ext.deployit.community.cli.manifestexport.io;

import static com.google.common.io.Files.createParentDirs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ExportLogWriter {
    protected static final String EXPORT_LOG_NAME = "export.log";

    public static File write(List<String> messages, String targetPath) 
            throws IOException {
        File exportLogFile = new File(targetPath + '/' + EXPORT_LOG_NAME);
        createParentDirs(exportLogFile);
        PrintWriter writer = new PrintWriter(exportLogFile);
        try {
            writer.println("The following issues were encountered during export:");
            // spacer line
            writer.println();
            for (String message : messages) {
                writer.println(message);
            }
            writer.flush();
        } finally {
            writer.close();
        }
        return exportLogFile;
    }
}
