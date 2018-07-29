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
    }, 200);
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
    var widthShrinkageFactor = 0.89;
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
                $.get(data.result[3], function(data, status){
                    addEventListeners(data);
                });
            });
        }
        $('.result-container').show();
	}

}

function addEventListeners(data) {
    console.log("renderedClass addEventListeners " + renderedClass)
	var re = /^\d{1,}_/;
	var currentClassName;
	var proteinIdToRendered = {};
	var trueClassNameToChanged = {};
	d3.select('#treeContainer>svg').selectAll('*')
	  .attr("dummy", function(){
			if (d3.select(this).text().length > 0 && re.test(d3.select(this).text())) {
			    classNameForJson = d3.select(this).text().replace(re, '');
				currentClassName = classNameForJson.replace('.', '');
				trueClassNameToChanged[currentClassName] = classNameForJson;

				d3.select(this).attr("class", currentClassName+"_text");
				d3.select(this).selectAll('*').attr("class", currentClassName);

			} else if (currentClassName) {
				d3.select(this).attr("class", currentClassName);
				d3.select(this).selectAll('*').attr("class", currentClassName);

				d3.select(this).on("click", function(){
						checkTableAndDisplay(d3.event, d3.select(this).attr("class"), data, proteinIdToRendered, trueClassNameToChanged);
					});
				d3.select(this).selectAll('*').on("click", function(){
						checkTableAndDisplay(d3.event, d3.select(this).attr("class"), data, proteinIdToRendered, trueClassNameToChanged);
					});
			}
	  });
}

function checkTableAndDisplay(event, currentClassName, data, proteinIdToRendered, trueClassNameToChanged) {
    console.log("currentClassName  checkTableAndDisplay " + currentClassName)
    console.log("renderedClass checkTableAndDisplay " + renderedClass)
    event.stopPropagation();
    if (!proteinIdToRendered[currentClassName]) {
        createTable(event, data, currentClassName, trueClassNameToChanged);
        proteinIdToRendered[currentClassName] = true;
        renderedClass = currentClassName;
    } else {
        $("."+currentClassName).show();
        renderedClass = currentClassName;
    }
}

function createTable(event, data, currentClassName, trueClassNameToChanged) {
    var organizedData = organizeData(data, trueClassNameToChanged[currentClassName]);
    var divToAddTo = createDivToAddTo(event, currentClassName);
    makeTable(divToAddTo, organizedData.domainOrganizedData);

}

function organizeData(data, currentClassName) {
    var domainOrganizedData = [], tmOrganizedData = [], additionalOrganizedData = [], score;
    var dataAsJson = JSON.parse(data)[currentClassName];
    var hrefRootPath;
    var domainHeaders;
    if (dataAsJson.domains && dataAsJson.domains[0].predictor == "RpsBlast") {
        score = "Bitscore";
        hrefRootPath = "https://www.ncbi.nlm.nih.gov/cdd?term=";
        domainHeaders = ["No.", "Domain", "Start", "End", "Bitscore", "Evalue", "Alignment"];
    } else if (dataAsJson.domains && dataAsJson.domains[0].predictor == "Hmmer"){
        score = "Probability";
        domainHeaders = ["No.", "Domain", "Start", "End", "Probability", "C-Evalue", "I-Evalue", "Alignment"];
        hrefRootPath = "https://pfam.xfam.org/search/keyword?query=";
    }
    var tmHeaders = ["No.", "Start", "End"];
    var additionalHeaders = ["Signal Peptide?", "Topology"];
    domainOrganizedData.push(domainHeaders);
    tmOrganizedData.push(tmHeaders);
    additionalOrganizedData.push(additionalHeaders);

    var domainCounter = 1, tmCounter = 1, domainRaw, tmRaw, domainName;
    for (var domain of dataAsJson.domains) {
        domainRaw = [];
        domainName = "<span><a href='" + hrefRootPath+domain.domainName + "'>" + domain.domainName + "</a></span>";
        domainRaw.push(domainCounter++);
        domainRaw.push(domainName);
        domainRaw.push(domain.aliStart);
        domainRaw.push(domain.aliEnd);
        domainRaw.push(domain[score.toLowerCase()]);
        if (score == "Bitscore") {
            domainRaw.push(domain.eValue);
        } else if (score == "Probability") {
            domainRaw.push(domain.ceValue);
            domainRaw.push(domain.ieValue);
        }
        domainRaw.push(domain.alignmentToModelType);
        domainOrganizedData.push(domainRaw);
    }
    for (var tm of dataAsJson.tmInfo['tmRegions']) {
        tmRaw = [];
        tmRaw.push(tmCounter++);
        tmRaw.push(tm.tmEnd);
        tmRaw.push(tm.tmStart);
        tmOrganizedData.push(tmRaw);
    }
    var additionalRaw = [dataAsJson.tmInfo.possibSigPep, dataAsJson.tmInfo.tmTopology];
    additionalOrganizedData.push(additionalRaw);

    return {'domainOrganizedData': domainOrganizedData, 'tmOrganizedData': tmOrganizedData, 'tmOrganizedData': tmOrganizedData};
}

function createDivToAddTo(event, currentClassName) {
    var xCoor = event.clientX - 400 + "px";
    var yCoor = event.clientY - 200 + "px";
    console.log("event.clientY " + event.clientY)
    var divElement = document.createElement("div");
    divElement.innerHTML = "<h4>" + currentClassName + "</h4>"
    divElement.className = currentClassName+"_table div-container";
    $("#svgContainer").after(divElement);
    $("."+currentClassName+"_table").css({"position": "absolute", "left": xCoor, "top": yCoor});
    return currentClassName+"_table";
}

function makeTable(container, data) {
    var table = $("<table/>").addClass('table table-condensed');
    $.each(data, function(rowIndex, r) {
        var row = $("<tr/>");
        $.each(r, function(colIndex, c) {
            row.append($("<t"+(rowIndex == 0 ?  "h" : "d")+"/>").html(c));
        });
        table.append(row);
    });
    console.log("table " + table)
    return $("."+container).append(table);
}

$(document).on("click", function () {
    $("."+renderedClass).hide();
});

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}