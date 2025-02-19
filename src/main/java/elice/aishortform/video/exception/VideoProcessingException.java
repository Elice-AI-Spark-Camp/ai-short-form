package elice.aishortform.video.exception;

public class VideoProcessingException extends RuntimeException {
	public VideoProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public VideoProcessingException(String message) {
		super(message);
	}
}
