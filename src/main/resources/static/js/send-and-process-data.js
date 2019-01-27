$(document).ready(function (){
	takeCareOfValidators();
	takeCareOfFields();

    setCookie();
    $('#GoAsync').click(function() {
        $("#first-area-message, #second-area-message, #both-areas-message, #one-area-message").hide();
    	options = getOptions();
    	checkFileAndSendQuery(options, "secondFile");
    });

    $('#GoAsyncPartial').click(function() {
        $("#second-area-message-partialP, #first-area-message-partialP, #partial-pipeline-message").hide();
        options = getOptions();
        checkFileAndSendQuery(options, "alignmentFile");
    });

    $('#GoAsyncNeib').click(function() {
        $("#first-area-message, #one-area-message").hide();
        options = getOptions();
        checkFileAndSendQueryNeib(options);
    });

    $('#options').click(function() {
        $('.extra-result-container').show();
        $('#options').hide();
    });

    $('#addSecondArea').click(function() {
        $('.second-area').toggle();
        if ($('.second-area').css("display") !== "none") {
            $('#addSecondArea').html("Close Second Area")
        }
        else {
            $('#addSecondArea').html("Add Second Area")
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
    });
});

function checkFileAndSendQuery(options, secondFile) {
    if (secondFile === "alignmentFile") {
        if (!(options.get("firstFile") && options.get("treeFile"))) {
            $("#partial-pipeline-message").show();
            return;
        }
    } else if (secondFile === "secondFile") {
        if ((!$(".second-area").is(':hidden') && !(options.get("secondFile") && options.get("firstFile")))
            || ($(".second-area").is(':hidden') && !options.get("firstFile"))) {
                checkFileAreaAndSendQuery(options);
                return;
        }
    }

    var fileReader = new FileReader();
    fileReader.readAsText(options.get("firstFile"));
    fileReader.onloadend = function() {
        if (fileReader.result.trim().slice(0, 1) !== ">") {
            if (secondFile === "alignmentFile")
                $("#first-area-message-partialP").show();
            else
                $("#first-area-message").show();
        } else if (options.get(secondFile)) {
            var alignmentFileReader = new FileReader();
            alignmentFileReader.readAsText(options.get(secondFile));
            alignmentFileReader.onloadend = function() {
                if (alignmentFileReader.result.trim().slice(0, 1) !== ">") {
                    if (secondFile === "alignmentFile")
                        $("#second-area-message-partialP").show();
                    else
                        $("#second-area-message").show();
                } else
                    getDataAsync(options);
            }
        } else //main condition
            getDataAsync(options);
    }
}

function checkFileAreaAndSendQuery(options) {
    if ($(".second-area").length && !$(".second-area").is(':hidden') && !(options.get("secondFileArea") && options.get("firstFileArea")))
        $("#both-areas-message").show();
    else if ($(".second-area").is(':hidden') && !options.get("firstFileArea"))
        $("#one-area-message").show();
    else if (options.get("firstFileArea") && options.get("firstFileArea").trim().slice(0, 1) !== ">")
        $("#first-area-message").show();
    else if (options.get("secondFileArea") && options.get("secondFileArea").trim().slice(0, 1) !== ">")
        $("#second-area-message").show();
    else
        getDataAsync(options);
}

function checkFileAndSendQueryNeib(options) {
    if (!options.get("firstFile")) {
        checkFileAreaAndSendQueryNeib(options);
        return;
    }
    var fileReader = new FileReader();
    fileReader.readAsText(options.get("firstFile"));
    fileReader.onloadend = function() {
        console.log("fileReader.result.trim().slice(0, 1) " + fileReader.result.trim().slice(0, 2))
        if (fileReader.result.trim().slice(0, 1) !== ">" && fileReader.result.trim().slice(0, 1) !== "(")
            $("#first-area-message").show();
        else {
            if (fileReader.result.trim().slice(0, 1) === "(") {
                options.set("treeFile", options.get("firstFile"));
                options.delete("firstFile");
                options.set("isFullPipeline", "false");
            }
            getDataAsyncNeighborGenes(options);
        }
    }
}

function checkFileAreaAndSendQueryNeib(options) {
    if (!options.get("firstFileArea"))
        $("#one-area-message").show();
    else if (options.get("firstFileArea") && options.get("firstFileArea").trim().slice(0, 1) !== ">"
        && options.get("firstFileArea").trim().slice(0, 1) !== "(")
        $("#first-area-message").show();
    else {
        if (options.get("firstFileArea").trim().slice(0, 1) === "(") {
            options.set("treeFileArea", options.get("firstFileArea"));
            options.delete("firstFileArea");
            options.set("isFullPipeline", "false");
        }
        getDataAsyncNeighborGenes(options);
    }
}

function setCookie() {
    typeof Cookies.get('protoTree') == 'undefined'
        ? Cookies.set('protoTree', ''+Math.random(), { expires: 1 })
        : null;
}

function getDataAsync(options) {
	$.ajax({
	      type: 'POST',
	      url: 'process-request',
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
    window.location.replace("tree/" + jobId);
}

function redirectNeighborGenes(jobId) {
    window.location.replace("gene-neighborhoods/tree/" + jobId);
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}