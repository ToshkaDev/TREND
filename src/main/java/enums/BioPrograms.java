package enums;

public enum BioPrograms {
    PROTO_TREE("protoTree");

    private String program;

    BioPrograms(String program) {
        this.program = program;
    }

    public String getProgramName() {
        return program;
    }
}
