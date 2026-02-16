package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for email data transfer objects
 * Contains common email fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String templateName;
}
