function getOptions() {
    var isFullPipeline = $('#isFullPipeline').text();
	if (isFullPipeline == 'false') {
        if (typeof $('#sequence-file')[0] != 'undefined')
            var firstFile = $('#sequence-file')[0].files[0];
        if (typeof $('#alignment-file')[0] != 'undefined')
            var alignmentFile = $('#alignment-file')[0].files[0];
        if (typeof $('#tree-file')[0] != 'undefined')
            var treeFile = $('#tree-file')[0].files[0];
	} else {
	    if (typeof $('#first-file')[0] != 'undefined')
            var firstFile = $('#first-file')[0].files[0];
    	if (typeof $('#second-file')[0] != 'undefined')
    	    var secondFile = $('#second-file')[0].files[0];
    	var firstFileArea = $('#first-file-area').val() ? $('#first-file-area').val().trim() : '';
    	var secondFileArea = $('#second-file-area').val() ? $('#second-file-area').val().trim() : '';
	}

    var treeBuildingProgram = $('input[name="tree-soft"]:checked').val();
    // Mega
    var doAlign = $('#do-align').prop("checked") ? "yes" : "no";
    var doPredictFeatures = $('#do-predict-features').prop("checked") ? "yes" : "no";
    var alignmentAlg = $('#alignment-alg').val();
    var treeBuildMethod = $('#tree-method').val();
    var aaSubstRate = $('#subst-rate').val();
    var initialTreeForMl = $('#initial-tree-ml').val();
    var gapsAndMissingData = $('#gaps-missing').val();
    var siteCovCutOff = $('#site-cov-cutoff').val();
    var numberOrReplicates = $('#number-of-replicates').val();
    var phylogenyTest = getOption("phylo-test");
    var aaSubstModel = getOption("subst-model");


    //FastTree
    var aaSubstModelFt = $('#subst-model-ft').val();
    var pseudoCountsFt = $('#pseudo-counts-ft').prop("checked") ? "yes" : "no";
    var phylogenyTestFt = $('#phylo-test-ft').val();
    var numberOrReplicatesFt = $('#number-of-replicates-ft').val();


    var domainPredictionProgram = $('input[name="dom-prediction-program"]:checked').val();

    var lcrPrediction = $("#lc-value").attr("checked");
    var enumerate = $("#enumerate-value").attr("checked");
    var fetchFromIds = $("#fetch-fromIds-value1").prop("checked");
    var fetchFromTree = $("#fetch-fromTree-value").prop("checked");

    var operonTolerance = $('#operon-tolerance').val();
    var domainTolerance = $('#domain-tolerance').val();
    var numberOfNeighbors = $("#numberOf-neighbors").val();


    var redundancy = $('#reduce-redundancy').prop("checked") ? $('#cd-hit').val() : '';

    var protoTreeCookies = setOrGetCookies();

    var optionToOptionName = {
        "treeBuildingProgram": treeBuildingProgram,
        "redundancy": redundancy,
        "fetchFromIds": fetchFromIds,
        "fetchFromTree": fetchFromTree,
        "enumerate": enumerate,
        "operonTolerance": operonTolerance,
        "domainTolerance": domainTolerance,
        "numberOfNeighbors": numberOfNeighbors,
        "protoTreeCookies": protoTreeCookies,
        "isFullPipeline": isFullPipeline,
        "firstFile": firstFile,
        "secondFile": secondFile,
        "firstFileArea": firstFileArea,
        "secondFileArea": secondFileArea,
        "doAlign": doAlign,
        "alignmentFile": alignmentFile,
        "treeFile": treeFile,
        "alignmentAlg": alignmentAlg,
        "treeBuildMethod": treeBuildMethod,
        "aaSubstRate": aaSubstRate,
        "phylogenyTest": phylogenyTest,
        "aaSubstModel": aaSubstModel,
        "initialTreeForMl": initialTreeForMl,
        "gapsAndMissingData": gapsAndMissingData,
        "siteCovCutOff": siteCovCutOff,
        "numberOrReplicates": numberOrReplicates,
        "aaSubstModelFt": aaSubstModelFt,
        "pseudoCountsFt": pseudoCountsFt,
        "phylogenyTestFt": phylogenyTestFt,
        "numberOrReplicatesFt": numberOrReplicatesFt,
        "doPredictFeatures": doPredictFeatures,
        "domainPredictionProgram": domainPredictionProgram,
        "lcrPrediction": lcrPrediction
    }
    var options = new FormData();
    setOptions(options, optionToOptionName);
	return options;
}

function setOrGetCookies() {
    var cookies = Cookies.get('ProtoTree_@BioUniverse_');
    if (typeof cookies == 'undefined') {
        cookies = Math.random().toString(36).substring(2);
        Cookies.set('Trend_@BioUniverse_', cookies, { expires: 30 });
    }
    return cookies;
}

function getOption(option) {
    var optionValue = '';
    switch ($('#tree-method').val()) {
        case "ML":
            optionValue = $('#ml-'+option).val();
            break;
        case "ME":
            optionValue = $('#nj_me-'+option).val();
            break;
        case "JN":
            optionValue = $('#nj_me-'+option).val();
            break;
    }
    return optionValue;
}

function setOptions(options, optionToOptionName) {
    for (var optionName in optionToOptionName) {
        if (optionIsDefined(optionToOptionName[optionName])) {
            options.append(optionName, optionToOptionName[optionName]);
            if (optionName === "domainPredictionProgram") {
                setDomainPredictionOptions(options, optionToOptionName[optionName]);
            }
        }
    }
}

function setDomainPredictionOptions(options, domainPredictionProgram) {
    var optionToOptionName = {};
    if (domainPredictionProgram == "hmmscan") {
        optionToOptionName["domainPredictionDb"] = $('.hmmer-db').val();
        optionToOptionName["eValue"] = $('#evalue-threshold-hmmer').val();
        optionToOptionName["probability"] = $('#probability').val();
    } else if (domainPredictionProgram == "rpsblast") {
        optionToOptionName["domainPredictionDb"] = $('.rpsblast-db').val();
        optionToOptionName["eValue"] = $('#evalue-threshold-rpsblast').val();
        // will not be used
        optionToOptionName["probability"] = '50';
    }
    setOptions(options, optionToOptionName);
}

function optionIsDefined(option) {
    if (typeof option != 'undefined') {
        if (option != '') {
            return true;
        }
    }
    return false;
}


