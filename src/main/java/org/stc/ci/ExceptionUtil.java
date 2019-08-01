package org.stc.ci;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {
	
	public static String stringStackTrace(final Exception ex) {
		if (ex == null) {
			return "";
		}

		final StringBuilder sbTrace = new StringBuilder();
		sbTrace.append(getExceptionTrace(ex));

		Throwable cause = ex.getCause();

		while (cause != null) {
			final String sb = "Cause : Exception : %1$s\nStackTrace : %2$s";
			sbTrace.append(String.format(sb, new Object[] { cause.getMessage(),
					getExceptionTrace(cause) }));

			cause = cause.getCause();
		}
		return sbTrace.toString();
	}

	private static String getExceptionTrace(final Throwable ex) {
		if (ex == null) {
			return "";
		}
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}
}
