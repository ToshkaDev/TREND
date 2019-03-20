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
//subtractions from directGeneInfoBoxY and reverseGeneInfoBoxY calculable, not hard-coded
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

PROCESSED_STABLE_IDS = [];
GENE_NEIB_COUNTER = 0;

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
    var ptoteinNames = [];
    var longestXCoord = 0;
    var longestXCoordText;
    d3.select('#treeContainer>svg').selectAll('*')
        .attr("dummy", function(){
            // a) we check 'processed' because text() gives text for actual 'text' and 'tspan' tag
            // which is inside 'text' tag
            // b) we check 'textCounter++ > 0' because the first text is a description (ProtoTree)
            // c) we check 'isNaN(d3.select(this).text())' because if it's a number then this is bootstrap value
            if (d3.select(this).text().length && isNaN(d3.select(this).text()) && !processed && textCounter++ > 0) {
                var yCoord = +d3.select(this).attr('y');
                var xCoord = +d3.select(this).attr('x');
                var text = d3.select(this).text();
                protIdsToYCoords[text] = yCoord-yShiftOfClusterRegardingLeafeYCoord;
                if (xCoord > longestXCoord) {
                    longestXCoord = xCoord;
                    longestXCoordText = d3.select(this).text();
                }
                processed = true;
            } else
                processed = false;
        });

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


function getGenesAndDraw(protIdsToYCoords, xCoordinate, xCoordinateText, jsonDomainsAndGenesObj) {
    xCoordinate = xCoordinate + xCoordinateText.length*6.5;
    var refSeqCounter = 0;

    for (proteinName in protIdsToYCoords) {
        var gene = jsonDomainsAndGenesObj[proteinName][jsonDomainsAndGenesObj[proteinName].length-1]
        var neighbGenes = jsonDomainsAndGenesObj[proteinName].slice(0, -1);
        drawNeighborGenes(d3.select('#treeContainer>svg'), gene, neighbGenes,
                                    protIdsToYCoords[proteinName], xCoordinate);
    }
}

function drawNeighborGenes(domElement, gene, neighbGenes, yCoordinate, xCoordinate) {
    var clusterPictureWidth = svgWidth - 0.19*svgWidth;
    var span = neighbGenes[neighbGenes.length-1].stop - neighbGenes[0].start;
    var genomeNeighbStart = neighbGenes[0].start - span*clusterOffsetLeft;
    var genomeNeighbStop = neighbGenes[neighbGenes.length-1].stop + span*clusterOffsetRight;
    var lastGeneStop = neighbGenes[neighbGenes.length-1].stop;
    var geneScale = d3.scaleLinear()
        .domain([genomeNeighbStart, genomeNeighbStop])
        .range([4, clusterPictureWidth-5]);

    //If a duplicate id is encountered, make it unique
    if (PROCESSED_STABLE_IDS.includes(gene.id)) {
        gene.id = gene.id + "_" + GENE_NEIB_COUNTER++;
    }
    PROCESSED_STABLE_IDS.push(gene.id);

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
            return gene.clusterColor ? gene.clusterColor : fillColour;
        })
        .attr("stroke", function(gene) {
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
            return `<div><a href="https://mistdb.com/genes/${gene.stable_id}" target="_blank">${gene.stable_id}</a></div>` +
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
            var isComplement = gene.strand === "-" ? true : false;
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