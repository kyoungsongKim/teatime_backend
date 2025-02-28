package castis.domain.attendance.entity;

public enum WorkType {
    OFFICE("OFFICE"),  // 정상 근무
    REMOTE("REMOTE"),  // 재택 근무
    FIELD("FIELD");    // 외근

    private final String displayName;

    WorkType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
