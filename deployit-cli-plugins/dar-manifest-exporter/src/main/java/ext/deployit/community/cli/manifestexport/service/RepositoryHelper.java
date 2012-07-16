package ext.deployit.community.cli.manifestexport.service;

import static java.lang.String.format;

import com.xebialabs.deployit.engine.api.RepositoryService;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

public class RepositoryHelper {
    private final RepositoryService repository;

    public RepositoryHelper(RepositoryService repository) {
        this.repository = repository;
    }

    public ConfigurationItem readExisting(String id) {
        if (!exists(id)) {
            throw new IllegalArgumentException(format("No item with ID '%s' found in the repository", id));
        }
        return repository.read(id);
    }

    // copied from RepositoryClient
    public boolean exists(String id) {
        return repository.exists(id);
    }
}
