$(document).ready(function (){
	$('.result-container').hide();
	$('.extra-result-container').hide();
	takeCareOfValidators();
	takeCareOfFields();

    setCookie();
    $('#Go').click(function() {
    	options = getOptions();
    	getData(options);
    });

    $('#GoAsync').click(function() {
    	options = getOptions();
    	getDataAsync(options);

    });

    $('#options').click(function() {
        $('.extra-result-container').show();
        $('#options').hide();
    });
});

function setCookie() {
    typeof Cookies.get('protoTree') == 'undefined'
        ? Cookies.set('protoTree', ''+Math.random(), { expires: 1 })
        : null;
}

//function getIfReady(jobId) {
//    console.log("Job is launched");
//    console.log('jobId ' + jobId);
//    fileGetter = setInterval(function() {tryToGetFileName(jobId)}, 5000);
//}
//
//function tryToGetFileName(jobId) {
//    $.ajax({
//      type: 'GET',
//      url: 'get-filename',
//      dataType:'json',
//      contentType: 'application/json',
//      data: {"jobId": jobId},
//      success: processRetrievedDataAsync,
//      error: error
//    });
//}
//
//function getData(options) {
//	$.ajax({
//	      type: 'POST',
//	      url: 'process-request',
//	      data : options,
//	      success: processRetrievedData,
//	      error: error,
//	      contentType: false,
//	      processData: false,
//	      dataType:'text',
//	      enctype: 'multipart/form-data'
//	    });
//}

function getDataAsync(options) {
    console.log("getDataAsync() called!");
	$.ajax({
	      type: 'POST',
	      url: 'process-request',
	      data : options,
	      success: redirect,
	      error: error,
	      contentType: false,
	      processData: false,
	      dataType:'text',
	      enctype: 'multipart/form-data'
	    });
}

function redirect(jobId) {
    console.log("Job is launched");
    console.log('jobId ' + jobId);
    window.location.replace("tree-for-you/" + jobId);
}
//function processRetrievedData(data) {
//    console.log("file name is " + data)
//	$('#results-load').attr("href", data);
//	$('.result-container').show();
//}
//
//function processRetrievedDataAsync(data) {
//    console.log("trying!!!")
//    if (data.status[0] != 'notReady') {
//        if (data.result.length === 1) {
//            $('#results-load').attr('href', data.result[0]);
//        }
//        if (data.result.length > 1) {
//            $('#results-load').attr('href', data.result[1]);
//        }
//
//        $('.result-container').show();
//        clearInterval(fileGetter);
//        console.log("data.result[1] "  + data.result[1])
//        window.location.href ="tree-for-you/" + 223;
//	}
//}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}