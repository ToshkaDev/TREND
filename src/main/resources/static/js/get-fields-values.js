function getOptions() {
	var firstFile = $('#first-file')[0].files[0];
	if (typeof $('#second-file')[0] != 'undefined') {
	    var secondFile = $('#second-file')[0].files[0];
    }
    if (typeof $('#third-file')[0] != 'undefined') {
        var thirdFile = $('#third-file')[0].files[0];
    }
	var firstFileArea = $('#first-file-area').val() ? $('#first-file-area').val().trim() : '';
	var secondFileArea = $('#second-file-area').val() ? $('#second-file-area').val().trim() : '';
	var thirdFileArea = $('#third-file-area').val() ? $('#third-file-area').val().trim() : '';

    var alignmentAlg = $('#alignment-alg').val();
    var treeBuildMethod = $('#tree-method').val();
    var aaSubstRate = $('#subst-rate').val();
    var initialTreeForMl = $('#initial-tree-ml').val();
    var gapsAndMissingData = $('#gaps-missing').val();
    var siteCovCutOff = $('#site-cov-cutoff').val();
    var numberOrReplicates = $('#number-of-replicates').val();
    var phylogenyTest = getOption("phylo-test");
    var aaSubstModel = getOption("subst-model");

    var domainPredictionProgram = $('input[name="dom-prediction-program"]:checked').val();

    Cookies.set('HEYE', 'This');
    console.log("cookie.get('HEYE') " + Cookies.get('HEYE'))
    var optionToOptionName = {
        "firstFile": firstFile,
        "secondFile": secondFile,
        "thirdFile": thirdFile,
        "firstFileArea": firstFileArea,
        "secondFileArea": secondFileArea,
        "thirdFileArea": thirdFileArea,
        "alignmentAlg": alignmentAlg,
        "treeBuildMethod": treeBuildMethod,
        "aaSubstRate": aaSubstRate,
        "phylogenyTest": phylogenyTest,
        "aaSubstModel": aaSubstModel,
        "initialTreeForMl": initialTreeForMl,
        "gapsAndMissingData": gapsAndMissingData,
        "siteCovCutOff": siteCovCutOff,
        "numberOrReplicates": numberOrReplicates,
        "domainPredictionProgram": domainPredictionProgram,
        "commandToBeProcessedBy": $('#subnavigation-tab').text()
    }
    var options = new FormData();
    setOptions(options, optionToOptionName);
	return options;
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


