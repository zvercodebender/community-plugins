package ext.deployit.community.plugin.clirunner.delegate;

import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebialabs.deployit.engine.spi.services.RepositoryFactory;
import com.xebialabs.deployit.plugin.api.services.Repository;

@Component
public class RepositoryHolder {
    private static final AtomicReference<RepositoryFactory> repositoryFactory = new AtomicReference<RepositoryFactory>();

    @Autowired
    public RepositoryHolder(RepositoryFactory repositoryFactory) {
        RepositoryHolder.repositoryFactory.set(repositoryFactory);
    }

    public static Repository getRepository() {
        return RepositoryHolder.repositoryFactory.get().create();
    }
}