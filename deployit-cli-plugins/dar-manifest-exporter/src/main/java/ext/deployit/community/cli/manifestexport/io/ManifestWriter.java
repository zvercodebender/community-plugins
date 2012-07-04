package ext.deployit.community.cli.manifestexport.io;

import static com.google.common.io.Files.createParentDirs;
import static java.util.jar.JarFile.MANIFEST_NAME;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.Manifest;

public class ManifestWriter {

    public static File write(Manifest manifest, String targetPath) 
            throws IOException {
        File manifestFile = new File(targetPath + '/' + MANIFEST_NAME);
        createParentDirs(manifestFile);
        OutputStream manifestOutput = new BufferedOutputStream(new FileOutputStream(manifestFile));
        try {
            manifest.write(manifestOutput);
        } finally {
            manifestOutput.close();
        }
        return manifestFile;
    }
}
