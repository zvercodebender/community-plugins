package ext.deployit.community.cli.manifestexport.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.xebialabs.deployit.cli.api.Proxies;
import com.xebialabs.deployit.cli.rest.ResponseExtractor;
import com.xebialabs.deployit.core.api.dto.ConfigurationItemDescriptorDto;
import com.xebialabs.deployit.core.api.dto.ConfigurationItemDescriptorList;
import com.xebialabs.deployit.core.api.dto.ConfigurationItemPropertyDescriptorDto;
import com.xebialabs.deployit.core.api.dto.RepositoryObject;

public class TypeReflectionHelper {
    private final Map<String, ConfigurationItemDescriptorDto> descriptors;

    public TypeReflectionHelper(Proxies proxies) {
        ConfigurationItemDescriptorList descriptorList = 
            (ConfigurationItemDescriptorList)(new ResponseExtractor(proxies.getReferenceData().list())).getEntity();
        Builder<String, ConfigurationItemDescriptorDto> typesToDescriptors = ImmutableMap.builder();
        for (ConfigurationItemDescriptorDto descriptor : descriptorList.getDescriptors()) {
            typesToDescriptors.put(descriptor.getType(), descriptor);
        }
        descriptors = typesToDescriptors.build();
    }

    public ConfigurationItemDescriptorDto getDescriptor(RepositoryObject ci) {
        return descriptors.get(ci.getType());
    }

    public List<ConfigurationItemPropertyDescriptorDto> getPropertyDescriptors(RepositoryObject ci) {
        return getDescriptor(ci).getPropertyDescriptors();
    }
}
