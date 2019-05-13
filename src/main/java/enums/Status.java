package enums;

public enum Status {
    status("status"),
    result("result"),
    stage("stage"),
    stageDetails("stageDetails"),
    error("Error"),
    megaError("ERROR. MEGA");

    private String statusEnum;

    Status(String statusEnum) {
        this.statusEnum = statusEnum;
    }

    public String getStatusEnum() {
        return statusEnum;
    }
}
