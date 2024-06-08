package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


@Getter
public class ApiError {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @JsonIgnore
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;

    public ApiError(Exception e, String message, String reason, HttpStatus status) {
        this.errors = Collections.singletonList(stackTraceToString(e));
        this.message = message;
        this.reason = reason;
        this.status = status.getReasonPhrase().toUpperCase();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public ApiError(Exception e, HttpStatus status) {
        this.errors = Collections.singletonList(stackTraceToString(e));
        this.message = e.getMessage();
        this.reason = "Произошла непредвиденная ошибка";
        this.status = status.getReasonPhrase().toUpperCase();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    private String stackTraceToString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

}