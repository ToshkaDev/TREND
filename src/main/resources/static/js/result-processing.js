$(document).ready(function (){
    var jobId = $('#jobId').text();
    console.log("jobId  " + jobId)
    renderedClass = null;
    infoPostfix = "_table";
    stageList = [];
    xOffset = 400;
    yOffset = 550;
    buttonIds = ["Domains", "TMs", "LCRs", "Sequence", "Additional"];
    buttonIdToTableClass = {"Domains": "domain-table", "TMs": "tm-table", "LCRs": "lcr-table", "Sequence": "sequence-table", "Additional": "additional-table"};
    entityToButton = {"domainOrganizedData": buttonIds[0], 'tmOrganizedData': buttonIds[1],
    "lcrOrganized": buttonIds[2], "sequenceData": buttonIds[3], "additionalOrganizedData": buttonIds[4]};
    getIfReady(jobId);
});

function getIfReady(jobId) {
    fileGetter = setInterval(function() {
        tryToGetFileName(jobId)
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
        displayStage(data.stage[0], true);

        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }
        if (data.result.length > 1) {
            // Add corresponding links to download buttons
            $('#results-load').attr('href', data.result[1]);
            $('#tree-load').attr('href', data.result[0]);
            featureData = null;
            // if all the files were provided for the pipeline
            if (data.result.length == 4) {
                $('#alignment-load').attr('href', data.result[2]);
                $('#features-load').attr('href', data.result[3]);

                featureData = data.result[3];
                // if alignment file was not provided for the pipeline (the partial one)
            } else if (data.result.length == 3) {
                $('#alignment-load').hide();
                $('#features-load').attr('href', data.result[2]);
                featureData = data.result[2];
            }
            prepareTreeContainer();
            d3.xml(data.result[1]).then(function(xml) {
                svgPicture = xml.documentElement
                document.getElementById("treeContainer").appendChild(svgPicture);
                $.get(featureData, function(data, status){
                    addEventListeners(data);
                });
            });
            $(window).resize(function() {
                d3.select('#svgContainer>svg').remove();
                prepareTreeContainer();
                document.getElementById("treeContainer").appendChild(svgPicture);
                $.get(featureData, function(data, status){
                    addEventListeners(data);
                });
            });
        }
        $('.result-container').show();
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

function addEventListeners(data) {
    //remove title and description
    d3.select('#treeContainer>svg').select('title').text("");
    d3.select('#treeContainer>svg').select('desc').text("");

    featureJSON = JSON.parse(data)
    var domStart, domStop;
	var notAllowedClassCharacters = /(\W|_)/g;
	var currentClassName;
	var proteinIdToRendered = {};
	trueClassNameToChanged = {};
	var pathFound = false;
	var treeCirclePath = "M3,1.5 C3,2.32843 2.32843,3 1.5,3 C0.671573,3 0,2.32843 0,1.5 C0,0.671573 0.671573,0 1.5,0 C2.32843,0 3,0.671573 3,1.5"
	var domainCount;
	d3.select('#treeContainer>svg').selectAll('*')
	  .attr("dummy", function(){
			if (d3.select(this).text().length > 0 && d3.select(this).text() in featureJSON) {
			    classNameForJson = d3.select(this).text();
				currentClassName = classNameForJson.replace(notAllowedClassCharacters, '');
				trueClassNameToChanged[currentClassName] = classNameForJson;

				d3.select(this).attr("class", currentClassName+"_text");
				d3.select(this).selectAll('*').attr("class", currentClassName);
				domainCount = 0;

			} else if (currentClassName) {
			    if (!d3.select(this).select("path").empty()) {
			       // "C" means that this shape is ellipse an so it's a domain
			       if (d3.select(this).select("path").attr("d").split(' ')[1][0] == "C"
			           && d3.select(this).select("path").attr("d").split(' ')[4][0] == "C"
			           && d3.select(this).select("path").attr("d").trim() != treeCirclePath) {
                        d3.select(this).select("path").attr("domainFlag", classNameForJson + ":" + domainCount++)
			       }
			    }

				d3.select(this).attr("class", currentClassName);
				d3.select(this).selectAll('*').attr("class", currentClassName);

				d3.select(this).on("click", function(){
						checkTableAndDisplay(d3.event, d3.select(this).attr("class"), proteinIdToRendered);
                        var proteinName = d3.select(this).attr("class");
                        var domainFlag = d3.select(this).attr("domainFlag");
                        var domainCount;
                        if ($("." + proteinName + infoPostfix).is(':visible') && domainFlag) {
                            domainCount = domainFlag.split(":")[1];
                            var seq = getHighlightedSequence(proteinName, domainCount);
                            $("#Sequence").click();
                            $("." + "protein-sequence").html(seq);
                        }
					});
				d3.select(this).selectAll('*').on("click", function(){
				        // stopping event propagation so that it will not be handled by the handler
				        // specified above. Commented out checkTableAndDisplay because we don't need to create a table
				        // for tree branches and branch names.
				        event.stopPropagation();
						//checkTableAndDisplay(d3.event, d3.select(this).attr("class"), proteinIdToRendered);
					});
			}
	  });
}

function checkTableAndDisplay(event, currentClassName, proteinIdToRendered) {
    event.stopPropagation();
    if (renderedClass) {
        $("." + renderedClass + infoPostfix).hide();
    }
    if (!proteinIdToRendered[currentClassName]) {
        createTable(event, currentClassName);
        proteinIdToRendered[currentClassName] = true;
        renderedClass = currentClassName;
    } else {
        updatePositionAndShow(event, currentClassName + infoPostfix);
        renderedClass = currentClassName;
    }
}

function createTable(event, currentClassName) {
    var organizedData = organizeData(trueClassNameToChanged[currentClassName]);
    var divToAddTo = createDivToAddTo(event, currentClassName, trueClassNameToChanged[currentClassName]);
    addButtons(divToAddTo, organizedData);
    var isFirstEncountered = false;
    isFirstEncountered = makeTable(divToAddTo, organizedData.domainOrganizedData, "domain-table", isFirstEncountered);
    isFirstEncountered = makeTable(divToAddTo, organizedData.tmOrganizedData, "tm-table", isFirstEncountered);
    isFirstEncountered = makeTable(divToAddTo, organizedData.lcrOrganized, "lcr-table", isFirstEncountered);
    isFirstEncountered = makeTable(divToAddTo, organizedData.sequenceData, "sequence-table", isFirstEncountered);
    isFirstEncountered = makeTable(divToAddTo, organizedData.additionalOrganizedData, "additional-table", isFirstEncountered);
}

function organizeData(currentClassName) {
    var domainOrganizedData = [], tmOrganizedData = [], sequenceData = [], additionalOrganizedData = [], lcrOrganized = [], score;
    var dataAsJson = featureJSON[currentClassName];

    if (dataAsJson) {
        var sequenceHeader = [""]
        sequenceData.push(sequenceHeader)
        sequenceData.push([dataAsJson.sequence])

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
                domainName = "<span><a target='_blank' href='" + hrefRootPath+domain.domainName + "'>" + domain.domainName + "</a></span>";
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
            lcrOrganized.push(lcrHeaders);
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
        'additionalOrganizedData': additionalOrganizedData,
        'sequenceData': sequenceData
    };
}

function createDivToAddTo(event, currentClassName, classNameForJson) {
    var xCoor = event.clientX - xOffset + "px";
    var yCoor = event.clientY - yOffset + "px";
    var readyClassName = currentClassName + infoPostfix;
    var divElement = $("<div/>");
    var removeElement = $("<div><span class='proto-tree-link'><span class='glyphicon glyphicon-remove pull-right'></span></a></div>");
    divElement.addClass(readyClassName + " protein-info-container");
    divElement.append(removeElement);
    divElement.append($("<h4>" + classNameForJson + "</h4><hr/>"));

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
    buttonTexts.forEach((buttonText) => {
        button = $("<button/>").addClass("btn btn-md protein-info-buttons ");
        button.attr("id", buttonText);
        addButtonEventListener(button);
        button.html(buttonText);
        $("."+container).append(button);
    });
}

function addButtonEventListener(buttonElement) {
    buttonElement.click(function(event) {
        event.stopPropagation();

        if ($(this).attr("class").split(" ")[2] == 'protein-info-buttons') {
           $(".protein-info-buttons-selected").removeClass("protein-info-buttons-selected").addClass("protein-info-buttons");
           $(this).removeClass("protein-info-buttons").addClass("protein-info-buttons-selected");
        }
        if ($(this).attr("id") === "Domains") {
            $("." + "domain-table").show();
            $("." + "lcr-table").hide();
            $("." + "tm-table").hide();
            $("." + "additional-table").hide();
            $("." + "sequence-table").hide();
        } else if ($(this).attr("id") === "TMs") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").hide();
            $("." + "tm-table").show();
            $("." + "additional-table").hide();
            $("." + "sequence-table").hide();
        } else if ($(this).attr("id") === "LCRs") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").show();
            $("." + "tm-table").hide();
            $("." + "additional-table").hide();
            $("." + "sequence-table").hide();
        } else if ($(this).attr("id") === "Additional") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").hide();
            $("." + "tm-table").hide();
            $("." + "additional-table").show();
            $("." + "sequence-table").hide();
        } else if ($(this).attr("id") === "Sequence") {
            $("." + "domain-table").hide();
            $("." + "lcr-table").hide();
            $("." + "tm-table").hide();
            $("." + "additional-table").hide();
            $("." + "sequence-table").show();
        }
    });
}

function makeTable(container, data, tableClass, isFirstEncountered) {
    var divBeginning = "<div>";
    if (tableClass == "sequence-table")
        divBeginning = "<div class='protein-sequence' style='width: 200px; font-family: monospace; font-size: 12px'>";
    if (data && data.length > 0) {
        var table = $("<table/>").addClass('table table-condensed ' + tableClass);
        $.each(data, function(rowIndex, r) {
            var row = $("<tr/>");
            $.each(r, function(colIndex, c) {
                if (rowIndex == 0)
                    row.append($("<th/>").html(c));
                else
                    row.append($("<td/>").html(divBeginning + c + "</div>"));
            });
            table.append(row);
        });
        $("."+container).append(table);
        if (!isFirstEncountered) {
            return true;
        }
        $("."+tableClass).hide();
    }
    return isFirstEncountered;
}

function getTranslatedCoordinate(coordinate) {
    // shift to zero-based first
    var myCoordinate = --coordinate;
    if (myCoordinate <= 59) {
        return myCoordinate;
    } else if (myCoordinate > 59) {
        return myCoordinate + Math.floor(myCoordinate / 60);
    }
}

function getHighlightedSequence(proteinName, domainCount) {
    var domainWithNoOverlaps = featureJSON[trueClassNameToChanged[proteinName]]["domainsWithNoOverlaps"][domainCount];
    var proteinSequnce = featureJSON[trueClassNameToChanged[proteinName]].sequence
    domStart = getTranslatedCoordinate(+domainWithNoOverlaps.start);
    domEnd = getTranslatedCoordinate(+domainWithNoOverlaps.end);
    sequenceFirstFragment = (''+proteinSequnce).substring(0, domStart);
    sequenceMiddleFragment = "<span style='background-color: #d8f7dd'>" + proteinSequnce.substring(domStart, domEnd+1) + "</span>";
    sequenceLastFragment = proteinSequnce.substring(domEnd+1, proteinSequnce.length);
    return sequenceFirstFragment+sequenceMiddleFragment+sequenceLastFragment;
}

function getNotHighlightedSequence(proteinName) {
    return featureJSON[trueClassNameToChanged[proteinName]].sequence;
}

function updatePositionAndShow(event, readyClassName) {
    var xCoor = event.clientX - xOffset + "px";
    var yCoor = event.clientY - yOffset + "px";
    $("." + readyClassName).css({"left": xCoor, "top": yCoor});
    $("." + readyClassName).show();
    var buttons = $("."+readyClassName).children(".btn");

    var buttonId;
    for (button of buttons) {
        buttonId = $(button).attr("id");
        if (buttonId === "Sequence") {
            $(button).removeClass("protein-info-buttons").addClass("protein-info-buttons-selected");
            $("."+buttonIdToTableClass[buttonId]).show();
        } else {
            $(button).removeClass("protein-info-buttons-selected").addClass("protein-info-buttons");
            $("."+buttonIdToTableClass[buttonId]).hide();
        }
     }
}

$(document).on("click", function () {
    $("." + renderedClass + infoPostfix).hide();
});

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}