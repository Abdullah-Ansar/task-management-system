
package com.example.taskmanager.dto;

public class ResetPasswordResponseDTO {

    private String resetToken;

    public ResetPasswordResponseDTO(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
