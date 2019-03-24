package enums;

public enum Status {
    status("status"),
    result("result"),
    stage("stage"),
    error("Error"),
    megaError("ERROR. MEGA");

    private String miscEnum;

    Status(String miscEnum) {
        this.miscEnum = miscEnum;
    }

    public String getStatusEnum() {
        return miscEnum;
    }
}
