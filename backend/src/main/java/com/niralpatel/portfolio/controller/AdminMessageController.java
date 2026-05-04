package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.ContactMessageDetail;
import com.niralpatel.portfolio.service.ContactMessageService;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated endpoints for managing contact-form submissions.
 * All routes are guarded by {@code /api/admin/**} → authenticated() in SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    private final ContactMessageService contactMessageService;

    public AdminMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping
    public List<ContactMessageDetail> list() {
        return contactMessageService.listAll();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() {
        return Map.of("unread", contactMessageService.unreadCount());
    }

    @PatchMapping("/{id}")
    public ContactMessageDetail patch(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean read = Boolean.TRUE.equals(body.get("read"));
        return contactMessageService.markRead(id, read);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
