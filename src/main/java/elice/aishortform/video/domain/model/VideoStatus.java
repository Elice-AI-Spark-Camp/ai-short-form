package elice.aishortform.video.domain.model;

public enum VideoStatus {
	PENDING,     // 생성 대기 중
	PROCESSING,  // 생성 중
	COMPLETED,   // 생성 완료
	FAILED       // 생성 실패
}
