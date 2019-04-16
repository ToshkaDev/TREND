$(document).ready( function() {
    attacheOnChevronClick('#domains-href');
     attacheOnChevronClick('#neib-href');
	var mainTab = "#" + $('#mainTab').text();
	mainNavbar(mainTab);
	// When the user scrolls the page, execute myFunction
    window.onscroll = function() {myFunction()};
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

// Get the navbar
navbar = document.getElementById("navbar");

// Get the offset position of the navbar
sticky = navbar.offsetTop;
// Add the sticky class to the navbar when you reach its scroll position. Remove "sticky" when you leave the scroll position
function myFunction() {

    if (window.pageYOffset >= sticky) {
        navbar.classList.add("sticky")
    } else {
        navbar.classList.remove("sticky");
    }
}