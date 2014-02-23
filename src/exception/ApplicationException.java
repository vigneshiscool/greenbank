package exception;

/**
 * @author Vignesh
 */
public class ApplicationException extends Exception {
	
	private static final long serialVersionUID = 2180348972548317896L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String exceptionArgString, Throwable exceptionArgClass) {
		super(exceptionArgString, exceptionArgClass);
		System.out.println(exceptionArgString);
		System.out.println(exceptionArgClass);

	}

	/**
	 * @param exceptionArgString
	 */
	public ApplicationException(String exceptionArgString) {
		super(exceptionArgString);
		System.out.println(exceptionArgString);

	}

	/**
	 * @param exceptionArgString
	 */
	public ApplicationException(Throwable exceptionArgString) {
		super(exceptionArgString);
		System.out.println(exceptionArgString);
 	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + getMessage() + ", " + getClass() + "]";
	}

}
