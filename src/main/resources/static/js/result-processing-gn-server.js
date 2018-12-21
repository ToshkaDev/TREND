$(document).ready(function(){
	takeCareOfValidators();
	takeCareOfFields();

    setCookie();
    var jobId = $('#jobId').text();
    stageList = [];
    getIfReady(jobId);
});