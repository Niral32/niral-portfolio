package com.niralpatel.portfolio.controller;

import com.niralpatel.portfolio.dto.ContactMessageRequest;
import com.niralpatel.portfolio.dto.ContactMessageResponse;
import com.niralpatel.portfolio.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactMessageResponse submit(@Valid @RequestBody ContactMessageRequest request) {
        return contactMessageService.save(request);
    }
}
