AJS.$(document).ready(function(){
	var header_height = 0;
	AJS.$('table.map-table th div').each(function() {
        if (AJS.$(this).outerWidth() > header_height) header_height = AJS.$(this).outerWidth();
    });
	AJS.$('table.map-table th').height(header_height);
    
	AJS.$(document).on("mouseover","td.relationship-cell div:last-child",function(){
		var That=AJS.$(this);
		if(That.html()!=""){
			var parentTdIndex=That.parent("td").index();
			var parentTr=That.parents("tr");
			parentTr.children("td:first").children("div").addClass("hovered");
			AJS.$("tr.table-user-head").children("th:eq("+parentTdIndex+")").children("div").addClass("hovered");
			
		}
	});
	AJS.$(document).on("mouseout","td.relationship-cell div:last-child",function(){
		AJS.$(".hovered").removeClass("hovered");
	});
	
	AJS.$(".relations-colors-bar div").tooltip();
	heightAdjuster();
	AJS.$(window).resize(function(){
		heightAdjuster();
	});
	AJS.$(document).on("click","ul.filter-list li",function(){
		var id=AJS.$(this).attr("id");
		window.location.href = "?filter="+id;
	})
	AJS.$(document).on("change","select#realtion-select",function(){
		var relationId=AJS.$(this).val();
		var filterId=AJS.$("ul.filter-list li.selected").attr("id");
		if(typeof filterId === 'undefined')
			window.location.href = "?jql-mode=true&relation="+relationId;
		else
			window.location.href = "?filter="+filterId+"&relation="+relationId;			
	})
	AJS.$("form#jql-form").on("submit",function(e){
		if(AJS.$("button#custom-jql-button").is(':disabled'))
			e.preventDefault();
	})
	AJS.$(document).on("click","button#custom-jql-button",function(){
		if(!AJS.$("input#custom-jql-input").is(':disabled')){
			AJS.$("form#jql-form").submit();
			return;
		}	
		AJS.$(this).children("span:last").html("Search");
		AJS.$("input#custom-jql-input").prop('disabled', false);
		AJS.$("ul.filter-list li.selected").removeClass("selected");
		verifyQuery();
	})
	AJS.$(document).on("keyup",function(){
		if(!AJS.$("input#custom-jql-input").is(":focus"))
			return;
		verifyQuery();		
	})
	AJS.$(document).on("focus","input#custom-jql-input",function(){
		verifyQuery();
	});
});
function heightAdjuster(){
	var top=AJS.$(".adjustable-heights").offset().top;
	var windowHeight=AJS.$(window).height();
	AJS.$(".adjustable-heights").height(windowHeight-top-1);
}
function verifyQuery(){
	var That=AJS.$("button#custom-jql-button");
	var query=AJS.$("input#custom-jql-input").val();
	var ok="aui-icon aui-icon-small aui-iconfont-approve";
	var error="aui-icon aui-icon-small aui-iconfont-error";
	AJS.$.post("",
	        {
				query: query
	        },
	        function(status){
	            if(status=="ok"){
	            	That.prop('disabled', false);
	            	That.removeClass("btn-error");
	            	That.addClass("btn-success");
	            	That.children("span:first-child").attr("class",ok);
	            }
	            else if(status=="error"){
	            	That.prop('disabled', true);
	            	That.removeClass("btn-success");
	            	That.addClass("btn-error");
	            	That.children("span:first-child").attr("class",error);
	            }
	            else
	            	console.log(status);
	});
}