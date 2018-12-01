$(document).ready(function (){
	takeCareOfValidators();
	takeCareOfFields();

    setCookie();
    $('#GoAsync').click(function() {
    	options = getOptions();
    	var dataIsSent = checkFileAndSendQuery(options, "secondFile");
    	if (!dataIsSent)
    	    	dataIsSent = checkFileAreaAndSendQuery(options);
        if (!dataIsSent)
            window.alert("To start the analysis you need to choose a file with full-length protein sequences or paste these sequences in the area below.");
   	    //getDataAsync(options);
    });

    $('#GoAsyncPartial').click(function() {
        options = getOptions();
        var dataIsSent = checkFileAndSendQuery(options, "alignmentFile");
        if (!dataIsSent)
            window.alert("Both a file with protein sequences and a file with phylogenetic tree should be provided.");
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
        if (!(options.get("firstFile") && options.get("treeFile")))
            return false;
    } else if (secondFile === "secondFile") {
        if (!options.get("firstFile"))
            return false;
    }

    var fileReader = new FileReader();
    console.log(options.get("firstFile"))
    fileReader.readAsText(options.get("firstFile"));
    fileReader.onloadend = function() {
        if (fileReader.result.trim().slice(0, 1) !== ">") {
            window.alert("Wrong format of the file with protein sequences. The fasta format is expected to begin with '>' sign.");
        } else if (options.get(secondFile)) {
            var alignmentFileReader = new FileReader();
            alignmentFileReader.readAsText(options.get(secondFile));
            alignmentFileReader.onloadend = function() {
                if (alignmentFileReader.result.trim().slice(0, 1) !== ">") {
                    window.alert("Wrong format of the file with the alignment. The fasta format is expected to begin with '>' sign.");
                } else
                    getDataAsync(options);
            }
        } else {
            getDataAsync(options);
        }
    }
}

function checkFileAreaAndSendQuery(options) {
    if (!options.get("firstFileArea"))
        return false;
    if (options.get("firstFileArea").trim().slice(0, 1) !== ">") {
        window.alert("Wrong format of the sequences in the first file area. The fasta format is expected to begin with '>' sign.");
    } else if (options.get("secondFileArea").trim().slice(0, 1) !== ">") {
        window.alert("Wrong format of the sequences in the second file area. The fasta format is expected to begin with '>' sign.");
    } else
        getDataAsync(options);
}

function setCookie() {
    typeof Cookies.get('protoTree') == 'undefined'
        ? Cookies.set('protoTree', ''+Math.random(), { expires: 1 })
        : null;
}

function getDataAsync(options) {
    console.log("options " + options)
    console.log("getDataAsync() called!");
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

function redirect(jobId) {
    console.log("Job is launched");
    console.log('jobId ' + jobId);
    window.location.replace("tree-for-you/" + jobId);
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}