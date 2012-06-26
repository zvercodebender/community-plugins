package ext.deployit.community.plugin.changemgmt.reflect;

import java.util.Collection;

import com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry;
import com.xebialabs.deployit.plugin.api.reflect.Type;

/**
 * From <a href="https://github.com/demobox/generic-plugin-extensions/blob/master/src/main/java/com/xebialabs/deployit/plugin/api/reflect/Types.java">generic-plugin-extensions</a>
 */
public class Types {
	public static boolean isSubtypeOf(Type supertype, Type subtype) {
		Collection<Type> typeAndSubtypes = DescriptorRegistry.getSubtypes(supertype);
		typeAndSubtypes.add(supertype);
		return typeAndSubtypes.contains(subtype);
	}
}
