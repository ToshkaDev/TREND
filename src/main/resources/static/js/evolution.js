function getOptions() {
	var firstFile = $('#first-file')[0].files[0];
	if (typeof $('#second-file')[0] != 'undefined') {
	    var secondFile = $('#second-file')[0].files[0];
    }
    if (typeof $('#third-file')[0] != 'undefined') {
        var thirdFile = $('#third-file')[0].files[0];
    }
	var firstFileArea = $('#first-file-area').val();
	var secondFileArea = $('#second-file-area').val();
	var thirdFileArea = $('#third-file-area').val();
    var alignmentAlg = $('#alignment-alg').val();
    var treeBuildMethod = $('#tree-method').val();

    var aaSubstRate = $('#subst-rate').val();
    var initialTreeForMl = $('#initial-tree-ml').val();
    var gapsAndMissingData = $('#gaps-missing').val();
    var siteCovCutOff = $('#site-cov-cutoff').val();
    var numberOrReplicates = $('#number-of-replicates').val();
    var domainPredictionProgram = $('input[name="dom-prediction-program"]:checked').val();
    var phylogenyTest = getOption("phylo-test");
    var aaSubstModel = getOption("subst-model");

    Cookies.set('HEYE', 'This');
    console.log("cookie.get('HEYE') " + Cookies.get('HEYE'))
    var optionToOptionNameMain = {
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

    for (var optionName in optionToOptionNameMain) {
        console.log("optionName " + optionName)
        console.log("optionToOptionNameMain[optionName] " + optionToOptionNameMain[optionName])
        if (optionIsDefined(optionToOptionNameMain[optionName])) {
            options.append(optionName, optionToOptionNameMain[optionName]);
        }
    }

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

function optionIsDefined(option) {
    if (typeof option != 'undefined') {
        if (option != '') {
            return true;
        }
    }
    return false;
}


