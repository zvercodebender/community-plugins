package ext.deployit.community.cli.manifestexport.service;

import static java.lang.String.format;

import com.xebialabs.deployit.cli.rest.ResponseExtractor;
import com.xebialabs.deployit.core.api.RepositoryProxy;
import com.xebialabs.deployit.core.api.dto.RepositoryObject;

public class RepositoryHelper {
    private final RepositoryProxy repository;

    public RepositoryHelper(RepositoryProxy repository) {
        this.repository = repository;
    }

    public RepositoryObject readExisting(String id) {
        if (!exists(id)) {
            throw new IllegalArgumentException(format("No item with ID '%s' found in the repository", id));
        }
        return new ResponseExtractor(repository.read(id)).getEntity();
    }

    // copied from RepositoryClient
    public boolean exists(String id) {
        ResponseExtractor responseExtractor = new ResponseExtractor(repository.exists(id));
        return responseExtractor.isValidResponse();
    }
}
