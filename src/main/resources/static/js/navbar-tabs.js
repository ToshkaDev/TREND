$(document).ready( function() {
	var mainTab = "#" + $('#mainTab').text();
	var subnavigationTab = "#" + $('#subnavigation-tab').text();
	console.log("mainTab " + mainTab);
    console.log("subnavigationTab " + subnavigationTab);
	mainNavbar(mainTab);

	subTabNavbar(subnavigationTab);
});

function mainNavbar(tab) {
	$(tab).addClass('active');
}

function subTabNavbar(tab) {
	$(tab).addClass('active');
}