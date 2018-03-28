package enums;

public enum BioPrograms {
    GET_SEQ_BYNAME("getSeqByName"), MAKE_UNIQUE("makeUnique"),
    CREATE_COGS("createCogs"), CONCATENATE("concatenate"),
    PROTO_TREE("protoTree");

    private String program;

    BioPrograms(String program) {
        this.program = program;
    }

    public String getProgramName() {
        return program;
    }
}
