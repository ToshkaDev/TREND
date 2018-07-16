$(document).ready(function (){
    var jobId = $('#jobId').text();
    renderedClass = null;
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

function prepareTreeContainer() {
    var minSvgWidth = 650;
    var widthShrinkageFactor = 0.8;
    var heightShrinkageFactor = 0.8;
    var width = window.innerWidth*widthShrinkageFactor;
    var height = window.innerHeight*heightShrinkageFactor;
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
function processRetrievedDataAsync(data) {
    if (data.status[0] === 'ready') {
        clearInterval(fileGetter);

        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }
        if (data.result.length > 1) {
            $('#results-load').attr('href', data.result[1]);
            prepareTreeContainer();
            d3.xml(data.result[1]).then(function(xml) {
                console.log(xml.documentElement)
                document.getElementById("treeContainer").appendChild(xml.documentElement);
            });

            $.get(data.result[3], function(data, status){
                addEventListeners(data);
            });

        }
        $('.result-container').show();
	}

}

function addEventListeners(data) {
	var re = /^\d{1,}_/;
	var currentClassName;
	var proteinIdToRendered = {}
	var sel = d3.select("#treeContainer");
	var ssel = sel.select("svg")
	console.log(d3.select("#treeContainer"))
	console.log(d3.select("#treeContainer>svg"))
	console.log(ssel)
	console.log(d3.select('#treeContainer').selectAll('svg>g'))
	console.log("here00")
	d3.select('#treeContainer').selectAll('*')
	  .attr("dummy", function(){
	  console.log("here0")
			if (d3.select(this).text().length > 0 && re.test(d3.select(this).text())) {
				console.log("here1")
				currentClassName = d3.select(this).text()
				d3.select(this).attr("class", currentClassName+"_text");
				d3.select(this).selectAll('*').attr("class", currentClassName);

			} else if (currentClassName) {
				console.log("here2")
				console.log("currentClassName " + currentClassName)
				d3.select(this).attr("class", currentClassName);
				d3.select(this).selectAll('*').attr("class", currentClassName);

				d3.select(this).on("click", function(event){
						checkTableAndDisplay(event, currentClassName, data, proteinIdToRendered);
					});
				d3.select(this).selectAll('*').on("click", function(event){
						checkTableAndDisplay(event, currentClassName, data, proteinIdToRendered);
					});
			}
	  });
}

function checkTableAndDisplay(event, currentClassName, data, proteinIdToRendered) {
    event.stopPropagation();
    if (!proteinIdToRendered[currentClassName]) {
        createTable(data, currentClassName);
        proteinIdToRendered[currentClassName] = true;
        renderedClass = currentClassName;
    } else {
        $("."+currentClassName).show();
        renderedClass = currentClassName;
    }
}

function createTable(data, currentClassName) {
    var organizedData = organizeData(data, currentClassName);
    var divToAddTo = createDivToAddTo(currentClassName);
    makeTable($(divToAddTo), organizedData);

}

function organizeData(data, currentClassName) {
    var domainOrganizedData = [];
    var tmOrganizedData = [];
    var additionalOrganizedData = []
    var score;
    var dataAsJson = JSON.parse(data)[currentClassName];
    var hrefRootPath;
    if (dataAsJson.predictor == "RpsBlast") {
        score = "Bitscore";
        hrefRootPath = "https://www.ncbi.nlm.nih.gov/cdd?term=";
    } else if (dataAsJson.predictor == "hmmscan"){
        score = "Probability";
        hrefRootPath = "https://pfam.xfam.org/search/keyword?query=";
    }
    var domainHeaders = ["No.", "Domain", "Start", "End", score, "eValue", "Alignment"];
    var tmHeaders = ["No.", "Start", "End"];
    var additionalHeaders = ["Signal Peptide?", "Topology"];
    domainOrganizedData.push(domainHeaders);
    tmOrganizedData.push(tmHeaders);
    additionalOrganizedData.push(additionalHeaders);

    var domainCounter = 1;
    var tmCounter = 1;
    var domainRaw;
    var tmRaw;
    var domainName;
    for (var domain in dataAsJson.domains) {
        domainRaw = [];
        domainName = "<a href=" + hrefRootPath+domain.domainName + ">domain.domainName" + "</a>";
        domdainRaw.push(domainCounter++);
        domdainRaw.push(domainName);
        domdainRaw.push(domain.aliStart);
        domdainRaw.push(domain.aliEnd);
        domdainRaw.push(domain.bitscore);
        domdainRaw.push(domain.eValue);
        domdainRaw.push(domain.alignmentToModelType);
        domainOrganizedData.push(domainRaw);
    }
    for (var tm in dataAsJson.tmInfo.tmRegions) {
        tmRaw = [];
        tmRaw.push(tmCounter++);
        tmRaw.push(tm.tmEnd);
        tmRaw.push(tm.tmStart);
        tmOrganizedData.push(tmRaw);
    }
    var additionalRaw = [dataAsJson.tmInfo.possibSigPep, dataAsJson.tmInfo.tmTopology];
    additionalOrganizedData.push(additionalRaw);

    return organizedData;
}

//TODO
function createDivToAddTo(currentClassName) {
    var selectedLeaf = document.getElementsByClassName(currentClassName+"_text")[0].getBoundingClientRect();
    var xAbsolute = selectedLeaf["x"] + window.scrollX;
    var yAbsolute = selectedLeaf["y"] + window.scrollY;
    var divElement = document.createElement("div").addClass(currentClassName+"_table");
    $(".table-container").after(divElement);
    d3.select("."+currentClassName+"_table")
    .style("top", yAbsolute+"px")
    .style("left", xAbsolute+"px");
    return currentClassName+"_table";
}

function makeTable(container, data) {
    var table = $("<table/>").addClass('table table-condensed');
    $.each(data, function(rowIndex, r) {
        var row = $("<tr/>");
        $.each(r, function(colIndex, c) {
            row.append($("<t"+(rowIndex == 0 ?  "h" : "d")+"/>").text(c));
        });
        table.append(row);
    });
    return container.append(table);
}

$(document).on("click", function () {
    $("."+renderedClass).hide();
});

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}