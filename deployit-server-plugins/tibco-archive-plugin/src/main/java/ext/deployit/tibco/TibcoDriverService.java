package ext.deployit.tibco;

import java.util.Map;

import de.schlichtherle.truezip.fs.FsDriver;
import de.schlichtherle.truezip.fs.FsScheme;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.fs.spi.FsDriverService;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TibcoDriverService extends FsDriverService {
    private final Map<FsScheme, FsDriver> DRIVERS;

    public TibcoDriverService() {
        logger.info("Loading Tibco archive driver...");
        DRIVERS = newMap(new Object[][] {
                { "par", new JarDriver(IOPoolLocator.SINGLETON) },
                { "sar", new JarDriver(IOPoolLocator.SINGLETON) }
        });
    }

    @Override
    public Map<FsScheme, FsDriver> get() {
        return DRIVERS;
    }

    private static final Logger logger = LoggerFactory.getLogger(TibcoDriverService.class);
}
