package enums;

/**
 * Created by vadim on 7/27/17.
 */
public enum MiscEnum {
    MEGA("Mega"), FAST_TREE("FastTree");

    private String program;

    MiscEnum(String program) {
        this.program = program;
    }

    public String getProgram() {
        return program;
    }


}
