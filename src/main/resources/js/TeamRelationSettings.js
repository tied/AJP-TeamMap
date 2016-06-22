AJS.$(document).ready(function(){
	AJS.$("#add-relation").click(function(e){
		e.preventDefault();
		var relationTitle=AJS.$("input#relation-title").val();
		var relationDesc=AJS.$("input#relation-description").val();
		var relationPhrase=AJS.$("input#relation-phrase").val();
		var relationReversePhrase=AJS.$("input#relation-reverse-phrase").val();
		var relationColor=AJS.$("select#relation-color").val();
		if(relationTitle=="" || relationDesc=="" || relationPhrase=="" ||  relationReversePhrase=="" || relationColor=="") {
			throwError('Form Error!','<p>Please Fill Out Mandatory field Marked by *</p>');
			return;
		}
		AJS.$.post("",
		        {
					title: relationTitle,
					description: relationDesc,
					phrase: relationPhrase,
					reversePhrase: relationReversePhrase,
					color: relationColor
		        },
		        function(status){
		            if(status.match("^<tr")){
		            	AJS.$('form#relation-form').trigger("reset");
		            	throwSuccess("Relation Added","<p>New Relation Has Been Saved!</p>");
		            	AJS.$("#relation-table").fadeTo(100,1);
		            	AJS.$("#relation-table").append(status);
		            }
		            else if(status=="error")
		            	throwError('Relation Not Added!','<p>Something is Wrong!!!</p>');
		            else
		            	console.log(status);
		});
	});
	AJS.$(document).on("click",".del-relation",function(e){
		e.preventDefault();
		var That=AJS.$(this);
		var id=That.parents("tr").attr("id");
		AJS.$.ajax({
		    url: '?id='+id,
		    type: 'DELETE',
		    success: function(status) {
		    	if(status=="success"){
			    	That.parents("tr").fadeOut(100).remove();
			    	if(AJS.$("#relation-table tr").length<2)
			    		AJS.$("#relation-table").fadeTo(100,0);
		    	}
		    	else if(status=="error")
		    		throwError('Relation Not Removed!','<p>Something is Wrong!!!</p>');
		    	else
	            	console.log(status);
		    },
		    error: function(status){
		    	throwError('Relation Not Removed!','<p>Something is Wrong!!!</p>');  	
		    }
		});
	});
	AJS.$(document).on("click",".toggle-active",function(e){
		e.preventDefault();
		var That=AJS.$(this);
		var buttonText="On";
		var buttonOldText=That.html();
		if(buttonOldText=="On")
			buttonText="Off";
		That.spin();
		That.html("");
		var id=That.parents("tr").attr("id");	
		AJS.$.ajax({
		    url: '?id='+id+'&toggle=true',
		    type: 'PUT',
		    success: function(status) {
		    	if(status=="success"){
		    		if(buttonText=="On")
			    		That.addClass("aui-button-primary");
			    	else
			    		That.removeClass("aui-button-primary");		    	
			    	That.html(buttonText);
		    	}
		    	else if(status=="error")
		    		That.html(buttonOldText);
		    	else
	            	console.log(status);
		    },
		    error: function(status){
		    	throwError("Relation Activation Error!","<p>Something is Wrong!!!</p>");
		    	That.html(buttonOldText);
		    }
		});
	});
	
});
function throwError(title,body){
	require(['aui/flag'], function(flag) {
        var myFlag = flag({
        type: 'error',
        title: title,
        close: 'auto',
        persistent: false,
        body:   body
        });
    });
}
function throwSuccess(title,body){
	require(['aui/flag'], function(flag) {
        var myFlag = flag({
        type: 'success',
        title: title,
        close: 'auto',
        persistent: false,
        body:   body
        });
    });
}