AJS.$(document).ready(function(){
	AJS.$(document).on("click","#report-a-relationship",function(){
	    AJS.dialog2("#report-a-relationship-dialog").show();
	});
	AJS.$(document).on("click","#dialog-close-button",function(e){
	    e.preventDefault();
	    var dialog=$(this).parents("section#report-a-relationship-dialog");
	    AJS.dialog2(dialog).hide();
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
		    		throwError("Error in removing!","<p>Something is Wrong...</p>","auto");
		    	else
		    		console.log(status);
		    },
		    error: function(status){
		    	throwError("Error in removing!","<p>Something is Wrong...</p>","auto");	
		    }
		});
	});
	AJS.$(document).on("click","#dialog-submit-button",function(e){
	    e.preventDefault();
	    var dialog=$(this).parents("section#report-a-relationship-dialog");
	    var form=dialog.find("form");
	    var projectKey=form.find("input#project-key").val();
		var IssueId=form.find("input#issue-id").val();
		var firstUser=form.find("select#first-user-select").val();
		var secondUser=form.find("select#second-user-select").val();
		var relationId=form.find("select#relation-select").val();
		AJS.$.post(form.attr("action"),
		        {
					projectKey: projectKey,
					IssueId: IssueId,
					firstUser: firstUser,
					secondUser: secondUser,
					relationId: relationId
		        },
		        function(status){
		            if(status.match("^<div")){
		            	form.trigger("reset");
		            	throwSuccess("Relationship Added","<p>New Relationship Has Been Recorded!</p>");
		            	AJS.$("#relationships-box").prepend(status);
		            }
		            else if(status=="error") 
		            	throwError("Relationship Not Added!","<p>Something is Wrong...</p>","auto");
		            else if(status=="licenseError") 
		            	throwError("License Error!","<p>Team Map plugin's License is either expired or invalid.</p>","manual");
		            else 
		            	console.log(status);

		});
		AJS.dialog2(dialog).hide();
	});
});
function throwError(title,body,close){
	require(['aui/flag'], function(flag) {
        var myFlag = flag({
        type: 'error',
        title: title,
        close: close,
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