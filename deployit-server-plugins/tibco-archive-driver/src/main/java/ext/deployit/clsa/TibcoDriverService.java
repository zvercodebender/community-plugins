package ext.deployit.clsa;

import java.util.Map;

import de.schlichtherle.truezip.fs.FsDriver;
import de.schlichtherle.truezip.fs.FsScheme;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.fs.spi.FsDriverService;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class TibcoDriverService extends FsDriverService {
    private final Map<FsScheme, FsDriver> DRIVERS;

    public TibcoDriverService() {
        DRIVERS = newMap(new Object[][] {
                { "par", new JarDriver(IOPoolLocator.SINGLETON) },
                { "sar", new JarDriver(IOPoolLocator.SINGLETON) }
        });
    }

    @Override
    public Map<FsScheme, FsDriver> get() {
        return DRIVERS;
    }
}
