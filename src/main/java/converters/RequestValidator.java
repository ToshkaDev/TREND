package converters;

import exceptions.IncorrectRequestException;
import model.request.ProtoTreeRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Server-side validation of every user-controlled ProtoTreeRequest field that is passed on to the
 * bioinformatics programs as a command-line argument.
 *
 * The browser form restricts these fields to fixed choices and numeric ranges, but requests can be
 * sent directly to the server, so the same restrictions are enforced here. A request with any value
 * outside the allowed set is rejected before files are stored or processes are launched.
 */
public final class RequestValidator {

    private static final Pattern INTEGER = Pattern.compile("^\\d{1,6}$");
    private static final Pattern DECIMAL = Pattern.compile("^(\\d{1,6}(\\.\\d{0,10})?|\\.\\d{1,10})([eE][-+]?\\d{1,3})?$");
    private static final Pattern COOKIE = Pattern.compile("^[A-Za-z0-9]{1,64}$");

    private static final Set<String> YES_NO = set("yes", "no");
    private static final Set<String> TRUE_FALSE = set("true", "false");
    private static final Set<String> CHECKBOX = set("true", "false", "checked");

    private static final Set<String> TREE_BUILDING_PROGRAMS = set("FastTree", "Mega");
    private static final Set<String> ALIGNMENT_ALGORITHMS = set(
            "--retree 2 --maxiterate 0",
            "--retree 2 --maxiterate 1000",
            "--maxiterate 1000 --localpair",
            "--maxiterate 1000 --genafpair",
            "--maxiterate 1000 --globalpair");
    private static final Set<String> TREE_BUILD_METHODS = set("JN", "ML", "ME");
    private static final Set<String> AA_SUBST_RATES = set("ur", "gd", "ir", "gir");
    private static final Set<String> INITIAL_TREES_ML = set("njBio", "mp", "nj", "bioNj");
    private static final Set<String> GAPS_AND_MISSING_DATA = set("compDel", "partDel", "pairDel");
    private static final Set<String> PHYLOGENY_TESTS = set("none", "bm", "ib");
    private static final Set<String> AA_SUBST_MODELS = set(
            "jtt", "nd", "pd", "pm", "eim", "dm", "dmf", "jttf", "wm", "wmf",
            "lg", "lgf", "grm", "grmf", "grc", "grcf", "grt", "grtf");
    private static final Set<String> AA_SUBST_MODELS_FT = set("jtt", "wag", "lg");
    private static final Set<String> PHYLOGENY_TESTS_FT = set("nosupport", "sh", "nome");
    private static final Set<String> DOMAIN_PREDICTION_PROGRAMS = set("hmmscan", "rpsblast");
    private static final Set<String> DOMAIN_PREDICTION_DBS = set(
            "pfam", "pfam-only", "pfam_and_mist", "cdd", "cdd_ncbi", "cog", "kog", "prk", "smart", "tigr");

    private RequestValidator() {
    }

    public static void validate(ProtoTreeRequest r) throws IncorrectRequestException {
        oneOf("treeBuildingProgram", r.getTreeBuildingProgram(), TREE_BUILDING_PROGRAMS);
        oneOf("doAlign", r.getDoAlign(), YES_NO);
        oneOf("alignmentAlg", r.getAlignmentAlg(), ALIGNMENT_ALGORITHMS);
        oneOf("treeBuildMethod", r.getTreeBuildMethod(), TREE_BUILD_METHODS);
        oneOf("aaSubstModel", r.getAaSubstModel(), AA_SUBST_MODELS);
        oneOf("aaSubstRate", r.getAaSubstRate(), AA_SUBST_RATES);
        oneOf("initialTreeForMl", r.getInitialTreeForMl(), INITIAL_TREES_ML);
        oneOf("gapsAndMissingData", r.getGapsAndMissingData(), GAPS_AND_MISSING_DATA);
        integerInRange("siteCovCutOff", r.getSiteCovCutOff(), 0, 100);
        oneOf("phylogenyTest", r.getPhylogenyTest(), PHYLOGENY_TESTS);
        integerInRange("numberOrReplicates", r.getNumberOrReplicates(), 1, 10000);

        oneOf("aaSubstModelFt", r.getAaSubstModelFt(), AA_SUBST_MODELS_FT);
        oneOf("pseudoCountsFt", r.getPseudoCountsFt(), YES_NO);
        oneOf("phylogenyTestFt", r.getPhylogenyTestFt(), PHYLOGENY_TESTS_FT);
        integerInRange("numberOrReplicatesFt", r.getNumberOrReplicatesFt(), 1, 10000);

        oneOf("doPredictFeatures", r.getDoPredictFeatures(), YES_NO);
        oneOf("domainPredictionProgram", r.getDomainPredictionProgram(), DOMAIN_PREDICTION_PROGRAMS);
        oneOf("domainPredictionDb", r.getDomainPredictionDb(), DOMAIN_PREDICTION_DBS);
        decimalInRange("eValue", r.geteValue(), 0, 10);
        decimalInRange("probability", r.getProbability(), 0, 100);
        oneOf("lcrPrediction", r.getLcrPrediction(), CHECKBOX);
        oneOf("enumerate", r.getEnumerate(), CHECKBOX);

        integerInRange("domainTolerance", r.getDomainTolerance(), 0, 100);
        integerInRange("operonTolerance", r.getOperonTolerance(), 0, 10000);
        integerInRange("numberOfNeighbors", r.getNumberOfNeighbors(), 0, 100);

        oneOf("fetchFromIds", r.getFetchFromIds(), CHECKBOX);
        oneOf("fetchFromTree", r.getFetchFromTree(), CHECKBOX);
        oneOf("isFullPipeline", r.isFullPipeline(), TRUE_FALSE);
        decimalInRange("redundancy", r.getRedundancy(), 0, 1);
        matches("protoTreeCookies", r.getProtoTreeCookies(), COOKIE);
    }

    private static void oneOf(String name, String value, Set<String> allowed) throws IncorrectRequestException {
        if (value != null && !allowed.contains(value)) {
            throw invalid(name);
        }
    }

    private static void matches(String name, String value, Pattern pattern) throws IncorrectRequestException {
        if (value != null && !pattern.matcher(value).matches()) {
            throw invalid(name);
        }
    }

    private static void integerInRange(String name, String value, int min, int max) throws IncorrectRequestException {
        if (value == null) {
            return;
        }
        if (!INTEGER.matcher(value).matches()) {
            throw invalid(name);
        }
        int number = Integer.parseInt(value);
        if (number < min || number > max) {
            throw invalid(name);
        }
    }

    private static void decimalInRange(String name, String value, double min, double max) throws IncorrectRequestException {
        if (value == null) {
            return;
        }
        if (!DECIMAL.matcher(value).matches()) {
            throw invalid(name);
        }
        double number = Double.parseDouble(value);
        if (number < min || number > max) {
            throw invalid(name);
        }
    }

    private static IncorrectRequestException invalid(String name) {
        return new IncorrectRequestException("Invalid value for parameter '" + name + "'.");
    }

    private static Set<String> set(String... values) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values)));
    }
}
