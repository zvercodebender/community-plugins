package ext.deployit.community.plugin.workdir.util;

import static com.xebialabs.deployit.plugin.api.deployment.specification.Operation.DESTROY;
import static com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry.getSubtypes;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.xebialabs.deployit.plugin.api.deployment.specification.Delta;
import com.xebialabs.deployit.plugin.api.deployment.specification.Operation;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployed;

public class Predicates2 {

    public static Predicate<Type> subtypeOfAny(Type... types) {
        return new IsSubtypeOfAny(types);
    }

    public static Predicate<Type> subtypeOf(Type type) {
        return subtypeOfAny(type);
    }

    public static Predicate<ConfigurationItem> instanceOfAny(Type... types) {
        return com.google.common.base.Predicates.compose(subtypeOfAny(types), 
                new Function<ConfigurationItem, Type>() {
                    @Override
                    public Type apply(ConfigurationItem input) {
                        return input.getType();
                    }
                });
    }

    public static Predicate<ConfigurationItem> instanceOf(Type type) {
        return instanceOfAny(type);
    }

    public static Predicate<Delta> deltaOfAny(Type... types) {
        return com.google.common.base.Predicates.compose(instanceOfAny(types), 
                extractDeployed());
    }

    public static Predicate<Delta> deltaOf(Type type) {
        return deltaOfAny(type);
    }

    public static <D extends Deployed<?, ?>> Function<Delta, D> extractDeployed() {
        return new ExtractDeployed<D>();
    }

    public static Predicate<Delta> operationIn(Operation... operationsToMatch) {
        return new OperationIn(operationsToMatch);
    }

    public static Predicate<Delta> operationIs(Operation operationToMatch) {
        return operationIn(operationToMatch);
    }

    private static class OperationIn implements Predicate<Delta> {
        private final Collection<Operation> operationsToMatch;

        protected OperationIn(Operation... operationsToMatch) {
            this.operationsToMatch = ImmutableSet.copyOf(operationsToMatch);
        }

        @Override
        public boolean apply(Delta input) {
            return operationsToMatch.contains(input.getOperation());
        }
    }

    private static class IsSubtypeOfAny implements Predicate<Type> {
        private final Collection<Type> subtypes;

        public IsSubtypeOfAny(Type... typesToMatch) {
            Builder<Type> subtypes = ImmutableSet.builder();
            for (Type typeToMatch : typesToMatch) {
                subtypes.addAll(getSubtypes(typeToMatch));
                subtypes.add(typeToMatch);
            }
            this.subtypes = subtypes.build();
        }

        @Override
        public boolean apply(Type input) {
            return subtypes.contains(input);
        }
    }

    private static class ExtractDeployed<D extends Deployed<?, ?>> implements Function<Delta, D> {
        @SuppressWarnings("unchecked")
        @Override
        public D apply(Delta input) {
            return (D) (input.getOperation().equals(DESTROY) 
                       ? input.getPrevious() 
                       : input.getDeployed());
        }
    }
}
