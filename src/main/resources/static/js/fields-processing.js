function takeCareOfValidators() {
    jQuery.validator.addMethod("clearIncorrect", function(value, element) {
        if (!$.isNumeric(value) || (value.slice(0,1) == 0)) {
            $(element).val('');
            return false;
        } else {
            return true;
        }
    }, "Column should be a number, greater than 0.");


    var valid1 = $("#first").validate( {
        rules: {
            "first-col": {
                clearIncorrect: true,
                digits: true
            }
        }
    });
    var valid2 = $("#second").validate( {
        rules: {
            "second-col": {
                clearIncorrect: true,
                digits: true
            }
        }
    });

    $("#first-col").keyup(function() {
        $("#first-col").valid();
    });

    $("#second-col").keyup(function() {
        $("#second-col").valid();
    });

    $("#first-col").focusout(function() {
       typeof valid1 != 'undefined' ? valid1.resetForm() : valid1;
       typeof valid2 != 'undefined' ? valid2.resetForm() : valid2;
    });
    $("#second-col").focusout(function() {
       typeof valid1 != 'undefined' ? valid1.resetForm() : valid1;
       typeof valid2 != 'undefined' ? valid2.resetForm() : valid2;
    });
}


function takeCareOfFields() {
	$('#first-file').change(function() {
	    $('#first-file-info').html(this.files[0].name);
	    $('#first-file-area').val('');
	    $('.result-container').hide();
	});

    $('#second-file').change(function() {
	    $('#second-file-info').html(this.files[0].name);
	    $('#second-file-area').val('');
	    $('.result-container').hide();
	});

    $('#first-file-area').keyup(function() {
        $('#first-file-info').empty();
        $('#first-file').val('');
        $('.result-container').hide();
    });

	$('#second-file-area').keyup(function() {
	    $('#second-file-info').empty();
	    $('#second-file').val('');
	    $('.result-container').hide();

	});

    $('#first, #second').on({
        keyup: function() {
            $('.result-container').hide();
        },
        change: function() {
            $('.result-container').hide();
        }
    });
}