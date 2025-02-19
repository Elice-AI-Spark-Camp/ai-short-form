package elice.aishortform.video.dto;

import elice.aishortform.video.domain.model.Video;

public record VideoResponse(Long videoId, String status, String videoUrl) {
	public static VideoResponse from(Video video) {
		return new VideoResponse(video.getId(), video.getStatus().name(), video.getVideoUrl());
	}
}
