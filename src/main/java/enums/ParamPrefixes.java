package enums;

public enum  ParamPrefixes {
    INPUT("-i "),
    TAXALIST("-l "),
    INPUT_SECOND("-s "),
    INPUT_THIRD("--ithird "),
    OUTPUT("-o "),
    DELIM("-d "),
    COLUMN("-c "),
    DELIM_SECOND("-t "),
    COLUMN_SECOND("-p "),
    WDIR("-w "),
    IDENTITY_THRESH("-t "),
    EVAL_THRESH("-e "),
    COVERAGE_THRESH("-v "),
    MERGE("-m "),
    THREAD("--thread "),
    TREE_THREAD("-u "),
    REORDER("-r "),
    ALGORITHM("-a "),
    TREE_BUILD_METHOD("-m "),
    AA_SUBST_MODEL("-l "),
    AA_SUBST_RATE("n "),
    INITIAL_TREE_ML("-e "),
    GAPS_AND_MISSING_DATA("-g "),
    SITE_COV_CUTOFF("-c "),
    PHYLOGENY_TEST("-p "),
    NUMBER_OF_REPLICATES("-b ");

    private String paramPrefix;

    ParamPrefixes(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String getPrefix() {
        return paramPrefix;
    }

}
