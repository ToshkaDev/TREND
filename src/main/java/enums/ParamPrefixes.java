package enums;

public enum  ParamPrefixes {
    INPUT("-i "),
    INPUT_SECOND("-s "),
    INPUT_THIRD("-d "),
    INPUT_FOURTH("-f "),
    OUTPUT("-o "),
    EVAL_THRESH("-e "),

    REORDER("-r "),
    ALGORITHM("-a "),
    THREAD_ALGN("-t "),

    MAFFT_PATH("-f "),
    MEGACC_PATH("-k "),
    THREAD("-u "),
    TREE_BUILD_METHOD("-m "),
    AA_SUBST_MODEL("-l "),
    AA_SUBST_RATE("-n "),
    INITIAL_TREE_ML("-e "),
    GAPS_AND_MISSING_DATA("-g "),
    SITE_COV_CUTOFF("-c "),
    PHYLOGENY_TEST("-p "),
    NUMBER_OF_REPLICATES("-b "),
    DOMAINS_PREDICTION_PROGRAM("-p "),
    DOMAINS_PREDICTION_DB("-D "),
    OUTPUT_PARAMS("-x "),
    OUTPUT_TREE("-z "),

    //protein features prediction specific parameters
    OUTPUT_SECOND("-n "),
    OUTPUT_THIRD("-b "),
    OUTPUT_FOURTH("-r "),
    OUTPUT_FIFTH("-f "),
    OUTPUT_SIXTH("-x "),
    HMMSCAN_DB_PATH("-A "),
    RPSBLAST_DB_PATH("-B "),
    RPSBPROC_DB_PATH("-C "),

    HMMSCAN_PATH("-H "),
    PROBABILITY("-y "),
    RPSBLAST_PATH("-R "),
    RPSBPROC_PATH("-P "),
    TMHMM_PATH("-T ");

    private String paramPrefix;

    ParamPrefixes(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String getPrefix() {
        return paramPrefix;
    }

}
