function takeCareOfValidators() {
    jQuery.validator.addMethod("clearIncorrect", function(value, element) {
        if (!$.isNumeric(value) || (value.slice(0,1) == 0)) {
            $(element).val('');
            return false;
        } else {
            return true;
        }
    }, "Column should be a number, greater than 0.");


    var protValidator = $("#first").validate( {
        rules: {
            "site-cov-cutoff": {
                clearIncorrect: true,
                digits: true
            },
            "number-of-replicates": {
                clearIncorrect: true,
                digits: true
            }
        }
    });

    $("#site-cov-cutoff").keyup(function() {
        $("#site-cov-cutoff").valid();
    });

    $("#number-of-replicates").keyup(function() {
        $("#number-of-replicates").valid();
    });

    $("#site-cov-cutoff").focusout(function() {
       typeof protValidator != 'undefined' ? protValidator.resetForm() : protValidator;
    });
    $("#number-of-replicates").focusout(function() {
       typeof protValidator != 'undefined' ? protValidator.resetForm() : protValidator;
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

    $('#third-file').change(function() {
	    $('#third-file-info').html(this.files[0].name);
	    $('#third-file-area').val('');
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

    $('#third-file-area').keyup(function() {
        $('#third-file-info').empty();
        $('#third-file').val('');
        $('.result-container').hide();

    });

    $('#first').on({
        keyup: function() {
            $('.result-container').hide();
        },
        change: function() {
            $('.result-container').hide();
        }
    });
}