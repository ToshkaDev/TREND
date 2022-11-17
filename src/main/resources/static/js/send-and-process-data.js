$(document).ready(function (){
	takeCareOfValidators();
	takeCareOfFields();

    $('#GoAsync').click(function() {
        $("#first-area-message, #second-area-message, #both-areas-message, #one-area-message, #malformed-fasta, #malformed-fasta-second, #cannot-have-ids-and-seqs").hide();
    	options = getOptions();
    	checkAndSubmit(options, "secondFile");
    });

    $('#GoAsyncPartial').click(function() {
        $("#second-area-message-partialP, #first-area-message-partialP, #partial-pipeline-message, #malformed-newick").hide();
        $("#malformed-fasta-partialP, #malformed-fasta-second-partialP").hide();
        options = getOptions();
        checkAndSubmit(options, "alignmentFile");
    });

    $('#GoAsyncNeib').click(function() {
        $("#first-area-message, #one-area-message, #malformed-fasta, #malformed-newick, #cannot-have-ids-and-seqs").hide();
        options = getOptions();
        checkFileAndSendQueryNeib(options);
    });

    $('#options').click(function() {
        $('.extra-result-container').show();
        $('#options').hide();
    });

    $('#addSecondArea').click(function() {
        $('.full-pipe, .second-area').trigger('reset');
        $('#first-file-info, #second-file-info').empty();
        $("#first-area-message, #second-area-message, #both-areas-message, #one-area-message, #malformed-fasta, #malformed-fasta-second, #cannot-have-ids-and-seqs").hide();
        $('.second-area').toggle();
        if ($('.second-area').css("display") !== "none") {
            $('#addSecondArea').html("Remove Second Area");
            $('.fetch-fromIds1').hide();
            $('.active-second-area').fadeIn();
        }
        else {
            $('#addSecondArea').html("Add Second Area");
            $('.fetch-fromIds1').show();
            $('.active-second-area').fadeOut();
        }
    });

    $('#full-pipeline').click(function() {
        $('.partial-pipe').hide();
        $('.full-pipe').show();
        $('#full-pipeline').removeClass('pipeline-button');
        $('#full-pipeline').addClass('pipeline-button-selected');
        $('#partial-pipeline').removeClass('pipeline-button-selected');
        $('#partial-pipeline').addClass('pipeline-button');
        $('#isFullPipeline').text("true");

        $(".predict-features").show();
        $('#rpsblast-db').hide();
        $("#do-predict-features").prop("checked", true);
        $("#do-predict-features").prop("disabled", false);

        clearFieldsOnPipelineSelect();
        $('#filter-clear').trigger("click");
    });
    $('#partial-pipeline').click(function() {
        $('.full-pipe').hide();
        $('.partial-pipe').show();
        $('.second-area').hide();
        $('#full-pipeline').removeClass('pipeline-button-selected');
        $('#full-pipeline').addClass('pipeline-button');
        $('#partial-pipeline').removeClass('pipeline-button');
        $('#partial-pipeline').addClass('pipeline-button-selected');
        $('#isFullPipeline').text("false");

        $(".predict-features").hide();
        $("#do-predict-features").prop("checked", false);
        $("#do-predict-features").prop("disabled", true);

        clearFieldsOnPipelineSelect();
        $('#filter-clear').trigger("click");
    });
});

function clearFieldsOnPipelineSelect() {
    //Clear retrieval by id values
    $("#fetch-fromIds-value1").prop("checked", false);
    $("#fetch-fromTree-value").prop("checked", false);
    //Clear text input areas
    $('#first-file-area').val('');
    $('#second-file-area').val('');
    //Clear file input areas
    $('#sequence-file').val('');
    $('#alignment-file').val('');
    $('#tree-file').val('');
    $("#alignment-file-info").html('');
    $("#sequence-file-info").html('');
    $('#sequence-file-info').html('');
}

function fastaIsCorrect(fasta, malformedMessageId,  notFastaMessageId, cannotHaveIdsAndSeqsMessage) {
    var fetchFromIds = $("#fetch-fromIds-value1").prop("checked");

    if (!fetchFromIds) {
    	if (fasta.trim().slice(0, 1) !== ">") {
    		$("#"+notFastaMessageId).show();
    		return false;
    	}
        seqMatches = fasta.match(/>/g);
        if (seqMatches.length < 3) {
            $('#'+malformedMessageId).show();
            return false;
        }
    } else {
        if (fasta.trim().slice(0, 1) == ">") {
            $('#'+cannotHaveIdsAndSeqsMessage).show();
            return false;
        } else {
            matches = fasta.match(/^.+$/gm);
            if (matches.length < 3) {
                $('#'+malformedMessageId).show();
                return false;
            }
        }
    }

    return true;
}

function submitIfNewickTreeIsOK(newickFile, options) {
    var fileReader = new FileReader();
    fileReader.readAsText(newickFile);
    fileReader.onloadend = function() {
	    try {
	        var textCounter = Newick.parse(fileReader.result)[1]
	        var newickEndIsOK = fileReader.result.trim().slice(-1) == ";"
	        textCounter < 3 || !newickEndIsOK ? $("#malformed-newick").show() : getDataAsync(options);
	    } catch(error) {
	        $("#malformed-newick").show()
	    }
	}
}

function checkFileAndSubmit(options, firstFile=null, firstInsuffSeqsMessageId, firstMalformedMessageId,
							secondFile=null, secondInsuffSeqsMessageId=null, secondMalformedMessageId=null,
							areaStatus=null, cannotHaveIdsAndSeqs=null) {
    var fileReader = new FileReader();
    fileReader.readAsText(options.get(firstFile ? firstFile : secondFile));
    fileReader.onloadend = function() {
        if (firstFile)
		    var inputStatus = fastaIsCorrect(fileReader.result, firstInsuffSeqsMessageId, firstMalformedMessageId, cannotHaveIdsAndSeqs);
		else if (secondFile)
		    var inputStatus = fastaIsCorrect(fileReader.result, secondInsuffSeqsMessageId, secondMalformedMessageId, cannotHaveIdsAndSeqs);
        if (inputStatus) {
			if (firstFile && options.get(secondFile)) {
				var secondFileReader = new FileReader();
				secondFileReader.readAsText(options.get(secondFile));
				secondFileReader.onloadend = function() {
					var secondInputStatus = fastaIsCorrect(secondFileReader.result, secondInsuffSeqsMessageId, secondMalformedMessageId, cannotHaveIdsAndSeqs);
					if (secondInputStatus && !options.get("treeFile"))
						getDataAsync(options);
					else if (secondInputStatus && options.get("treeFile"))
						submitIfNewickTreeIsOK(options.get("treeFile"), options);
				}
			} else if (options.get("treeFile")) {
				submitIfNewickTreeIsOK(options.get("treeFile"), options);
			} else if (areaStatus) { // if areaStatus is true, then !$(".second-area").is(':hidden') for sure.
			    //it's taken care of by a function  which clears pasted inputs of files selected when clicking 'Add Second Area' button
                getDataAsync(options);
            } else if (areaStatus == null && firstFile === "firstFile") {
                if ($(".second-area").is(':hidden'))
                    getDataAsync(options);
                else
                    $("#both-areas-message").show();
            } else if (areaStatus == null && firstFile !== "firstFile") {
				$("#both-areas-message").show();
			}
        }
    }
}

function checkAndSubmit(options, secondFile) {
	var firstAreaStatus = null, secondAreaStatus = null;
    if (secondFile === "alignmentFile") {
        if (!options.get("treeFile")) {
            $("#partial-pipeline-message").show();
            return;
        }
        if (options.get("fetchFromTree")) {
            submitIfNewickTreeIsOK(options.get("treeFile"), options);
            return;
        } else if (!options.get("firstFile") && !options.get("alignmentFile")) {
            $("#partial-pipeline-message").show();
            return;
        }

    }
    if (secondFile === "secondFile") {
		if ($(".second-area").is(':hidden') && !options.get("firstFileArea") && !options.get("firstFile")) {
		    $("#one-area-message").show();
		}

		if (!options.get("firstFile") && options.get("firstFileArea") && options.get("firstFileArea").length) {
			firstAreaStatus = fastaIsCorrect(options.get("firstFileArea"), "malformed-fasta", "first-area-message", "cannot-have-ids-and-seqs");
		}
		if (!options.get("secondFile") && options.get("secondFileArea") && options.get("secondFileArea").length) {
		    secondAreaStatus = fastaIsCorrect(options.get("secondFileArea"), "malformed-fasta-second", "second-area-message");
		}

		if (!$(".second-area").is(':hidden')) {
			if (firstAreaStatus && secondAreaStatus) {
				getDataAsync(options);
				return;
			}
		} else {
			if (firstAreaStatus) {
				getDataAsync(options);
				return;
			}
		}
	}

	if (options.get("firstFile")) {
		if (secondFile === "alignmentFile")
			checkFileAndSubmit(options, "firstFile", "malformed-fasta-partialP", "first-area-message-partialP",
				secondFile, "malformed-fasta-second-partialP", "second-area-message-partialP", null);
		else if (secondFile === "secondFile")
			checkFileAndSubmit(options, "firstFile", "malformed-fasta", "first-area-message",
				secondFile, "malformed-fasta-second", "second-area-message", secondAreaStatus, "cannot-have-ids-and-seqs");
	} else if (secondFile === "alignmentFile") {
            checkFileAndSubmit(options, null, "malformed-fasta-partialP", "first-area-message-partialP",
                secondFile, "malformed-fasta-second-partialP", "second-area-message-partialP", null);
	} else if (options.get("secondFile")) {
		// nulls in the argument list below because sending request with second input specified by the user and empty first input
		// is invalid (we are ending up in this condition only if the previous is false; but the first area can be specified).
		checkFileAndSubmit(options, secondFile, "malformed-fasta-second", "second-area-message",
				null, null, null, firstAreaStatus);
	} else if (!$(".second-area").is(':hidden') && firstAreaStatus === null && secondAreaStatus === null) {
	    $("#both-areas-message").show();
	}

}

///////////////////

function checkFileAndSendQueryNeib(options) {
    if (!options.get("firstFile")) {
        checkFileAreaAndSendQueryNeib(options);
        return;
    }
    var fileReader = new FileReader();
    fileReader.readAsText(options.get("firstFile"));
    fileReader.onloadend = function() {
        if (fileReader.result.trim().slice(0, 1) !== ">" && fileReader.result.trim().slice(0, 1) !== "(") {
            if (fastaIsCorrect(fileReader.result, "malformed-fasta", "first-area-message", "cannot-have-ids-and-seqs"))
                getDataAsyncNeighborGenes(options);
        }
        else {
            if (fileReader.result.trim().slice(0, 1) === "(") {
                var textCounter = Newick.parse(fileReader.result)[1]
                if (textCounter < 3 || fileReader.result.trim().slice(-1) !== ";") {
                    $("#malformed-newick").show();
                    return;
                }
                options.set("treeFile", options.get("firstFile"));
                options.delete("firstFile");
                options.set("isFullPipeline", "false");
            } else if (!fastaIsCorrect(fileReader.result, "malformed-fasta", "first-area-message", "cannot-have-ids-and-seqs"))
                return;
            getDataAsyncNeighborGenes(options);
        }
    }
}

function checkFileAreaAndSendQueryNeib(options) {
    if (!options.get("firstFileArea"))
        $("#one-area-message").show();
    else if (options.get("firstFileArea") && options.get("firstFileArea").trim().slice(0, 1) !== ">"
        && options.get("firstFileArea").trim().slice(0, 1) !== "(") {
            if (fastaIsCorrect(options.get("firstFileArea"), "malformed-fasta", "first-area-message", "cannot-have-ids-and-seqs"))
                getDataAsyncNeighborGenes(options);
        }
    else {
        if (options.get("firstFileArea").trim().slice(0, 1) === "(") {
            var textCounter = Newick.parse(options.get("firstFileArea"))[1]
            if (textCounter < 3 || options.get("firstFileArea").trim().slice(-1) !== ";") {
                $("#malformed-newick").show();
                return;
            }
            options.set("treeFileArea", options.get("firstFileArea"));
            options.delete("firstFileArea");
            options.set("isFullPipeline", "false");
        } else if (!fastaIsCorrect(options.get("firstFileArea"), "malformed-fasta", "first-area-message", "cannot-have-ids-and-seqs"))
            return;
        getDataAsyncNeighborGenes(options);
    }
}

function getDataAsync(options) {
	$.ajax({
	      type: 'POST',
	      url: 'domains/process-request',
	      data : options,
	      success: redirect,
	      error: error,
	      contentType: false,
	      processData: false,
	      dataType:'text',
	      enctype: 'multipart/form-data'
	    });
}

function getDataAsyncNeighborGenes(options) {
	$.ajax({
	      type: 'POST',
	      url: 'gene-neighborhoods/process-request',
	      data : options,
	      success: redirectNeighborGenes,
	      error: error,
	      contentType: false,
	      processData: false,
	      dataType:'text',
	      enctype: 'multipart/form-data'
	    });
}

function redirect(jobId) {
    window.location.replace("domains/tree/" + jobId);
}

function redirectNeighborGenes(jobId) {
    window.location.replace("gene-neighborhoods/tree/" + jobId);
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}
