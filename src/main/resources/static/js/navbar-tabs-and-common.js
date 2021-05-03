$(document).ready( function() {
    // control main navbar
	var mainTab = "#" + $('#mainTab').text();
	mainNavbar(mainTab);
    // when the user scrolls the page, execute adjustStickyNavbar()
    window.onscroll = function() {adjustStickyNavbar()};
    // on the help page
    attacheOnChevronClick('#domains-href');
    attacheOnChevronClick('#neib-href');
    attacheOnChevronClick('#credits-href');
    // attache the url of the page to show to a user that he can check the status of his job by this url.
    $("#check-by-url").attr("href", window.location.href);
    $("#check-by-url").text(window.location.href);
    setCopyrightDate();

    // control navbar toggling on load
    controlNavBarToggle();
    // control navbar toggling on 'show'/'hide' events
    $('.navbar-collapse').on('shown.bs.collapse hidden.bs.collapse', function () {
        controlNavBarToggle();
    });
    // initialize tooltips
    $('[data-toggle="tooltip"]').tooltip();

    //set codirect check input to "on"
    $('#codirect-value').attr('checked', true);
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

// control navbar toggling on 'window resize' event
$(window).resize(function(){
    controlNavBarToggle();
});

// navbar control function
function controlNavBarToggle() {
    var navbarVisible = $(".navbar-collapse").is(":visible");
        if (window.innerWidth <= 317) {
        setWrapperClassHeight(navbarVisible, "870px", "540px");
    } else  if (window.innerWidth <= 372) {
        setWrapperClassHeight(navbarVisible, "830px", "510px");
    } else if (window.innerWidth <= 506) {
        setWrapperClassHeight(navbarVisible, "805px", "490px");
    } else if (window.innerWidth <= 679) {
        setWrapperClassHeight(navbarVisible, "785px", "490px");
    } else if (window.innerWidth <= 768) {
        setWrapperClassHeight(navbarVisible, "760px", "460px");
    } else if (window.innerWidth <= 991) {
        $('.wrapper').css("height", "510px");
    } else if (window.innerWidth <= 1159) {
        $('.wrapper').css("height", "340px");
    } else {
        $('.wrapper').css("height", "290px");
    }
}

function setWrapperClassHeight(navbarVisible, heightL, heightS) {
    if (navbarVisible)
      $('.wrapper').css("height", heightL);
    else
      $('.wrapper').css("height", heightS);
}