//Figure
svgWidth = 700;
svgHeight = 300;

clusterFrameHeight = svgHeight*0.17;
frameRectXRadius = 7;
frameRectYRadius = 7;

//Gene cluster
directGeneFigureTopY = 3.4;
geneFigureWidth = 17;
directGeneFigureBottomY = directGeneFigureTopY + geneFigureWidth;
directGeneFigureMiddlePointY = directGeneFigureTopY + geneFigureWidth*0.5;
reverseGeneFigureTopY = directGeneFigureBottomY+10;
reverseGeneFigureBottomY = reverseGeneFigureTopY + geneFigureWidth;
reverseGeneFigureMiddlePointY = reverseGeneFigureTopY + geneFigureWidth*0.5;
geneFigureArrowLen = 10;
fillColour = 'white';
borderColour = '#a3a3c2';
currentGeneColour = "#7FB9AB";
lastGeneStop = null;
yShiftOfClusterRegardingLeafeYCoord = 25;
axisYTranslation = 25;

//Gene info box
//Need to make textPositionFactorDirect, textPositionFactorReverse,
//substractions from directGeneInfoBoxY and reverseGeneInfoBoxY calculable, not hard-coded
infoBoxWidth = 300;
infoBoxHeight = svgHeight*0.3;
infoBoxRectXRadius = 3;
infoBoxRectYRadius = 3;
directGeneInfoBoxY = directGeneFigureTopY-89;
reverseGeneInfoBoxY = reverseGeneFigureTopY-89;
textPositionFactorDirectY = 95;
textPositionFactorReverseY = 68;
textPositionZoomCorrection = 1;
textPositionFactorX = 10;
textPositionFactorXLast = 10;
xShiftLeft = 0.03;
xShiftLeftLast = 0.03;
clusterOffsetLeft = 0.05;
clusterOffsetRight = 0.084;

function buildGeneTree(nwkObject, jsonDomainsAndGenesData) {
    var textCounter = Newick.parse(nwkObject.newick)[1]
    if (textCounter < 3)
        return false;
    createZoomableBox();
    phylocanvas = new Smits.PhyloCanvas(
        nwkObject,
        'treeContainer',
        2000, 1300*textCounter/21
    );
    d3.select('#treeContainer>svg').select('desc').text("ProtoTree");
    var processed = false;
    textCounter = 0;
    var protIdsToYCoords = {};
//    var protIds1 = [];
//    var protIds2 = [];
//    var protIds3 = [];
    var ptoteinNames = [];
    var longestXCoord = 0;
    var longestXCoordText;
    d3.select('#treeContainer>svg').selectAll('*')
        .attr("dummy", function(){
            // a) we check 'processed' because text() gives text for actual 'text' and 'tspan' tag
            // which is inside 'text' tag
            // b) we check 'textCounter++ > 0' because the first text is a text from rafael package
            // c) we check 'isNaN(d3.select(this).text())' because if it's a number then this is bootstrap value
            if (d3.select(this).text().length && isNaN(d3.select(this).text()) && !processed && textCounter++ > 0) {
                var yCoord = +d3.select(this).attr('y');
                var xCoord = +d3.select(this).attr('x');
                //var text = d3.select(this).text().replace(/^\d+_/, "");
                var text = d3.select(this).text();
                protIdsToYCoords[text] = yCoord-yShiftOfClusterRegardingLeafeYCoord;
//                text = text.split("_").slice(0, 3).join("_").trim();
//                protIdsToYCoords[text] = yCoord-yShiftOfClusterRegardingLeafeYCoord;
//                //protIds1.push(text);
//                text = text.split("_").slice(0, 2).join("_").trim();
//                protIdsToYCoords[text] = yCoord-yShiftOfClusterRegardingLeafeYCoord;
//                //protIds2.push(text);
//                text = text.split("_")[0].trim();
//                protIdsToYCoords[text] = yCoord-yShiftOfClusterRegardingLeafeYCoord;
                //protIds3.push(text);
                if (xCoord > longestXCoord) {
                    longestXCoord = xCoord;
                    longestXCoordText = d3.select(this).text();
                }
                processed = true;
            } else
                processed = false;
        });

    //getGenesAndDraw([protIds1, protIds2, protIds3], protIdsToYCoords, longestXCoord, longestXCoordText);
    getGenesAndDraw(protIdsToYCoords, longestXCoord, longestXCoordText, JSON.parse(jsonDomainsAndGenesData));
    return true;
}

function createZoomableBox() {
    var minSvgWidth = 650;
    var widthShrinkageFactor = 0.89;
    var heightShrinkageFactor = 0.8;
    var width = window.innerWidth*widthShrinkageFactor;
    var height = window.innerHeight*heightShrinkageFactor;
    var tree = d3.select('#svgContainer')
        .style("width", width)
        .style("height", height)
        .style("border", "1.4px solid #9494b8")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .style("pointer-events", "all")
        .call(d3.zoom().on("zoom", function() {
            tree.attr("transform", d3.event.transform);
            textPositionZoomCorrection = d3.event.transform.k;
        }))
        .append("g")
        .attr("id", "treeContainer");
}

//function getGenesAndDraw(protIdsList, protIdsToYCoords, xCoordinate, xCoordinateText, jsonDomainsAndGenesObj) {
function getGenesAndDraw(protIdsToYCoords, xCoordinate, xCoordinateText, jsonDomainsAndGenesObj) {
    xCoordinate = xCoordinate + xCoordinateText.length*6.5;
    var refSeqCounter = 0;

    for (proteinName in protIdsToYCoords) {
        var gene = jsonDomainsAndGenesObj[proteinName][jsonDomainsAndGenesObj[proteinName].length-1]
        console.log(gene)
        var neighbGenes = jsonDomainsAndGenesObj[proteinName].slice(0, -1);
        drawNeighborGenes(d3.select('#treeContainer>svg'), gene, neighbGenes,
                                    protIdsToYCoords[proteinName], xCoordinate);
    }
//
//    function getFetchSettings(gene, type) {
//        var geneUrl = `https://api.mistdb.caltech.edu/v1/genes?search=${gene}`;
//        var geneNeighborsUrl = `https://api.mistdb.caltech.edu/v1/genes/${gene}/neighbors`;
//        var geneFetchSettings = {
//            "async": true,
//            "crossDomain": true,
//            "url": "https://api.mistdb.caltech.edu/v1/genes/GCF_000302455.1-A994_RS01985",
//            "method": "GET",
//            "headers": {}
//        };
//        geneFetchSettings.url = type === "neighbors" ? geneNeighborsUrl : geneUrl;
//        return geneFetchSettings;
//    }
//
//    // don't show while not all neoghbor genes are loaded
//    //$('#svgContainer').hide();
//    fetchGene(protIdsList[0][0], protIdsList[1][0], protIdsList[2][0], refSeqCounter);
//    //recursive function
//    //first 3 several ajax requests using different protein ids.
//    function fetchGene(protId1, protId2, protId3, refSeqCounter) {
//        $.ajax(getFetchSettings(protId1)).done(function (gene) {
//            if (gene[0])
//                fetchNeighborGenes(gene, protIdsToYCoords, xCoordinate, protId1, protIdsList, refSeqCounter);
//            else {
//                $.ajax(getFetchSettings(protId2)).done(function (gene) {
//                    if (gene[0])
//                        fetchNeighborGenes(gene, protIdsToYCoords, xCoordinate, protId2, protIdsList, refSeqCounter);
//                     else {
//                        $.ajax(getFetchSettings(protId3)).done(function (gene) {
//                            if (gene[0])
//                                fetchNeighborGenes(gene, protIdsToYCoords, xCoordinate, protId3, protIdsList, refSeqCounter);
//                             else
//                                fetchNext(protIdsList, refSeqCounter);
//                        });
//                     }
//                });
//            }
//        }).fail(function(jqXHR, textStatus, errorThrown) {
//            fetchNext(protIdsList, refSeqCounter);
//        });
//    }
//
//    function fetchNeighborGenes(gene, protIdsToYCoords, xCoordinate, protId, protIdsList, refSeqCounter) {
//            $.ajax(getFetchSettings(gene[0].stable_id, "neighbors")).done(function (neighbGenes) {
//                if (neighbGenes.length == 10)
//                    drawNeighborGenes(d3.select('#treeContainer>svg'), gene[0], neighbGenes,
//                        protIdsToYCoords[protId], xCoordinate);
//                fetchNext(protIdsList, refSeqCounter);
//            }).fail(function(jqXHR, textStatus, errorThrown) {
//                fetchNext(protIdsList, refSeqCounter);
//            });
//    }
//
//    function fetchNext(protIdsList, refSeqCounter) {
//        if (++refSeqCounter < protIdsList[0].length)
//            fetchGene(protIdsList[0][refSeqCounter], protIdsList[1][refSeqCounter], protIdsList[2][refSeqCounter], refSeqCounter);
//        else {
//            //$('#svgContainer').show();
//        }
//    }
}

function drawNeighborGenes(domElement, gene, neighbGenes, yCoordinate, xCoordinate) {
    var clusterPictureWidth = svgWidth - 0.19*svgWidth;
    //var span = neighbGenes[neighbGenes.length-1].stop - neighbGenes[0].start;
    var span = neighbGenes[neighbGenes.length-2].stop - neighbGenes[0].start;
    var genomeNeighbStart = neighbGenes[0].start - span*clusterOffsetLeft;
    var genomeNeighbStop = neighbGenes[neighbGenes.length-1].stop + span*clusterOffsetRight;
    var lastGeneStop = neighbGenes[neighbGenes.length-1].stop;
    var geneScale = d3.scaleLinear()
        .domain([genomeNeighbStart, genomeNeighbStop])
        .range([4, clusterPictureWidth-5]);

    var containerGroup = domElement.append("g")
        .attr("transform", `translate(${xCoordinate}, ${yCoordinate})`);
    var	geneCluster = createFrameAndAppendGroupTags(containerGroup, [...neighbGenes, gene], clusterPictureWidth);
    createGenePaths(geneCluster, gene, geneScale);
    containerGroup.append("g").attr("class", "gene-axis-identifier-"+gene.id)
        .attr("transform", `translate(0, ${axisYTranslation})`)
        .attr("color", "#e6e6e6")
        .call(d3.axisBottom(geneScale).tickValues([]));
    createDescriptionBoxes(geneCluster, geneScale, span, gene.id);
    var divs = addHtml([...neighbGenes, gene], d3.select('#svgContainer'));
    addEventListeneres(geneCluster, geneScale);
    addHtmlEventListeneres(divs, geneScale);
}

function createFrameAndAppendGroupTags(containerGroup, neighbourGenes, clusterPictureWidth) {
    var clusterFrameWidth = clusterPictureWidth;
    console.log(neighbourGenes)
    containerGroup.insert("rect")
        .attr("transform", "translate(0,0)")
        .attr("fill-opacity", "0.00")
        .attr("stroke", "#d9d9d9")
        .attr("stroke-width", 2)
        .attr("rx", frameRectXRadius)
        .attr("ry", frameRectYRadius)
        .attr('width', clusterFrameWidth)
        .attr('height', clusterFrameHeight);

    return containerGroup.selectAll("g")
        .data(neighbourGenes)
        .enter()
        .append("g")
        .attr("class", function(d) {
            return "gene"+d.id+" gene-div "+"identifier-"+neighbourGenes[neighbourGenes.length-1].id;
        })
        .attr("transform", function(d) {
            return "translate(0, 0)";
        });
}

function createGenePaths(geneCluster, thisgene, geneScale) {
    var genePath;
    geneCluster.append("path")
        .attr("d", function(gene, i) {
            var isComplement = gene.strand === "-" ? true : false;
            if (!isComplement) {
                genePath = [
                    `M${geneScale(gene.start)}`, directGeneFigureTopY,
                    `L${geneScale(gene.stop)-geneFigureArrowLen}`, directGeneFigureTopY,
                    `L${geneScale(gene.stop)}`, directGeneFigureMiddlePointY,
                    `L${geneScale(gene.stop)-geneFigureArrowLen}`, directGeneFigureBottomY,
                    `L${geneScale(gene.start)}`, directGeneFigureBottomY, 'Z'
                ].join(" ");
            } else {
                genePath = [
                    `M${geneScale(gene.start)}`, reverseGeneFigureMiddlePointY,
                    `L${geneScale(gene.start)+geneFigureArrowLen}`, reverseGeneFigureTopY,
                    `L${geneScale(gene.stop)}`, reverseGeneFigureTopY,
                    `L${geneScale(gene.stop)}`, reverseGeneFigureBottomY,
                    `L${geneScale(gene.start)+geneFigureArrowLen}`, reverseGeneFigureBottomY, 'Z'
                ].join(" ");
            }
            return genePath;
        })
        .attr("fill", function(gene){
//            if (gene.stable_id === thisgene.stable_id)
//                return currentGeneColour;
//            return fillColour;
            return gene.clusterColor ? gene.clusterColor : fillColour;
        })
        .attr("stroke", function(gene) {
            //return borderColour;
            return gene.operon ? gene.operon : borderColour;
        })
        .attr("class", "gene-path");
}


function addHtml(neighbourGenes, d3ParentElement) {
    return d3ParentElement
        .data(["", ...neighbourGenes])
        .enter()
        .append('div')
        .style("display", "none")
        .style("position", "absolute")
        .attr("class", function(gene) {
            return "gene"+gene.id+" gene-div "+"identifier-"+neighbourGenes[neighbourGenes.length-1].id;
        })
        .html(function(gene) {
            var format = gene.strand === "-" ? "complement(coords)" : "(coords)";
            var geneCoordinates = format.replace("coords", gene.start + ".." + gene.stop);
            return `<div><a href="/genes/${gene.stable_id}">${gene.stable_id}</a></div>` +
                `<div>${gene.version}</div><div>${geneCoordinates}</div>` +
                `<div>${gene.product}<div/>`;
        });
}

function createDescriptionBoxes(geneCluster, geneScale, span, mainGeneId) {
    geneCluster.append("rect")
        .style("display", "none")
        .attr("class", function(d) {
            return "gene"+d.id+" gene-div "+"identifier-"+mainGeneId;
        })
        .attr('width', infoBoxWidth)
        .attr('height', infoBoxHeight)
        .attr("fill-opacity", "1")
        .attr("fill", "white")
        .attr("stroke", "gray")
        .attr("stroke-width", 1)
        .attr("rx", infoBoxRectXRadius)
        .attr("ry", infoBoxRectYRadius)
        .attr("x", function(gene, ind) {
            var boxXCoord = gene.start - span*xShiftLeft;
            if (ind === geneCluster.size()-2)
                boxXCoord = gene.start - span*xShiftLeftLast;
            return geneScale(boxXCoord);
        })
        .attr("y", function(gene) {
            var isComplement = gene.strand === "-" ? true : false;
            if (!isComplement)
                return directGeneInfoBoxY;
            return reverseGeneInfoBoxY;
        });
}

function addEventListeneres(geneCluster, geneScale) {
    geneCluster
    .on("mouseover", function (){
        //if the svg zoomed out proportion is less than 0.6 don't show anything
        if (textPositionZoomCorrection < 0.6)
            return
        var element = d3.select(this);
        var mainGeneIdentifier = element.attr("class").split(" ")[2];
        var axisElem = document.getElementsByClassName('gene-axis-'+mainGeneIdentifier)[0].getBoundingClientRect();
        var textPositionFactorXMain;
        var top, left, xAbsolute = axisElem["x"] + window.scrollX, yAbsolute = axisElem["y"] + window.scrollY;
        element.attr("dummy", function(gene) {
            let isComplement = gene.strand === "-" ? true : false;
            if (!isComplement)
                top = yAbsolute - (textPositionFactorDirectY*textPositionZoomCorrection) + "px;";
            else top = yAbsolute - (textPositionFactorReverseY*textPositionZoomCorrection) + "px;";
            if (gene.stop === lastGeneStop) {
                textPositionFactorXMain = textPositionFactorXLast;
            }
            else
                textPositionFactorXMain = textPositionFactorX;
            left = geneScale(gene.start)*textPositionZoomCorrection + xAbsolute + textPositionFactorXMain + "px;";
        });

        var elementsOfTheClass = document.getElementsByClassName(element.attr("class"));
        var textDiv = elementsOfTheClass[2];
        var textDivStyles = textDiv.getAttribute("style").replace("display: none","display: inline");

        var regTop = /top: \d+.+px;/;
        var regLeft = /left: \d+.+px;/;
        regTop.test(textDivStyles)
            ? textDivStyles = textDivStyles.replace(regTop, "top: " + top)
            : textDivStyles = textDivStyles + "top: " + top;

        regLeft.test(textDivStyles)
            ? textDivStyles = textDivStyles.replace(regLeft, "left: " + left)
            : textDivStyles = textDivStyles + "left: " + left;
        textDiv.setAttribute("style", textDivStyles);
        var descripRect = elementsOfTheClass[1];
        var descripRectStyles = descripRect.getAttribute("style").replace("display: none","display: inline");
        descripRect.setAttribute("style", descripRectStyles);

        element.raise();
    })
    .on("mouseout", function(){
        var element = d3.select(this);
        var elementsOfTheClass = document.getElementsByClassName(element.attr("class"));

        var textDiv = elementsOfTheClass[2];
        var textDivStyles = textDiv.getAttribute("style").replace("display: inline","display: none");
        textDiv.setAttribute("style", textDivStyles);

        var descripRect = elementsOfTheClass[1];
        var descripRectStyles = descripRect.getAttribute("style").replace("display: inline","display: none");
        descripRect.setAttribute("style", descripRectStyles);
    });
}

function addHtmlEventListeneres(divs, geneScale) {
    divs
    .on("mouseover", function () {
        //if the svg zoomed out proportion is less than 0.6 don't show anything
        if (textPositionZoomCorrection < 0.6)
            return
        var element = d3.select(this);
        var mainGeneIdentifier = element.attr("class").split(" ")[2];
        var axisElem = document.getElementsByClassName('gene-axis-'+mainGeneIdentifier)[0].getBoundingClientRect();
        var xAbsolute = axisElem["x"] + window.scrollX, yAbsolute = axisElem["y"] + window.scrollY;
        var textPositionFactorXMain;
        element
            .style("top", function(gene) {
                var isComplement = gene.strand === "-" ? true : false;
                if (!isComplement)
                    return yAbsolute - (textPositionFactorDirectY*textPositionZoomCorrection) + "px";
                return yAbsolute - (textPositionFactorReverseY*textPositionZoomCorrection) + "px";

            })
            .style("left", function(gene) {
                if (gene.stop === lastGeneStop)
                    textPositionFactorXMain = textPositionFactorXLast;
                else
                    textPositionFactorXMain = textPositionFactorX;
                return geneScale(gene.start)*textPositionZoomCorrection + xAbsolute + textPositionFactorXMain + "px";
            })
            .style("display", "inline");

        var elementsOfTheClass = document.getElementsByClassName(element.attr("class"));
        var descripRect = elementsOfTheClass[1];
        var descripRectStyles = descripRect.getAttribute("style").replace("display: none","display: inline");
        descripRect.setAttribute("style", descripRectStyles);
        element.raise();
    })
    .on("mouseout", function(){
        var element = d3.select(this);
        element.style("display", "none");

        var elementsOfTheClass = document.getElementsByClassName(element.attr("class"));
        var descripRect = elementsOfTheClass[1];
        var descripRectStyles = descripRect.getAttribute("style").replace("display: inline","display: none");
        descripRect.setAttribute("style", descripRectStyles);
    });
}