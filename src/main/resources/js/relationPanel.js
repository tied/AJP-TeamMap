AJS.$(document).ready(function(){
	AJS.$(document).on("click","#report-a-relationship",function(){
	    AJS.dialog2("#report-a-relationship-dialog").show();
	});
	AJS.$(document).on("click","#dialog-close-button",function(e){
	    e.preventDefault();
	    AJS.dialog2("#report-a-relationship-dialog").hide();
	});
	AJS.$(document).on("click","span.DeleteLink",function(e){
	    var relationship=AJS.$(this).parents("div.aRelation");
	    var url=AJS.$(this).attr("action");
	    var id=relationship.attr("id");
	    AJS.$.ajax({
		    url: url+'?id='+id,
		    type: 'DELETE',
		    success: function(status) {
		    	if(status=="success"){
			    	relationship.slideUp(100);
			    	relationship.fadeOut(110);
		    	}
		    	else if (status=="error")
		    		throwError("Error in removing!","<p>Something is Wrong...</p>");
		    	else
		    		console.log(status);
		    },
		    error: function(status){
		    	throwError("Error in removing!","<p>Something is Wrong...</p>");	
		    }
		});
	});
	AJS.$(document).on("click","#dialog-submit-button",function(e){
	    e.preventDefault();
	    var projectKey=AJS.$("input#project-key").val();
		var IssueId=AJS.$("input#issue-id").val();
		var firstUser=AJS.$("select#first-user-select").val();
		var secondUser=AJS.$("select#second-user-select").val();
		var relationId=AJS.$("select#relation-select").val();
		AJS.$.post(AJS.$("form#submit-relationship").attr("action"),
		        {
					projectKey: projectKey,
					IssueId: IssueId,
					firstUser: firstUser,
					secondUser: secondUser,
					relationId: relationId
		        },
		        function(status){
		            if(status.match("^<div")){
		            	AJS.$('form#relation-form').trigger("reset");
		            	throwSuccess("Relationship Added","<p>New Relationship Has Been Recorded!</p>");
		            	AJS.$("#relationships-box").prepend(status);
		            }
		            else if(status=="error") 
		            	throwError("Relationship Not Added!","<p>Something is Wrong...</p>");
	
		            else 
		            	console.log(status);
		            
		            
		});
	    AJS.dialog2("#report-a-relationship-dialog").hide();
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