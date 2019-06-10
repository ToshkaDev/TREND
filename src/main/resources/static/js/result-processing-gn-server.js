$(document).ready(function(){
    var jobId = $('#jobId').text();
    stageList = [];
    controlProgressBar(jobId);
    // getIfReady(jobId) is in result-processing-common.js
    getIfReady(jobId);
});

function processRetrievedDataAsync(data) {
    if (data.status[0] === 'noSuchBioJob') {
        clearInterval(fileGetter);
        $('.wait-for-it').hide();
        $('.no-such-biojob').show();
    } else if (data.status[0] === 'Error') {
        clearInterval(fileGetter);
        $('.wait-for-it').hide();
        if (data.status.length > 1)
            $('.server-error').html(data.status[1]);
        $('.server-error').show();
    } else  if (data.status[0] === 'ready') {
        clearInterval(fileGetter);
        // displayStage(data, statusReady=false) is in result-processing-common.js
        displayStage(data, true);

        if (data.result.length >= 1) {
            // Add corresponding links to download buttons
            $('#tree-load').attr('href', data.result[0]);
            $('#json-load').attr('href', data.result[1]);
            if (data.result.length >= 3 ) {
                $('#alignment-load').attr('href', data.result[2]);
                $('#alignment-load').show();
                if (data.result.length == 4) {
                    $('#cdhit-clusters-load').show();
                    $('#cdhit-clusters-load').attr('href', data.result[3]);
                }
            }

            var newickTree = data.result[0];
            var jsonDomainsAndGenes = data.result[1];
            $.get(newickTree, function(data, status) {
                nwkObject = {newick: data};
                $.get(jsonDomainsAndGenes, function(data, status) {
                    jsonDomainsAndGenesData = data
                    if (!buildGeneTree(nwkObject, jsonDomainsAndGenesData)) {
                        $('.malformed-newick').show();
                    } else {
                        $('.result-container').show();
                        onDownload();
                    }
                });
            });
        }

	} else if (data.status[0] === 'notReady') {
	    // displayStage(data, statusReady=false) is in result-processing-common.js
	    displayStage(data);
	}
}

function onDownload() {
    d3.select("#results-load").on("click", function() {
      d3.select(this)
        .attr("href", 'data:application/octet-stream;base64,' + btoa(d3.select("#svgContainer>svg>#treeContainer").html()))
        .attr("download", "ProtoTree.svg")
    });
}

function controlProgressBar(jobId) {
    var stageNumToPercentFullPipe = {"1":"33", "2":"66", "3": "100"};
    var stageNumToPercentFullPipeWithRedund = {"1":"25", "2":"50", "3": "75", "4": "100"};
    var stageNumToPercentPartialPipe = {"1":"50", "2":"100"};
    var stageNumToPercent = stageNumToPercentFullPipe;
    var urlParts = jobId.split("-");
    if (urlParts[1] == "f") {
        if (urlParts[2] == "r")
            stageNumToPercent = stageNumToPercentFullPipeWithRedund;
    } else
        stageNumToPercent = stageNumToPercentPartialPipe;
    /*moveProgressBar is in fields-processing.js */
    moveProgressBar(stageNumToPercent);
}