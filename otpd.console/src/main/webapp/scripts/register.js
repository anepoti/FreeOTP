$(function() {

	$('#registerButton').bind('click', function() {
			// Send ajax post 
			dataString = $("#formElem").serialize();
			$.ajax( {
				type : 'POST',
				url : 'webresources/identity/create/wizard',
				data : dataString,
				success : function(data) {
					alert("Data sent succesfully..");
				},
				error : function(xhr, ajaxOptions, thrownError) {
					alert(xhr.responseText);
				},
				ifModified : true,
				cache : true
			});
		});

});