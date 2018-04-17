$(document).ready(function (){
    var jobId = $('#jobId').text();
    getIfReady(jobId);

});


function getIfReady(jobId) {
    console.log('Checking if ready ');

    console.log('jobId ' + jobId + Cookies.get('protoTree'));
    fileGetter = setInterval(function() {tryToGetFileName(jobId + "-" + Cookies.get('protoTree'))}, 5000);
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

function processRetrievedDataAsync(data) {

//
//    drag = d3.behavior.drag()
//        .origin(function(d) { return d; })
//        .on("dragstart", dragstarted)
//        .on("drag", dragged)
//        .on("dragend", dragended);

    if (data.status[0] != 'notReady') {
        clearInterval(fileGetter);

        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }
        if (data.result.length > 1) {
            $('#results-load').attr('href', data.result[1]);
            var tree = d3.select("div#svgContainer")
            .append("svg")
            .attr("width", 1000)
            .attr("height", 1200)
            .style("pointer-events", "all")
            .call(d3.zoom().on("zoom", function() {tree.attr("transform", d3.event.transform)}))
            .append("g")
            .attr("id", "treeContainer");

            d3.xml(data.result[1]).then(function(xml) {
                console.log(xml.documentElement)
                document.getElementById("treeContainer").appendChild(xml.documentElement);
            });

        }
        $('.result-container').show();
	}

}




function dragstarted(d) {
  d3.event.sourceEvent.stopPropagation();
  d3.select(this).classed("dragging", true);
}

function dragged(d) {
  d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
}

function dragended(d) {
  d3.select(this).classed("dragging", false);
}


function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}