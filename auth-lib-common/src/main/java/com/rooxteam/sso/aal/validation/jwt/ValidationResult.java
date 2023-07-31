package com.rooxteam.sso.aal.validation.jwt;

public final class ValidationResult {

    private final boolean success;

    private Reason reason = Reason.Success;

    ValidationResult(boolean success) {
        this.success = success;
    }

    ValidationResult(boolean success, Reason reason) {
        this.success = success;
        this.reason = reason;
    }

    public enum Reason {
        Success, Unknown, UnsupportedAlg, NoValidatorsConfigured, BeforeNotBefore, Expired, SignatureNotValid, TokenWasUsed, TokenStoreError, KeyNotFound,
        TokenIsNotSigned, InvalidIssuer, InvalidSub, InvalidIssueTime
    }

    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult fail(Reason reason) {
        return new ValidationResult(false, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "success=" + success +
                ", reason=" + reason +
                '}';
    }
}
