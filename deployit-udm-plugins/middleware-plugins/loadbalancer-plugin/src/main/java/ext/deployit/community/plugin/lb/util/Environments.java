package ext.deployit.community.plugin.lb.util;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Container;
import com.xebialabs.deployit.plugin.api.udm.Environment;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static ext.deployit.community.plugin.lb.util.Predicates2.instanceOf;

public class Environments {
    
    public static <T> Set<T> getMembersOfType(Environment environment, Type type) {
        return copyOf(transform(Sets.filter(environment.getMembers(), instanceOf(type)),
                new Function<Container, T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T apply(Container input) {
                        return (T) input;
                    }
                })
            );
    }
}
