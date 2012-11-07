package ext.deployit.community.plugin.precheck.util;

/**
 * Rationale: We don't want to depend on the apache-lang library, but guava doesn't offer all the nice stringutils stuff, so roll our own...
 */
public class Strings {

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static String defaultIfEmpty(String s, String defaultValue) {
		return isEmpty(s) ? defaultValue : s;
	}
}
