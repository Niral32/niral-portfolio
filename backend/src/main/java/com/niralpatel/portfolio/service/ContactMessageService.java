package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.dto.ContactMessageDetail;
import com.niralpatel.portfolio.dto.ContactMessageRequest;
import com.niralpatel.portfolio.dto.ContactMessageResponse;
import com.niralpatel.portfolio.entity.ContactMessage;
import com.niralpatel.portfolio.repository.ContactMessageRepository;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailNotificationService emailNotificationService;
    private final SiteSettingsService siteSettingsService;

    public ContactMessageService(
            ContactMessageRepository contactMessageRepository,
            EmailNotificationService emailNotificationService,
            SiteSettingsService siteSettingsService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailNotificationService = emailNotificationService;
        this.siteSettingsService = siteSettingsService;
    }

    @Transactional
    public ContactMessageResponse save(ContactMessageRequest request) {
        if (!siteSettingsService.get().contactEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The owner has temporarily turned off the contact form.");
        }
        ContactMessage entity = new ContactMessage();
        entity.setName(request.name().trim());
        entity.setEmail(request.email().trim());
        entity.setSubject(request.subject().trim());
        entity.setMessage(request.message().trim());
        ContactMessage saved = contactMessageRepository.save(entity);

        // Fire-and-forget email notification. The async method swallows exceptions
        // so a flaky SMTP server can't fail the contact submission.
        emailNotificationService.notifyNewContactMessage(saved);

        return new ContactMessageResponse(
                saved.getId(), saved.getName(), saved.getEmail(), saved.getSubject(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<ContactMessageDetail> listAll() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDetail)
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return contactMessageRepository.countByReadAtIsNull();
    }

    @Transactional
    public ContactMessageDetail markRead(Long id, boolean read) {
        ContactMessage entity = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        entity.setReadAt(read ? Instant.now() : null);
        return toDetail(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!contactMessageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        contactMessageRepository.deleteById(id);
    }

    private ContactMessageDetail toDetail(ContactMessage e) {
        return new ContactMessageDetail(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getSubject(),
                e.getMessage(),
                e.getCreatedAt(),
                e.getReadAt());
    }
}
