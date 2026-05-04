package com.niralpatel.portfolio.entity;

import jakarta.persistence.*;

/**
 * Single-row settings table (id = 1). Place where the admin can toggle
 * site-wide flags and edit their displayed name + role.
 */
@Entity
@Table(name = "site_settings")
public class SiteSettings {

    @Id
    private Long id = 1L;

    @Column(name = "contact_enabled", nullable = false)
    private boolean contactEnabled = true;

    @Column(name = "contact_disabled_message", length = 500)
    private String contactDisabledMessage =
            "I'm not accepting messages right now — please reach me on LinkedIn instead.";

    @Column(name = "owner_name", nullable = false, length = 120)
    private String ownerName = "Niral Patel";

    @Column(name = "owner_title", nullable = false, length = 200)
    private String ownerTitle = "Java Full Stack Developer";

    @Column(name = "booking_enabled", nullable = false)
    private boolean bookingEnabled = true;

    public boolean isBookingEnabled() { return bookingEnabled; }
    public void setBookingEnabled(boolean bookingEnabled) { this.bookingEnabled = bookingEnabled; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isContactEnabled() { return contactEnabled; }
    public void setContactEnabled(boolean contactEnabled) { this.contactEnabled = contactEnabled; }

    public String getContactDisabledMessage() { return contactDisabledMessage; }
    public void setContactDisabledMessage(String contactDisabledMessage) { this.contactDisabledMessage = contactDisabledMessage; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerTitle() { return ownerTitle; }
    public void setOwnerTitle(String ownerTitle) { this.ownerTitle = ownerTitle; }
}
