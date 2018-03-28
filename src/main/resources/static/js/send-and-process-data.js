$(document).ready(function (){
	$('.result-container').hide();
	takeCareOfValidators();
	takeCareOfFields();

    $('#Go').click(function() {
    	options = getOptions();
    	getData(options);
    });

    $('#GoAsync').click(function() {
    	options = getOptions();
    	getDataAsync(options);
    });
});

function getIfReady(jobId) {
    console.log("Job is launched");
    console.log('jobId ' + jobId);
    fileGetter = setInterval(function() {tryToGetFileName(jobId)}, 5000);
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

function getData(options) {	
	$.ajax({
	      type: 'POST',
	      url: 'process-request',
	      data : options,
	      success: processRetrievedData,
	      error: error,
	      contentType: false,
	      processData: false,
	      dataType:'text',
	      enctype: 'multipart/form-data'
	    });
}

function getDataAsync(options, jobId) {
    console.log("getDataAsync() called!");
	$.ajax({
	      type: 'POST',
	      url: 'process-request',
	      data : options,
	      success: getIfReady,
	      error: error,
	      contentType: false,
	      processData: false,
	      dataType:'text',
	      enctype: 'multipart/form-data'
	    });
}

function processRetrievedData(data) {
    console.log("file name is " + data)
	$('#results-load').attr("href", data);
	$('.result-container').show();
}

function processRetrievedDataAsync(data) {
    if (data.status[0] != 'notReady') {
        if (data.result.length === 1) {
            $('#results-load').attr('href', data.result[0]);
        }

        $('.result-container').show();
        clearInterval(fileGetter);
	}
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}