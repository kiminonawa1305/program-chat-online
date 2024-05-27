package com.lamnguyen.chat_online.controllers;

import com.google.firebase.database.*;
import com.lamnguyen.chat_online.enums.Status;
import com.lamnguyen.chat_online.model.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private DatabaseReference reference;
    @Autowired
    private DateTimeFormatter formatter;

    @GetMapping
    private String test(){
        return "Hello World";
    }

    @GetMapping("/{type}/{id}")
    public String test(@PathVariable("type") String type, @PathVariable("id") String roomId) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        reference.child("room_chats").child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
            }
        });
        return future.get();
    }

    @PostMapping("/send/{type}/{id}")
    public String sendMessage(@PathVariable("type") String type, @PathVariable("id") String roomId, @RequestBody @Valid Message message) throws ExecutionException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        String key = String.valueOf(now.toInstant(ZoneOffset.UTC).toEpochMilli());
        message.setStatus(Status.SENT);
        message.setTime(formatter.format(now));
        CompletableFuture<String> future = new CompletableFuture<>();
        reference.child(type).child(roomId).child(key)
                .setValue(message, (databaseError, databaseReference) -> {
                    if (databaseError != null)
                        future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                    else future.complete(key);
                });
        return future.get();
    }

    @GetMapping("/search/{type}/{id}")
    public List<Message> searchMessages(
            @PathVariable("type") String type,
            @PathVariable("id") String roomId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "status", required = false) Status status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) throws ExecutionException, InterruptedException {

        CompletableFuture<List<Message>> future = new CompletableFuture<>();

        reference.child(type).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        boolean matches = true;

                        if (content != null && !message.getMessage().contains(content)) {
                            matches = false;
                        }
                        if (status != null && message.getStatus() != status) {
                            matches = false;
                        }
                        if (startTime != null || endTime != null) {
                            LocalDateTime messageTime = LocalDateTime.parse(message.getTime(), formatter);
                            if (startTime != null && messageTime.isBefore(startTime)) {
                                matches = false;
                            }
                            if (endTime != null && messageTime.isAfter(endTime)) {
                                matches = false;
                            }
                        }

                        if (matches) {
                            messages.add(message);
                        }
                    }
                }
                future.complete(messages);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
            }
        });

        return future.get();
    }
}
