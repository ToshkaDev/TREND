package enums;

public enum Status {
    status("status"),
    result("result"),
    stage("stage"),
    stageDetails("stageDetails"),
    error("Error"),
    ERROR("ERROR"),
    megaError("ERROR. MEGA"),
    fastTreeError("ERROR. FastTree");

    private String statusEnum;

    Status(String statusEnum) {
        this.statusEnum = statusEnum;
    }

    public String getStatusEnum() {
        return statusEnum;
    }
}
