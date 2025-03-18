package org.aiedwise2024.aiedproject;

/**
 * Message format is found in the documentation here:
 * https://console.groq.com/docs/api-reference#chat
 * An example message:
 * [ {"role: "system", "content": "You are an AI assistant"},
 * {"role": "user", "content" : "PROMPT HERE"}]
 * */

public class LMmessage {
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private String role; // one of the roles given above
    private String content;

    public LMmessage(String role, String content) {
        //to control input values for role
        if (!role.equals(ROLE_SYSTEM) && !role.equals(ROLE_ASSISTANT) && !role.equals(ROLE_USER)) {
            throw new IllegalArgumentException(role + " is not a valid role");
        }

        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // for debugging purposes
    @Override
    public String toString() {
        return "LMmessage{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}