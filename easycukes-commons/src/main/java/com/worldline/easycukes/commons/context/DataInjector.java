package com.worldline.easycukes.commons.context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * This {@link DataInjector} class allows to deal with dynamic data to be used
 * in the tests scenarios. It allows to introduce a way of defining variables
 * using the <i>$__variable__$</i> syntax.
 * 
 * It simply generates some random values for the variables and injects it in
 * the execution context.
 * 
 * @author aneveux
 * @version 1.0
 */
public class DataInjector {

	/**
	 * Token allowing to define the start of a variable
	 */
	public final static String TOKEN_START = "$__";

	/**
	 * Token allowing to define the end of a variable
	 */
	public final static String TOKEN_END = "__$";

	/**
	 * {@link Pattern} allowing to find the variables in the {@link String}
	 * elements to be submitted
	 */
	protected final static Pattern p = Pattern.compile("\\$__([a-zA-Z]+)__\\$");

	/**
	 * Searches for variables in the provided {@link String}, then replaces them
	 * by some random generated {@link String} (it uses internally
	 * {@link RandomStringUtils} from Apache Commons. Finally, it adds the
	 * generated variable in the execution context so it can be retrieved using
	 * the name of the variable. Also, if the execution context was already
	 * containing a generated value for the variable, it just retrieves it from
	 * the context, and injects the data directly.
	 * 
	 * @param s
	 *            {@link String} in which you'd like variables to be generated /
	 *            injected
	 * @return the same {@link String} as provided, except that variables will
	 *         be replaced by the value present in the execution context
	 */
	public static String injectData(final String s) {
		// 0. We store the initial string in order to use it for injecting
		// tokens
		String result = s;
		// 1. Apply pattern on the string to extract all tokens
		final Matcher m = p.matcher(s);
		// 2. While tokens are processed
		while (m.find()) {
			// 3. We extract the token content
			String token = m.group(1);
			// 4. If the token has never been identified and is not present in
			// the context
			if (!ExecutionContext.contains(token)
					|| ExecutionContext.get(token) == null) {
				// 5. If the token is present in the config file
				if (Configuration.get(token) != null)
					// 6. From the config file, we get a value for the token and
					// put it in the context
					ExecutionContext.put(token, Configuration.get(token));
				else {
					// 7. We generate a value for the token and put it in
					// the
					// context
					ExecutionContext.put(token, "ktest-"
							+ RandomStringUtils.randomAlphabetic(11)
									.toLowerCase());
				}
			}
			// 8. Then, we'll inject the value from the context directly in the
			// result
			result = result.replace(TOKEN_START + token + TOKEN_END,
					ExecutionContext.get(token));
		}
		// 7. Finally we return the result, which is the string with data
		// injected in it
		return result;
		// 8. BTW, all the tokens can be found from the context, with the random
		// values associated
	}

}
