$(document).ready(function (){
    var jobId = $('#jobId').text();
    renderedClass = null;
    infoPostfix = "_table";
    xOffset = 400;
    yOffset = 200;
    buttonIds = ["Domains", "TMs", "LCRs", "Additional"];
    buttonIdToTableClass = {"Domains": "domain-table", "TMs": "tm-table", "LCRs": "lcr-table", "Additional": "additional-table"};
    entityToButton = {"domainOrganizedData": buttonIds[0], 'tmOrganizedData': buttonIds[1], "lcrOrganized": buttonIds[2], "additionalOrganizedData": buttonIds[3]};
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
        .style("border", "1.4px solid #9494b8")
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
    event.stopPropagation();
    if (renderedClass) {
        $("." + renderedClass + infoPostfix).hide();
    }
    if (!proteinIdToRendered[currentClassName]) {
        createTable(event, data, currentClassName, trueClassNameToChanged);
        proteinIdToRendered[currentClassName] = true;
        renderedClass = currentClassName;
    } else {
        updatePositionAndShow(event, currentClassName + infoPostfix);
        renderedClass = currentClassName;
    }
}

function createTable(event, data, currentClassName, trueClassNameToChanged) {
    var organizedData = organizeData(data, trueClassNameToChanged[currentClassName]);
    var divToAddTo = createDivToAddTo(event, currentClassName);
    addButtons(divToAddTo, organizedData);
    makeTable(divToAddTo, organizedData.domainOrganizedData, "domain-table");
    makeTable(divToAddTo, organizedData.tmOrganizedData, "tm-table");
    makeTable(divToAddTo, organizedData.lcrOrganized, "lcr-table");
    makeTable(divToAddTo, organizedData.additionalOrganizedData, "additional-table");
}

function organizeData(data, currentClassName) {
    var domainOrganizedData = [], tmOrganizedData = [], additionalOrganizedData = [], lcrOrganized = [], score;
    var dataAsJson = JSON.parse(data)[currentClassName];

    if (dataAsJson) {
        var hrefRootPath;
        var domainHeaders;
        var domainCounter = 1, tmCounter = 1, lcrCounter = 1, domainRaw, tmRaw, domainName;

        if (dataAsJson.domains && dataAsJson.domains.length > 0) {
            if ( dataAsJson.domains[0].predictor == "RpsBlast") {
                score = "Bitscore";
                hrefRootPath = "https://www.ncbi.nlm.nih.gov/cdd?term=";
                domainHeaders = ["No.", "Domain", "Start", "End", "Bitscore", "Evalue", "Alignment"];
            } else if (dataAsJson.domains[0].predictor == "Hmmer"){
                score = "Probability";
                domainHeaders = ["No.", "Domain", "Start", "End", "Probability", "C-Evalue", "I-Evalue", "Alignment"];
                hrefRootPath = "https://pfam.xfam.org/search/keyword?query=";
            }

            domainOrganizedData.push(domainHeaders);
            for (var domain of dataAsJson.domains) {
                domainRaw = [];
                domainName = "<span><a href='" + hrefRootPath+domain.domainName + "'>" + domain.domainName + "</a></span>";
                domainRaw.push(domainCounter++ + ".");
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
        }

        if (dataAsJson.tmInfo['tmRegions'] && dataAsJson.tmInfo['tmRegions'].length > 0) {
            var tmHeaders = ["No.", "Start", "End"];
            var additionalHeaders = ["Signal Peptide?", "Topology"];
            tmOrganizedData.push(tmHeaders);
            additionalOrganizedData.push(additionalHeaders);
            for (var tm of dataAsJson.tmInfo['tmRegions']) {
                tmRaw = [];
                tmRaw.push(tmCounter++ + ".");
                tmRaw.push(tm.tmSart);
                tmRaw.push(tm.tmEnd);
                tmOrganizedData.push(tmRaw);
            }
            var additionalRaw = [dataAsJson.tmInfo.possibSigPep, dataAsJson.tmInfo.tmTopology];
            additionalOrganizedData.push(additionalRaw);
        }

        if (dataAsJson.lowComplexity && dataAsJson.lowComplexity.length > 0) {
            var lcrHeaders = ["No.", "Start", "End"];
            lcrOrganized.push(tmHeaders);
            for (var lcr of dataAsJson.lowComplexity) {
                lcrRaw = [];
                lcrRaw.push(lcrCounter++ + ".");
                lcrRaw.push(lcr.start);
                lcrRaw.push(lcr.end);
                lcrOrganized.push(lcrRaw);
            }
        }
    }

    return {
        'domainOrganizedData': domainOrganizedData,
        'tmOrganizedData': tmOrganizedData,
        'lcrOrganized': lcrOrganized,
        'additionalOrganizedData': additionalOrganizedData
    };
}

function createDivToAddTo(event, currentClassName) {
    var xCoor = event.clientX - xOffset + "px";
    var yCoor = event.clientY - yOffset + "px";
    var readyClassName = currentClassName + infoPostfix;
    var divElement = $("<div/>");
    var removeElement = $("<div><a href='#' class='proto-tree-link'><span class='glyphicon glyphicon-remove pull-right'></span></a></div>");
    divElement.addClass(readyClassName + " protein-info-container");
    divElement.append(removeElement);
    divElement.append($("<h4>" + currentClassName.replace(/_/g, " ") + "</h4><hr/>"));

    divElement.click(function(event){
        event.stopPropagation();
    });

    removeElement.click(function(event){
        $("." + renderedClass + infoPostfix).hide();
    });

    $("#svgContainer").after(divElement);
    divElement.css({"position": "absolute", "left": xCoor, "top": yCoor});
    return readyClassName;
}

function addButtons(container, organizedData) {
    var buttonTexts = [];
    for (var entity in organizedData) {
        organizedData[entity].length > 0 && entityToButton[entity] ? buttonTexts.push(entityToButton[entity]) : null;
    }
    var button;
    buttonTexts.forEach((buttonText, idx) => {
        if (idx === 0) {
            button = $("<button/>").addClass("btn btn-md protein-info-buttons-selected ");
        } else {
            button = $("<button/>").addClass("btn btn-md protein-info-buttons ");
        }

        button.attr("id", buttonText);
        addButtonEventListener(button);
        button.html(buttonText);
        $("."+container).append(button);
    });

    return buttonTexts[0];
}

function addButtonEventListener(buttonElement) {
    buttonElement.click(function(event) {
        event.stopPropagation();
        $(".protein-info-buttons-selected").removeClass("protein-info-buttons-selected").addClass("protein-info-buttons");
        $(this).removeClass("protein-info-buttons").addClass("protein-info-buttons-selected");
        if ($(this).attr("id") === "Domains") {
            $("." + "domain-table").show();
            $("." + "lcr-table").hide();
            $("." + "tm-table").hide();
            $("." + "additional-table").hide();
        } else if ($(this).attr("id") === "TMs") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").hide();
            $("." + "tm-table").show();
            $("." + "additional-table").hide();
        } else if ($(this).attr("id") === "LCRs") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").show();
            $("." + "tm-table").hide();
            $("." + "additional-table").hide();
        } else if ($(this).attr("id") === "Additional") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").hide();
            $("." + "tm-table").hide();
            $("." + "additional-table").show();
        }
    });
}

function makeTable(container, data, tableClass) {
    if (data && data.length > 0) {
        var table = $("<table/>").addClass('table table-condensed ' + tableClass);
        $.each(data, function(rowIndex, r) {
            var row = $("<tr/>");
            $.each(r, function(colIndex, c) {
                row.append($("<t"+(rowIndex == 0 ?  "h" : "d")+"/>").html(c));
            });
            table.append(row);
        });
        $("."+container).append(table);
    }

}

function updatePositionAndShow(event, readyClassName) {
    var xCoor = event.clientX - xOffset + "px";
    var yCoor = event.clientY - yOffset + "px";
    $("." + readyClassName).css({"left": xCoor, "top": yCoor});
    $("." + readyClassName).show();
    var buttons = $("."+readyClassName).children(".btn");

    var buttonId, counter = 0;
    for (button of buttons) {
        buttonId = $(button).attr("id");
        if (counter === 0) {
            $(button).removeClass("protein-info-buttons").addClass("protein-info-buttons-selected");
            $("."+buttonIdToTableClass[buttonId]).show();
        } else {
            $(button).removeClass("protein-info-buttons-selected").addClass("protein-info-buttons");
            $("."+buttonIdToTableClass[buttonId]).hide();
        }
        counter = counter + 1;
     }
}

$(document).on("click", function () {
    $("." + renderedClass + infoPostfix).hide();
});

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}