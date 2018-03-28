function getOptions() {
    console.log("getOptions() called!")
    var options = new FormData();

	if (typeof $('#first-file')[0] != 'undefined') {
		$.each($('#first-file')[0].files, function(i, file) {
			options.append('listOfFiles', file);
        });
    }

    var commandToBeProcessedBy = $('#subnavigation-tab').text();

    if (commandToBeProcessedBy === "createCogs") {
    	var fileDelim = $('#first-delim').val();
    	var fileColumn = $('#first-col').val();
    	var identityThreshold = $('#identity-threshold').val();
    	var coverageThreshold = $('#coverage-threshold').val();
    	var evalueThreshold = $('#evalue-threshold').val();
    	var merge = $('input[name="merge"]:checked').val();


        if (typeof fileDelim != 'undefined') {
            if (fileDelim != null) {
                options.append("fileDelim", fileDelim);
            }
        }
        if (typeof fileColumn != 'undefined') {
            if (fileColumn != '') {
                options.append("fileColumn", fileColumn);
            }
        }
        if (typeof identityThreshold != 'undefined') {
            if (identityThreshold != '') {
                options.append("identityThreshold", identityThreshold);
            }
        }
        if (typeof coverageThreshold != 'undefined') {
            if (coverageThreshold != '') {
                options.append("coverageThreshold", coverageThreshold);
            }
        }
        if (typeof evalueThreshold != 'undefined') {
            if (evalueThreshold != '') {
                options.append("evalueThreshold", evalueThreshold);
            }
        }

    } else if (commandToBeProcessedBy === "concatenate") {
        var organismNameCol = $('#organism-name-col').val();
        var proteinNameCol = $('#protein-name-col').val();
        var cogIdCol = $('#cogId-col').val();


        if (typeof organismNameCol != 'undefined') {
            if (organismNameCol != '') {
                options.append("organismNameCol", organismNameCol);
            }
        }
        if (typeof proteinNameCol != 'undefined') {
            if (organismNameCol != '') {
                options.append("proteinNameCol", proteinNameCol);
            }
        }
        if (typeof cogIdCol != 'undefined') {
            if (organismNameCol != '') {
                options.append("cogIdCol", cogIdCol);
            }
        }
    }



    if (typeof commandToBeProcessedBy != 'undefined') {
        if (commandToBeProcessedBy != '') {
            options.append("commandToBeProcessedBy", commandToBeProcessedBy);
        }
    }

	return options;
}


