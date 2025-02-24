package castis.enums;

public enum UserRole {
    ROLE_USER("ROLE_USER"),
    ROLE_USER_BASIC("ROLE_USER_BASIC"),
    ROLE_USER_SILVER("ROLE_USER_SILVER"),
    ROLE_USER_GOLD("ROLE_USER_GOLD"),
    ROLE_USER_VIP("ROLE_USER_VIP"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN");

    String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
