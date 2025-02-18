package elice.aishortform.video.exception;

public class VideoNotFoundException extends RuntimeException {
	public VideoNotFoundException(Long videoId) {
		super("비디오 ID " + videoId + "를 찾을 수 없습니다.");
	}
}
