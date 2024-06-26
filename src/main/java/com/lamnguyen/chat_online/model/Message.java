package com.lamnguyen.chat_online.model;

import com.lamnguyen.chat_online.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.value.qual.EnumVal;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    @NotNull(message = "Message is not null!")
    @NotBlank(message = "Message is not null!")
    private String message;

    @NotNull(message = "User send is not null!")
    @NotBlank(message = "User send is not null!")
    private String userId;

    @EnumVal({"SENT", "RECEIVED", "READ", "REDEEM"})
    private String status;

    private String time;


    public @NotNull(message = "Message is not null!") @NotBlank(message = "Message is not null!") String getMessage() {
        return message;
    }

    public void setMessage(@NotNull(message = "Message is not null!") @NotBlank(message = "Message is not null!") String message) {
        this.message = message;
    }

    public @NotNull(message = "User send is not null!") @NotBlank(message = "User send is not null!") String getUserId() {
        return userId;
    }

    public void setUserId(@NotNull(message = "User send is not null!") @NotBlank(message = "User send is not null!") String userId) {
        this.userId = userId;
    }

    public @EnumVal({"SENT", "RECEIVED", "READ", "REDEEM"}) String getStatus() {
        return status;
    }

    public void setStatus(@EnumVal({"SENT", "RECEIVED", "READ", "REDEEM"}) String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
