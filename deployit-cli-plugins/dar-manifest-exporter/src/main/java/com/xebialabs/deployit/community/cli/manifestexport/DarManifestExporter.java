package com.xebialabs.deployit.community.cli.manifestexport;

import java.io.File;
import java.io.IOException;

import com.xebialabs.deployit.cli.CliObject;
import com.xebialabs.deployit.cli.api.ProxiesInstance;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

import ext.deployit.community.cli.manifestexport.convert.PackageContentAnalyzer;
import ext.deployit.community.cli.manifestexport.convert.PackageContentAnalyzer.ManifestAndLogMessages;
import ext.deployit.community.cli.manifestexport.io.ExportLogWriter;
import ext.deployit.community.cli.manifestexport.io.ManifestWriter;
import ext.deployit.community.cli.manifestexport.service.RepositoryHelper;

@CliObject(name = "manifestexporter")
public class DarManifestExporter {
    private final RepositoryHelper repository;
    private final PackageContentAnalyzer packageAnalyzer;

    public DarManifestExporter(ProxiesInstance proxies) {
        repository = new RepositoryHelper(proxies.getRepository());
        packageAnalyzer = new PackageContentAnalyzer(repository);
    }

    public File export(String deploymentPackageId, String targetPath)
            throws IOException {
        ConfigurationItem deploymentPackage = repository.readExisting(deploymentPackageId);
        ManifestAndLogMessages manifestAndErrors =  packageAnalyzer.extractFromPackage(deploymentPackage);
        if (!manifestAndErrors.messages.isEmpty()) {
            ExportLogWriter.write(manifestAndErrors.messages, targetPath);
        }
        return ManifestWriter.write(manifestAndErrors.manifest, targetPath);
    }
}