package elice.aishortform.global.exception;

import java.util.concurrent.CompletionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import elice.aishortform.video.exception.SummaryNotFoundException;
import elice.aishortform.video.exception.VideoNotFoundException;
import elice.aishortform.video.exception.VideoProcessingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SummaryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSummaryNotFoundException(SummaryNotFoundException ex) {
        log.error("요약 정보를 찾을 수 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVideoNotFoundException(VideoNotFoundException ex) {
        log.error("비디오를 찾을 수 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(VideoProcessingException.class)
    public ResponseEntity<ErrorResponse> handleVideoProcessingException(VideoProcessingException ex) {
        log.error("비디오 처리 오류: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("PROCESSING_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
        log.error("비동기 요청 타임아웃: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
            .body(new ErrorResponse("REQUEST_TIMEOUT", "비동기 요청 처리 시간이 초과되었습니다."));
    }
    
    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ErrorResponse> handleCompletionException(CompletionException ex) {
        log.error("비동기 작업 완료 예외: {}", ex.getMessage());
        
        // CompletionException의 원인 예외에 따라 다른 응답 반환
        Throwable cause = ex.getCause();
        if (cause instanceof SummaryNotFoundException) {
            return handleSummaryNotFoundException((SummaryNotFoundException) cause);
        } else if (cause instanceof VideoProcessingException) {
            return handleVideoProcessingException((VideoProcessingException) cause);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ASYNC_ERROR", "비동기 작업 처리 중 오류가 발생했습니다: " + cause.getMessage()));
        }
    }
    
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("WebClient 응답 오류: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String code = "API_ERROR_" + status.value();
        
        return ResponseEntity.status(status)
            .body(new ErrorResponse(code, "외부 API 호출 오류: " + ex.getStatusText() + " - " + ex.getResponseBodyAsString()));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("잘못된 인자 값: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("서버 내부 오류: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
