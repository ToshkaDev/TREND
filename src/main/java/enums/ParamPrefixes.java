package enums;

public enum  ParamPrefixes {
    INPUT("-i "),
    WDIR("-w "),

    TAXALIST("-l "),
    INPUT_SECOND("-s "),
    INPUT_THIRD("--ithird "),
    INPUT_FOURTH("-f"),
    OUTPUT("-o "),
    DELIM("-d "),
    COLUMN("-c "),
    DELIM_SECOND("-t "),
    COLUMN_SECOND("-p "),

    EVAL_THRESH("-e "),

    IDENTITY_THRESH("-t "),
    COVERAGE_THRESH("-v "),
    MERGE("-m "),


    REORDER("-r "),
    ALGORITHM("-a "),
    THREAD("--thread "),

    TREE_THREAD("-u "),
    TREE_BUILD_METHOD("-m "),
    AA_SUBST_MODEL("-l "),
    AA_SUBST_RATE("n "),
    INITIAL_TREE_ML("-e "),
    GAPS_AND_MISSING_DATA("-g "),
    SITE_COV_CUTOFF("-c "),
    PHYLOGENY_TEST("-p "),
    NUMBER_OF_REPLICATES("-b "),
    DOMAINS_PREDICTION_PROGRAM("-p "),
    OUTPUT_PARAMS("-x"),
    OUTPUT_TREE("-z"),

    //protein features prediction specific parameters
    OUTPUT_SECOND("--osecond"),
    OUTPUT_THIRD("--othird"),
    OUTPUT_FOURTH("-r"),
    OUTPUT_FIFTH("-f"),
    OUTPUT_SIXTH("-x"),
    HMMSCAN_DB_PATH("-A"),
    RPSBLAST_DB_PATH("-B"),
    RPSBPROC_DB_PATH("-C"),
    RPSBLAST_SP_DB("-D"),

    HMMSCAN_PATH("-H"),
    RPSBLAST_PATH("-R"),
    RPSBPROC_PATH("-P"),
    TMHMM_PATH("-T"),
    OUTPUT_PROTEIN_FEAUTURES("-d")
    ;

    private String paramPrefix;

    ParamPrefixes(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String getPrefix() {
        return paramPrefix;
    }

}
