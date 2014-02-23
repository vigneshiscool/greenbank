package filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class RequestWrapper extends HttpServletRequestWrapper {
	private static Logger logger = Logger.getLogger(RequestWrapper.class);
	
    private static Pattern[] patterns = new Pattern[]{
        // Script fragments
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // lonely script tags
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
	
	public RequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}
	
	

	public String[] getParameterValues(String parameter) {
		logger.info("In getParameterValues .. parameter .......");
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}

	public String getParameter(String parameter) {
		logger.info("In getParameter .. parameter .......");
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		logger.info("In getParameter RequestWrapper ........ value .......");
		return cleanXSS(value);
	}

	public String getHeader(String name) {
		logger.info("In getHeader .. parameter .......");
		String value = super.getHeader(name);
		if (value == null)
			return null;
		logger.info("In getHeader RequestWrapper ........... value ....");
		return cleanXSS(value);
	}

	private String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		String inv = "IN-VALID-GREENBANK";
		logger.info("In cleanXSS RequestWrapper ..............." + value);
		value = value.replaceAll("<script>",inv );
		value = value.replaceAll("javascript:*", inv);
		if(value.contains(inv)){
			return inv;
		}
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		
		
		value = value.replaceAll("(?i)<script.*?>.*?<script.*?>", "");
		value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
		value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
		value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");
		//value = value.replaceAll("<script>", "");
		//value = value.replaceAll("</script>", "");
//        for (Pattern scriptPattern : patterns){
//            value = scriptPattern.matcher(value).replaceAll("");
//        }
		logger.info("Out cleanXSS RequestWrapper ........ value ......." + value);
		return value;
	}
}