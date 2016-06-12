AJS.$(document).ready(function(){
	AJS.$("#add-relation").click(function(e){
		e.preventDefault();
		var relationTitle=AJS.$("input#relation-title").val();
		var relationDesc=AJS.$("input#relation-description").val();
		var relationPhrase=AJS.$("input#relation-phrase").val();
		var relationReversePhrase=AJS.$("input#relation-reverse-phrase").val();
		var relationColor=AJS.$("select#relation-color").val();
		AJS.$.post("",
		        {
					title: relationTitle,
					description: relationDesc,
					phrase: relationPhrase,
					reversePhrase: relationReversePhrase,
					color: relationColor
		        },
		        function(status){
		            if(status.length>0){
		            	AJS.$('form#relation-form').trigger("reset");
		            	require(['aui/flag'], function(flag) {
		        	        var myFlag = flag({
		        	        type: 'success',
		        	        title: 'Relation Added',
		        	        close: 'auto',
		        	        persistent: false,
		        	        body:   '<p>New Relation Has Been Saved!</p>'
		        	        });
		        	    });
		            	AJS.$("#relation-table").append(status);
		            }
		            else {
		            	require(['aui/flag'], function(flag) {
		        	        var myFlag = flag({
		        	        type: 'error',
		        	        title: 'Relation Not Added!',
		        	        close: 'auto',
		        	        persistent: false,
		        	        body:   '<p>Something is Wrong!!!</p>'
		        	        });
		        	    });
		            }
		            
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
		    	That.parents("tr").fadeOut(100);
		    },
		    error: function(status){
		    	require(['aui/flag'], function(flag) {
        	        var myFlag = flag({
        	        type: 'error',
        	        title: 'Relation Not Removed!',
        	        close: 'auto',
        	        persistent: false,
        	        body:   '<p>Something is Wrong!!!</p>'
        	        });
        	    });		    	
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
		That.html('<i class="fa fa-circle-o-notch fa-spin fa-fw"></i>');
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
		    	else
		    		That.html(buttonOldText);
		    },
		    error: function(status){
		    	require(['aui/flag'], function(flag) {
        	        var myFlag = flag({
        	        type: 'error',
        	        title: 'Relation Not Removed!',
        	        close: 'auto',
        	        persistent: false,
        	        body:   '<p>Something is Wrong!!!</p>'
        	        });
        	    });	
		    	That.html(buttonOldText);
		    }
		});
	});
	
});