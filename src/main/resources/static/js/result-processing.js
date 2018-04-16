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
    if (data.status[0] != 'notReady') {
        clearInterval(fileGetter);

        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }

        if (data.result.length > 1) {
            $('#results-load').attr('href', data.result[1]);
            d3.select("div#svgContainer")
            .append("svg")
            .attr("class", "bioTree")
            .attr("width", 1000)
            .attr("height", 1200);

            d3.select('svg.bioTree')
            .append("g")
            .attr("id", "treeContainer")
            .attr("transform", "translate(0, 0)");




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