$(function() {
	
	// Send ajax get to find user
	$('#searchButton').bind('click', function() {
			// Send ajax get
			$.getJSON("webresources/identity/read?", {
				commoname : $('#commoname').val()
			}, function(jsonResponse) {
				var firtsName = jsonResponse.firstName;
				$("#firtsName").val(firtsName);
			});
	});
	
	// Send ajax post to synchronize token
	$('#synchButton').bind('click', function() {
			// Send ajax post 
			$.ajax({
	            type: 'POST',
	            url: 'webresources/otp/resynch',
	            data: {
	    			commoname : $('#commoname').val(),
	    			otppassword1 : $('#otppassword1').val(),
	    			otppassword2 : $('#otppassword2').val()
	    		}, 
				success: function(data){
					alert("Done.");
			    },
			    error:function (xhr, ajaxOptions, thrownError){
					alert(xhr.responseText);
                },
	            ifModified: true,
	            cache: true
	        });
	});

});
 