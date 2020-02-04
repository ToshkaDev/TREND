$(document).ready( function() {
    // control main navbar
	var mainTab = "#" + $('#mainTab').text();
	mainNavbar(mainTab);
    // when the user scrolls the page, execute adjustStickyNavbar()
    window.onscroll = function() {adjustStickyNavbar()};
    // on the help page
    attacheOnChevronClick('#domains-href');
    attacheOnChevronClick('#neib-href');
    // attache the url of the page to show to a user that he can check the status of his job by this url.
    $("#check-by-url").attr("href", window.location.href);
    $("#check-by-url").text(window.location.href);
    setCopyrightDate();
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
function adjustStickyNavbar() {

    if (window.pageYOffset >= sticky) {
        navbar.classList.add("sticky")
    } else {
        navbar.classList.remove("sticky");
    }
}

function setCopyrightDate() {
    var today = new Date()
    $("#current-year").text(today.getFullYear())
}