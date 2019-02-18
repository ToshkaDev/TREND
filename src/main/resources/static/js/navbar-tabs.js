$(document).ready( function() {
    attacheOnChevronClick('#domains-href');
     attacheOnChevronClick('#neib-href');
	var mainTab = "#" + $('#mainTab').text();
	mainNavbar(mainTab);
});

function mainNavbar(tab) {
	$(tab).addClass('active');
}

function attacheOnChevronClick(elementId) {
    $(elementId).click(function() {
        if ($(elementId + ' .chevron-info').hasClass('fa-chevron-down')) {
            $(elementId + ' .chevron-info').removeClass('fa-chevron-down');
            $(elementId + ' .chevron-info').addClass('fa-chevron-up');
        } else {
            $(elementId + ' .chevron-info').removeClass('fa-chevron-up');
            $(elementId + ' .chevron-info').addClass('fa-chevron-down');
        }
    });
}