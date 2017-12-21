package net.pla1.srssmonitor;

public class LoginResponse {
    private String message;
    private String token;
    private long token_exp_sec;

    public String toString() {
        return String.format("Message: %s Token: %s Expire seconds: %d", message, token, token_exp_sec);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getToken_exp_sec() {
        return token_exp_sec;
    }

    public void setToken_exp_sec(long token_exp_sec) {
        this.token_exp_sec = token_exp_sec;
    }
}
