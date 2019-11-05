function getIfReady() {
    fileGetter = setInterval(function() {
        tryToGetFileName()
    }, 2000);
}

function tryToGetFileName() {
    pathList = location.pathname.split("/");
    // global object 'paramsOfTrend' is used
    paramsOfTrend["jobId"] = pathList[pathList.length-1];
    $.ajax({
      type: 'GET',
      url: 'get-filename',
      dataType:'json',
      contentType: 'application/json',
      data: paramsOfTrend,
      success: processRetrievedDataAsync,
      error: error
    });
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}

function displayStage(data, statusReady=false) {
    var stages = JSON.parse(data.stage[0].replace(/'/g, '"'));
    var stageDetails = data.stageDetails[0];
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
    if (stageDetails) {
        if (!$('#result-stageDetails').length && $('#aln-tree-stage').length) {
            $('#aln-tree-stage').after("<div id='result-stageDetails'></div>" )
        }
        $("#result-stageDetails").html(stageDetails + "<br/><br/>");
    }
}

function processStageMessage(stage) {
    var alnTreeStage = "";
    if (stage === "Aligning sequences and building phylogenetic tree.")
        alnTreeStage = "aln-tree-stage"
    if (stageList.length > 0) {
        $('.stage-element').last().append("<span class='glyphicon glyphicon-ok complete'></span>");
        $('#result-stage').append("<div class='stage-element' id=" + alnTreeStage + "><h4>" + stage + "</h4></div>");
    } else {
        $('#result-stage').append("<div class='stage-element' id=" + alnTreeStage + "><h4>" + stage + "</h4></div>");
    }
    stageList.push(stage);
}

function initializeLocationParams() {
    paramsOfTrend = {};
    var paramsList = location.search.replace(/^\?/, '').split('&');
    for (var i = 0; i < paramsList.length; i++) {
        var pair = paramsList[i].split('=');
        paramsOfTrend[pair[0]] = decodeURIComponent(pair[1]);
    }
}

function moveProgressBar(stageNumToPercent) {
  var elem = document.getElementById("pipelineProgressBar");
  var width = 0;
  var prevStageNum = 0;
  var id = setInterval(frame, 100);
  function frame() {
    var stageNum = $('.stage-element .glyphicon-ok').length;
    if (width >= 100) {
      clearInterval(id);
      $('#pipelineProgressBar').removeClass("active");
    } else {
        if (stageNum > prevStageNum) {
            if (width < +stageNumToPercent[stageNum]) {
                width = stageNumToPercent[stageNum];
                elem.style.width = width + '%';
                elem.innerHTML = width * 1  + '%';
            }
            prevStageNum = stageNum;
        }
        else {
            if (stageNum+1 in stageNumToPercent && width < +stageNumToPercent[stageNum+1] && width < 99) {
                width++;
                elem.style.width = width + '%';
                elem.innerHTML = width * 1  + '%';
            }
        }
    }
  }
}