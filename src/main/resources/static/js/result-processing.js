$(document).ready(function (){
    var minSvgWidth = 650;
    var widthShrinkageFactor = 0.8;
    var heightShrinkageFactor = 0.8;
    var width = window.innerWidth*widthShrinkageFactor;
    var height = window.innerHeight*heightShrinkageFactor;
    treeContainer = prepareTreeContainer(width, height);

    var jobId = $('#jobId').text();
    getIfReady(jobId);

});

function getIfReady(jobId) {
    console.log('Checking if ready ');

    console.log('jobId ' + jobId + Cookies.get('protoTree'));
    fileGetter = setInterval(function() {
        tryToGetFileName(jobId + "-" + Cookies.get('protoTree'))
    }, 5000);
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

function prepareTreeContainer(width, height) {
    var tree = d3.select("div#svgContainer")
        .style("width", width)
        .style("height", height)
        .style("border", "1px solid #9494b8")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .style("pointer-events", "all")
        .call(d3.zoom().on("zoom", function() {tree.attr("transform", d3.event.transform)}))
        .append("g")
        .attr("id", "treeContainer");
}
function processRetrievedDataAsync(data, treeContainer, width, height) {
    if (data.status[0] != 'notReady') {
        clearInterval(fileGetter);

        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }
        if (data.result.length > 1) {
            $('#results-load').attr('href', data.result[1]);
            d3.xml(data.result[1]).then(function(xml) {
                console.log(xml.documentElement)
                document.getElementById("treeContainer").appendChild(xml.documentElement);
            });
        }
        $('.result-container').show();
	}

}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}