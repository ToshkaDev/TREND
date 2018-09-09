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
	});

    $('#second-file').change(function() {
	    $('#second-file-info').html(this.files[0].name);
	    $('#second-file-area').val('');
	});

    $('#first-file-area').keyup(function() {
        $('#first-file-info').empty();
        $('#first-file').val('');
    });

	$('#second-file-area').keyup(function() {
	    $('#second-file-info').empty();
	    $('#second-file').val('');
	});

    $('#sequence-file').change(function() {
	    $('#sequence-file-info').html(this.files[0].name);
	});

    $('#alignment-file').change(function() {
        $('#alignment-file-info').html(this.files[0].name);
    });

    $('#tree-file').change(function() {
        $('#tree-file-info').html(this.files[0].name);
    });

    $('#gaps-missing').change(function() {
        var checkedOption = $('#gaps-missing').val();
        if (checkedOption === "compDel" || checkedOption === "pairDel") {
            $('.cov-cutoff').hide();
        } else if (checkedOption === "partDel") {
            $('.cov-cutoff').show();
        }
    });

    $('input[name="dom-prediction-program"]').change(function() {
        var checkedOption = $('input[name="dom-prediction-program"]:checked').val();
        if (checkedOption === "hmmscan") {
            $('#rpsblast-db').hide();
            $('#hmmer-db').show();
        } else if (checkedOption === "rpsblast") {
            $('#hmmer-db').hide();
            $('#rpsblast-db').show();
        }
    });


    treeMethodToPrefix = {"ML": "ml", "ME": "nj_me", "JN": "nj_me"};

    $('#tree-method').change(function() {
        var treeMethod = $('#tree-method').val();
        switch (treeMethod) {
            case "ML":
                $('.ml-options').show();
                $('.nj-and-me-options').hide();
                break;
            case "ME":
                $('.ml-options').hide();
                $('.nj-and-me-options').show();
                break;
            case "JN":
                $('.ml-options').hide();
                $('.nj-and-me-options').show();
                break;
        }
        var prefix = treeMethodToPrefix[treeMethod];
        var phylogenyTest = $("#" + prefix + "-phylo-test").val();
        phylogenyTest != "none" ? $(".number-of-replicates").show() : $(".number-of-replicates").hide();
    });

    $("#ml-phylo-test, #nj_me-phylo-test").change(function() {
        var treeMethod = $('#tree-method').val();
        var prefix = treeMethodToPrefix[treeMethod];
        var phylogenyTest = $("#" + prefix + "-phylo-test").val();
        phylogenyTest != "none" ? $(".number-of-replicates").show() : $(".number-of-replicates").hide();
    });


    $('#first').on({
        keyup: function() {
            $('.result-container').hide();
        },
        change: function() {
            $('.result-container').hide();
        }
    });

    lcrStates = {"checked": true, "undefined": false};
    $('#lc-value').click(function() {
        var lcValue = $('#lc-value').attr("checked");
        $('#lc-value').attr('checked', !lcrStates[lcValue]);
    })
}