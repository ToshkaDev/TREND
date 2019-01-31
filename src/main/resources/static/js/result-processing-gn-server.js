$(document).ready(function(){
	takeCareOfValidators();
	takeCareOfFields();
    setCookie();
    var jobId = $('#jobId').text();
    stageList = [];
    getIfReady(jobId);
});

function setCookie() {
    typeof Cookies.get('protoTree') == 'undefined'
        ? Cookies.set('protoTree', ''+Math.random(), { expires: 1 })
        : null;
}

function getIfReady(jobId) {
    console.log('Checking if ready ');
    console.log('jobId ' + jobId + " " + Cookies.get('protoTree'));
    fileGetter = setInterval(function() {
        tryToGetFileName(jobId + "-" + Cookies.get('protoTree'))
    }, 2000);
}

function tryToGetFileName(jobId) {
    $.ajax({
      type: 'GET',
      url: 'get-filename',
      dataType:'json',
      contentType: 'application/json',
      data: {"jobId": jobId},
      success: processRetrievedDataAsync,
      error: error
    });
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}

function processRetrievedDataAsync(data) {
    if (data.status[0] === 'noSuchBioJob') {
        clearInterval(fileGetter);
        $('.wait-for-it').hide();
        $('.no-such-biojob').show();
    }
    else  if (data.status[0] === 'ready') {
        clearInterval(fileGetter);
        displayStage(data.stage[0], true);

        if (data.result.length >= 1) {
            // Add corresponding links to download buttons
            if (data.result.length == 2)
                $('#alignment-load').attr('href', data.result[1]);
            $('#tree-load').attr('href', data.result[0]);
            var newickTree = data.result[0];
            $.get(newickTree, function(data, status) {
                if (!buildGeneTree({newick: data})) {
                    $('.malformed-newick').show();
                } else {
                     $('.result-container').show();
                }
            });
        }

	} else if (data.status[0] === 'notReady') {
	    displayStage(data.stage[0]);
	}
}

function displayStage(dataStage, statusReady=false) {
    var stages = JSON.parse(dataStage.replace(/'/g, '"'));
    for (var stage of stages) {
        var stageReady = stage.split("-")[0]
        if (!statusReady) {
            if (!stageList.includes(stageReady)) {
                processStageMessage(stageReady);
            }
        } else {
            if (!stageList.includes(stageReady)) {
                processStageMessage(stageReady);
            }
            if (stage.split("-").length == 2) {
                $('.stage-element').last().append("<span class='glyphicon glyphicon-ok complete'></span>");
            }
        }
    }
}

function processStageMessage(stage) {
    if (stageList.length > 0) {
        $('.stage-element').last().append("<span class='glyphicon glyphicon-ok complete'></span>");
        $('#result-stage').append("<div class='stage-element'><h4>" + stage + "</h4></div>");
    } else {
        $('#result-stage').append("<div class='stage-element'><h4>" + stage + "</h4></div>");
    }
    stageList.push(stage);
}