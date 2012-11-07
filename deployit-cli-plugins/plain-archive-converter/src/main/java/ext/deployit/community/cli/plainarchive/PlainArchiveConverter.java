package ext.deployit.community.cli.plainarchive;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static ext.deployit.community.cli.plainarchive.dar.DarWriter.DAR_EXTENSION;
import static ext.deployit.community.cli.plainarchive.dar.DarWriter.MANIFEST_PATH;
import static ext.deployit.community.cli.plainarchive.io.Files2.getTempFilePath;
import static ext.deployit.community.cli.plainarchive.io.TFiles.listTFiles;
import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.xebialabs.deployit.cli.CliObject;
import com.xebialabs.deployit.cli.api.ProxiesInstance;

import de.schlichtherle.truezip.file.TFile;
import ext.deployit.community.cli.plainarchive.config.ConfigParser;
import ext.deployit.community.cli.plainarchive.config.RuleParser;
import ext.deployit.community.cli.plainarchive.dar.DarManifestBuilder;
import ext.deployit.community.cli.plainarchive.dar.DarManifestBuilder.DarEntry;
import ext.deployit.community.cli.plainarchive.dar.DarWriter;
import ext.deployit.community.cli.plainarchive.io.Filenames.VersionedFilename;
import ext.deployit.community.cli.plainarchive.matcher.CarMatcher.CarMatcherFactory;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher.MatchResult;
import ext.deployit.community.cli.plainarchive.matcher.ConfigurationItemMatcher.MatcherFactory;
import ext.deployit.community.cli.plainarchive.matcher.PathMatcher.PathMatcherFactory;
import ext.deployit.community.cli.plainarchive.matcher.RegexMatcher.RegexMatcherFactory;

@CliObject(name = "zipconverter")
public class PlainArchiveConverter {
    // make configurable?
    @VisibleForTesting
    static final String DEFAULT_APP_VERSION = "1.0";
    
    private static final String DAR_SUFFIX = "." + DAR_EXTENSION;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlainArchiveConverter.class);

    private static final String CONFIG_FILE_NAME = "plain-archive-converter.properties";
    private static final Properties CONFIG = new Properties();
    
    private static final Collection<MatcherFactory> MATCHER_FACTORIES = 
        ImmutableSet.of(new PathMatcherFactory(), new RegexMatcherFactory(),
                new CarMatcherFactory());
    
    static {
        try {
            CONFIG.load(checkNotNull(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE_NAME), CONFIG_FILE_NAME));
        } catch (Exception exception) {
            LOGGER.warn(format("Unable to load configuration file '%s' from classpath", 
                    CONFIG_FILE_NAME), exception);
        }
    }

    private final List<ConfigurationItemMatcher> matchers;
    
    public PlainArchiveConverter(ProxiesInstance ignored) {
        this(new ConfigParser(CONFIG, new RuleParser(MATCHER_FACTORIES)).get());
    }
    
    @VisibleForTesting
    protected PlainArchiveConverter(List<ConfigurationItemMatcher> matchers) {
        this.matchers = matchers;
    }
    
    public File convert(String sourcePath) throws IOException {
        VersionedFilename sourceName = VersionedFilename.from(
                getBaseName(sourcePath), DEFAULT_APP_VERSION);
        return convert(sourcePath, sourceName.name, sourceName.version);
    }
    
    public File convert(String sourcePath, String appName, String version) throws IOException {
        return convert(sourcePath, appName, version, 
                getTempFilePath(getBaseName(sourcePath), getExtension(sourcePath)));
    }

    public File convert(String sourcePath, String targetPath) throws IOException {
        VersionedFilename sourceName = VersionedFilename.from(
                getBaseName(sourcePath), DEFAULT_APP_VERSION);
        return convert(sourcePath, sourceName.name, sourceName.version, targetPath);
    }
    
    public File convert(String sourcePath, String appName, String version, String targetPath) throws IOException {
        if (!targetPath.endsWith(DAR_SUFFIX)) {
            targetPath += DAR_SUFFIX;
            LOGGER.info("Result file will be '{}' since DARs must end in '{}' to be importable", 
                    targetPath, DAR_SUFFIX);
        }
        File target = new File(targetPath);
        checkArgument(!target.exists(), "Target file '%s' already exists", targetPath);
        return convert(sourcePath, appName, version, target);
    }
    
    private File convert(String sourcePath, String appName, String version, File target) throws IOException {
        TFile sourceArchive = new TFile(sourcePath);
        checkValidArchive(sourceArchive);
        
        /*
         * Matched TFiles return a DarEntry, unmatched return NULL. The final
         * Iterable has had all the NULLs filtered out. 
         */
        Iterable<DarEntry> collectedEntries = filter(transform(listTFiles(sourceArchive),
                new Function<TFile, DarEntry>() {
                    @Override
                    public DarEntry apply(TFile input) {
                        for (ConfigurationItemMatcher matcher : matchers) {
                            MatchResult result = matcher.apply(input);
                            // first match wins
                            if (result.matched) {
                                LOGGER.debug("Matched '{}' to DAR entry '{}'", input, 
                                        result.result);
                                return result.result;
                            }
                        }
                        // no match
                        return DarEntry.NULL;
                    }
                }), not(new Predicate<DarEntry>() {
                    @Override
                    public boolean apply(DarEntry input) {
                        return (input == DarEntry.NULL);
                    }
                }));

        // the DAR will just be a copy of the source with a manifest
        Files.copy(sourceArchive, target);
        LOGGER.debug("Copied source archive {} to new target {}", sourceArchive, target);
        
        LOGGER.info("Converting plain archive '{}' to DAR '{}' for '{}' version '{}'",
                new Object[] { sourceArchive, target, appName, version });
        LOGGER.debug("Adding manifest to new target {}", target);
        DarWriter.addManifest(target, new DarManifestBuilder()
            .setApplication(appName)
            .setVersion(version)
            .addDarEntries(collectedEntries)
            .build());
        DarWriter.flush(target);
        return target;
    }
    
    private static void checkValidArchive(TFile archive) {
        checkArgument(archive.exists(), "Archive '%s' does not exist or cannot be read", archive);
        checkArgument(archive.isArchive(), "File '%s' is not a valid ZIP archive", archive);
        checkArgument(!(new TFile(archive, MANIFEST_PATH).exists()), 
                "Archive '%s' already contains a manifest at '%s'", archive, MANIFEST_PATH);
    }
}