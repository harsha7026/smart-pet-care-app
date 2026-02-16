package com.petcare.dto;

/**
 * DTO for Registration Success Email
 */
public class RegistrationEmailDTO {
    private String recipientEmail;
    private String userName;
    private String roleBadge;
    private String loginUrl;
    private String helpUrl;
    private String privacyUrl;
    private String termsUrl;
    private boolean isDoctor;
    private boolean isPetOwner;

    // Constructors
    public RegistrationEmailDTO() {}

    public RegistrationEmailDTO(String recipientEmail, String userName, String roleBadge, 
                               String loginUrl, String helpUrl, String privacyUrl, 
                               String termsUrl, boolean isDoctor, boolean isPetOwner) {
        this.recipientEmail = recipientEmail;
        this.userName = userName;
        this.roleBadge = roleBadge;
        this.loginUrl = loginUrl;
        this.helpUrl = helpUrl;
        this.privacyUrl = privacyUrl;
        this.termsUrl = termsUrl;
        this.isDoctor = isDoctor;
        this.isPetOwner = isPetOwner;
    }

    // Getters and Setters
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getRoleBadge() { return roleBadge; }
    public void setRoleBadge(String roleBadge) { this.roleBadge = roleBadge; }

    public String getLoginUrl() { return loginUrl; }
    public void setLoginUrl(String loginUrl) { this.loginUrl = loginUrl; }

    public String getHelpUrl() { return helpUrl; }
    public void setHelpUrl(String helpUrl) { this.helpUrl = helpUrl; }

    public String getPrivacyUrl() { return privacyUrl; }
    public void setPrivacyUrl(String privacyUrl) { this.privacyUrl = privacyUrl; }

    public String getTermsUrl() { return termsUrl; }
    public void setTermsUrl(String termsUrl) { this.termsUrl = termsUrl; }

    public boolean isDoctor() { return isDoctor; }
    public void setDoctor(boolean doctor) { isDoctor = doctor; }

    public boolean isPetOwner() { return isPetOwner; }
    public void setPetOwner(boolean petOwner) { isPetOwner = petOwner; }
}
