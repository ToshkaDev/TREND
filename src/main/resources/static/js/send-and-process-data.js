$(document).ready(function (){
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

function getDataAsync(options) {
    console.log("getDataAsync() called!");
	$.ajax({
	      type: 'POST',
	      url: 'prototree/process-request',
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
    window.location.replace("prototree/tree-for-you/" + jobId);
}

function error(jqXHR, textStatus, errorThrown) {
	window.alert('Error happened!');
	console.log(jqXHR);
}