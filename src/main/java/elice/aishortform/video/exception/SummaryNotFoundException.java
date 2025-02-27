package elice.aishortform.video.exception;

public class SummaryNotFoundException extends RuntimeException {
	public SummaryNotFoundException(String message) {
		super(message);
	}

	public SummaryNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}