//Figure
svgWidth = 800;
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
borderColour = '#68686b';
currentGeneColour = "#7FB9AB";
lastGeneStop = null;
yShiftOfClusterRegardingLeafeYCoord = 25;
axisYTranslation = 25;
thisGeneStroke = 3;
neighborGeneStroke = 1;

//Gene info box
//Need to make textPositionFactorDirect, textPositionFactorReverse,
//subtractions from directGeneInfoBoxY and reverseGeneInfoBoxY calculable, not hard-coded
infoBoxWidth = 450;
infoBoxHeight = svgHeight*0.64;
infoBoxRectXRadius = 3;
infoBoxRectYRadius = 3;
directGeneInfoBoxY = directGeneFigureTopY-186;
reverseGeneInfoBoxY = reverseGeneFigureTopY-186;
textPositionFactorDirectY = 185;
textPositionFactorReverseY = 155;
textPositionZoomCorrection = 1;
textPositionFactorX = 1;
textPositionFactorXLast = 10;
xShiftLeft = 0.03;
xShiftLeftLast = 0.03;
clusterOffsetLeft = 0.02;
clusterOffsetRight = 0.044;

PROCESSED_STABLE_IDS = [];
GENE_NEIB_COUNTER = 0;

function buildGeneTree(nwkObject, jsonDomainsAndGenesData, firstBuild=true) {
    var textCounter = Newick.parse(nwkObject.newick)[1]
    if (textCounter < 3)
        return false;
    if (firstBuild)
        createZoomableBox();

    var jsonDomainsAndGenesObj = JSON.parse(jsonDomainsAndGenesData);
    setSvgSizeAndBuildTree(textCounter, jsonDomainsAndGenesObj)
    if ($.isEmptyObject(jsonDomainsAndGenesObj))
        $("#neighbors-not-retrieved-from-MiST").fadeIn();

    d3.select('#treeContainer>svg').select('desc').text("ProtoTree");
    var processed = false;
    textCounter = 0;
    var protIdsToYCoords = {};
    var ptoteinNames = [];
    var longestXCoord = 0;
    var longestXCoordText;
    var currentLongestLeaf = 0;
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
                if ((xCoord + text.length) > currentLongestLeaf) {
                    longestXCoord = xCoord;
                    longestXCoordText = text;
                    currentLongestLeaf = xCoord + text.length;
                }
                processed = true;
            } else
                processed = false;
        });
    getGenesAndDraw(protIdsToYCoords, longestXCoord, longestXCoordText, jsonDomainsAndGenesObj);
    return true;
}

function setSvgSizeAndBuildTree(textCounter, jsonDomainsAndGenesObj) {
    var fullSvgWidth = 2800, reductionFactor = 1;
    // factoring the tree vertical size depending on the number of leaves
    if (textCounter <= 7) {
        reductionFactor = 0.58;
    } else if (textCounter <= 100) {
        reductionFactor = textCounter/12;
    } else if (textCounter <= 1000) {
        reductionFactor = textCounter/19;
    } else {
        reductionFactor = textCounter/23;
    }

    var proteinNum = 0, neighbLengths = [];
    for (var protein in jsonDomainsAndGenesObj) {
        if (proteinNum++ > 20)
            break;
        neighbLengths.push(jsonDomainsAndGenesObj[protein].length);
    }
    var neighbourhoodLength = Math.max(...neighbLengths);
    if (neighbourhoodLength <= 15) {
        svgWidth = 800;
        fullSvgWidth = 2800;
        textPositionFactorX = 1;
    } else if (neighbourhoodLength > 15 && neighbourhoodLength <= 23) {
        svgWidth = 1200;
        fullSvgWidth = 4200;
        textPositionFactorX = -9;
    } else {
        svgWidth = 2000;
        fullSvgWidth = 7000;
        textPositionFactorX = -17;
    }

    phylocanvas = new Smits.PhyloCanvas(
        nwkObject,
        'treeContainer',
        fullSvgWidth, 1300*reductionFactor
    );
}

function createZoomableBox() {
    var minSvgWidth = 650;
    var widthShrinkageFactor = 0.93;
    var heightShrinkageFactor = 0.91;
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
            setWidthHeightOfDescriptionBoxes();
        }))
        .append("g")
        .attr("id", "treeContainer");
}

function setWidthHeightOfDescriptionBoxes() {
    d3.select('#svgContainer').selectAll('.description-box')
    .attr("width", infoBoxWidth/textPositionZoomCorrection)
    .attr("height", infoBoxHeight/textPositionZoomCorrection);
}

function getGenesAndDraw(protIdsToYCoords, xCoordinate, xCoordinateText, jsonDomainsAndGenesObj) {
    xCoordinate = xCoordinate + xCoordinateText.length*7.1;
    var refSeqCounter = 0;
    d3.select('#treeContainer>svg').attr("style", "");
    for (proteinName in protIdsToYCoords) {
        if (jsonDomainsAndGenesObj.hasOwnProperty(proteinName)) {
            var gene = jsonDomainsAndGenesObj[proteinName][jsonDomainsAndGenesObj[proteinName].length-1]
            var neighbGenes = jsonDomainsAndGenesObj[proteinName].slice(0, -1);
            if (neighbGenes && neighbGenes.length > 0) {
                drawNeighborGenes(d3.select('#treeContainer>svg'), gene, neighbGenes,
                                            protIdsToYCoords[proteinName], xCoordinate);
            }
        }
    }
}

function drawNeighborGenes(domElement, gene, neighbGenes, yCoordinate, xCoordinate) {
    var clusterPictureWidth = svgWidth - 0.19*svgWidth;
    var spanStart = neighbGenes[0].start;
    var spanStop = neighbGenes[neighbGenes.length-1].stop;
    if (gene.start < spanStart) { spanStart = gene.start; }
    else if (gene.stop > spanStop) { spanStop = gene.stop; }

    var span = spanStop - spanStart;
    var genomeNeighbStart = spanStart - span*clusterOffsetLeft;
    var genomeNeighbStop = spanStop + span*clusterOffsetRight;
    var lastGeneStop = spanStop;
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
    createDescriptionBoxes(geneCluster, geneScale, span, gene);
    var divs = addHtml([...neighbGenes, gene], d3.select('#svgContainer'));
    addEventListeneres(geneCluster, geneScale, gene);
    addHtmlEventListeneres(divs, geneScale, gene);
}

function getCondition(gene, thisgene){
    var forcelyCodirect = $("#codirect-value").attr("checked");
    var condition;
    //gene.strand === thisgene.strand => render the gene as direct (left to write direction) regardless of an actual strand ("-" or "+")
    if (forcelyCodirect)
        condition = gene.strand === thisgene.strand ? true : false;
    else
        condition = gene.strand === "+" ? true : false;
    return condition;
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
            var condition = getCondition(gene, thisgene);
            if (condition) {
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
            return gene.operonColour ? gene.operonColour : borderColour;
        })
        .attr("stroke-width", function(gene){
            return gene.id === thisgene.id ? thisGeneStroke : neighborGeneStroke;
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
            var domainsList = "";
            if (gene && gene.Aseq && gene.Aseq.pfam31NamesOnly) {
                gene.Aseq.pfam31NamesOnly.forEach(domainName =>
                    domainsList+=`<a href="https://www.ebi.ac.uk/interpro/search/text/${domainName}" target="_blank">${domainName}</a>, `);
            }
            domainsList = domainsList.slice(0, -2);

            const refSeqPrefixes = new Set(['NP', 'AP', 'XP', 'YP', 'WP']);
            var mistDatabase = "https://mistdb.com/mist/genes/" + gene.stable_id;
            if (gene.version && !refSeqPrefixes.has(gene.version.split("_")[0]))
                mistDatabase = "https://mistdb.com/mist-metagenomes/genes/" + gene.stable_id;

            return `<div class="gene-info-style"><span style="font-weight: bold">Product: </span>${gene.product}</div>` +
                `<div class="gene-info-style"><span style="font-weight: bold">MiST Id: </span><a href="${mistDatabase}" target="_blank">${gene.stable_id}</a></div>` +
                `<div class="gene-info-style"><span style="font-weight: bold">NCBI Id: </span><a href="https://www.ncbi.nlm.nih.gov/protein/${gene.version}" target="_blank">${gene.version}</a></div>` +
                `<div class="gene-info-style"><span style="font-weight: bold">Gene coordinates: </span>${geneCoordinates}</div>` +
                `<div class="gene-info-style"><span style="font-weight: bold">Domains: </span>${domainsList}</div>` +
                `<div class="gene-info-style"><span style="font-weight: bold">Cluster Id: </span>${gene.clusterId}</div>`;
        });
}

function createDescriptionBoxes(geneCluster, geneScale, span, thisgene) {
    geneCluster.append("rect")
        .style("display", "none")
        .attr("class", function(d) {
            return "gene"+d.id+" gene-div "+"identifier-"+thisgene.id + " description-box";
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
            var condition = getCondition(gene, thisgene);
            if (condition)
                return directGeneInfoBoxY;
            return reverseGeneInfoBoxY;
        });
}

function addEventListeneres(geneCluster, geneScale, thisgene) {
    geneCluster
    .on("mouseover", function (){
        //if the svg zoomed out proportion is less than 0.4 don't show anything
        if (textPositionZoomCorrection < 0.4)
            return
        var element = d3.select(this);
        var mainGeneIdentifier = element.attr("class").split(" ")[2];
        var axisElem = document.getElementsByClassName('gene-axis-'+mainGeneIdentifier)[0].getBoundingClientRect();
        var textPositionFactorXMain;
        var top, left, xAbsolute = axisElem["x"] + window.scrollX, yAbsolute = axisElem["y"] + window.scrollY;
        var geneId = null;
        var isComplement = false;
        var positionCorrection = 1, positionCorrectionReveres = 1;
        //Adjusting position of the text
        if (textPositionZoomCorrection > 1.4 && textPositionZoomCorrection < 2.2) {
            positionCorrection = 1.03;
            positionCorrectionReveres = 1.01;
        } else if (textPositionZoomCorrection >= 2.2 && textPositionZoomCorrection < 2.8) {
            positionCorrection = 1.05;
            positionCorrectionReveres = 1.02;
        } else if (textPositionZoomCorrection >= 2.8) {
            positionCorrection = 1.07;
            positionCorrectionReveres = 1.04;
        }

        element.attr("dummy", function(gene) {
            geneId = gene.id;
            condition = getCondition(gene, thisgene);
            if (condition)
                top = (yAbsolute - textPositionFactorDirectY)/positionCorrection  + "px;";
            else
                top = (yAbsolute - textPositionFactorReverseY)/positionCorrectionReveres + "px;";
            if (gene.stop === lastGeneStop)
                textPositionFactorXMain = textPositionFactorXLast;
            else
                textPositionFactorXMain = textPositionFactorX;
            var positionCorrectionLeft = positionCorrection > 1.03 ? positionCorrection - 0.02 : 1;
            left = (geneScale(gene.start)*textPositionZoomCorrection + xAbsolute + textPositionFactorXMain)/positionCorrectionLeft + "px;";
        });

        geneCluster.select(".gene"+geneId+">.description-box").attr("y", function() {
            if (condition)
                return directGeneInfoBoxY/textPositionZoomCorrection;
            // Corrections of vertical position of info boxes for reverse genes
            else {
                if (textPositionZoomCorrection < 1)
                    return reverseGeneInfoBoxY/(textPositionZoomCorrection*0.93);
                else if (textPositionZoomCorrection > 1.3 && textPositionZoomCorrection < 1.67)
                    return reverseGeneInfoBoxY/(textPositionZoomCorrection*1.1);
                else if (textPositionZoomCorrection >= 1.67 && textPositionZoomCorrection < 2)
                    return reverseGeneInfoBoxY/(textPositionZoomCorrection*1.2);
                else if (textPositionZoomCorrection >= 2)
                    return reverseGeneInfoBoxY/(textPositionZoomCorrection*1.3);
                return reverseGeneInfoBoxY/textPositionZoomCorrection;
            }
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

function addHtmlEventListeneres(divs, geneScale, thisgene) {
    divs
    .on("mouseover", function () {
        //if the svg zoomed out proportion is less than 0.6 don't show anything
        if (textPositionZoomCorrection < 0.4)
            return
        var element = d3.select(this);
        var mainGeneIdentifier = element.attr("class").split(" ")[2];
        var axisElem = document.getElementsByClassName('gene-axis-'+mainGeneIdentifier)[0].getBoundingClientRect();
        var xAbsolute = axisElem["x"] + window.scrollX, yAbsolute = axisElem["y"] + window.scrollY;
        var textPositionFactorXMain;
        var geneId = null;
        var isComplement = false;
        var positionCorrection = 1, positionCorrectionReveres = 1;
        //Adjusting position of the text
        if (textPositionZoomCorrection > 1.4 && textPositionZoomCorrection < 2.2) {
            positionCorrection = 1.03;
            positionCorrectionReveres = 1.01;
        } else if (textPositionZoomCorrection >= 2.2 && textPositionZoomCorrection < 2.8) {
            positionCorrection = 1.05;
            positionCorrectionReveres = 1.02;
        } else if (textPositionZoomCorrection >= 2.8) {
            positionCorrection = 1.07;
            positionCorrectionReveres = 1.04;
        }
        element
            .style("top", function(gene) {
                condition = getCondition(gene, thisgene);
                if (condition)
                    return (yAbsolute - textPositionFactorDirectY)/positionCorrection + "px";
                return (yAbsolute - textPositionFactorReverseY)/positionCorrectionReveres + "px";

            })
            .style("left", function(gene) {
                if (gene.stop === lastGeneStop)
                    textPositionFactorXMain = textPositionFactorXLast;
                else
                    textPositionFactorXMain = textPositionFactorX;
                var positionCorrectionLeft = positionCorrection > 1.03 ? positionCorrection - 0.02 : 1;
                return (geneScale(gene.start)*textPositionZoomCorrection + xAbsolute + textPositionFactorXMain)/positionCorrectionLeft + "px";
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