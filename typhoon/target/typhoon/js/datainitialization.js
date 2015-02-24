var dataList;
function init(){	
	$.ajax({
		url: "http://localhost:8080/typhoon/datainitialization",
		async: false,
		contentType: "application/json",
		dataType: "json",
		type: "POST",
		data: JSON.stringify({'num':'hell'}),
		success: function(data){
			dataList = data;			
			var tmp;
			var obj="<option></option>";
			$(dataList).each(function(index){
				tmp = dataList[index];
				obj=obj+"<option id='"+tmp.year+"'>"+tmp.year+"</option>";
			});				
			$('#selectedYear').empty();
  			$('#selectedYear').append($(obj));  						
			//alert(result);
		},
		error: function(data){
			console.log("something wrong");			
		}
	});	
}

init();
