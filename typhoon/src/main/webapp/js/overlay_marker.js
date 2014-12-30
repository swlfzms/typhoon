/**
 * 
 */
function getIcon(center_speed){
	if(center_speed >= 32.7){
		level = 0;
		console.log("red");
	}else if(24.5 <= center_speed && center_speed <= 32.6){
		level = 1;
		console.log("orange");
	}else if(17.2 <= center_speed && center_speed <= 24.4){
		level = 2;
		console.log("yellow");
	}else if(10.8 <= center_speed && center_speed <= 17.1){
		level = 3;
		console.log("blue");
	}else {
		level = 4;
		console.log("white");
	}
	
	var myIcon;
	switch(level){
	case 0:
		myIcon = new BMap.Icon("image/bullet_red.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	case 1:
		myIcon = new BMap.Icon("image/bullet_orange.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	case 2:
		myIcon = new BMap.Icon("image/bullet_yellow.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	case 3:
		myIcon = new BMap.Icon("image/bullet_blue.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	case 4:
		myIcon = new BMap.Icon("image/bullet_white.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	default:
		myIcon = new BMap.Icon("image/bullet_white.png", new BMap.Size(14,14), {offset: new BMap.Size(7,7)});
		break;
	}
	return myIcon;
}

function addMarker(obj){
	var myIcon = getIcon(obj.center_speed);
	
	var positionPoint = new BMap.Point(obj.lng, obj.lat);
	
	var marker = new BMap.Marker(positionPoint, {icon: myIcon});
	marker.disableDragging();
	
	var content = "中心位置：" + obj.lng + "E" + obj.lat + "N<br>"
				 +"最低气压：" + obj.pressure + "百帕<br>"
				 +"中心风速：" + obj.center_speed + "m/s<br>"
				 +"移动速度：" + obj.move_speed + "m/s<br>"
				 +"移动方向：" + obj.direction + "<br>"
				 +"七级风圈：" + obj.seven_solar_halo + "公里<br>"
				 +"十级风圈：" + obj.ten_solar_halo + "公里<br>";
	content = '<div style="margin:0;line-height:20px;padding:2px;>' + content + '</div>';
	
	var searchInfoWindow = null;
	searchInfoWindow = new BMapLib.SearchInfoWindow(map, content,{
		title : "相关信息",
		width : 200,
		height : 145,
		panel : "panel",
		enableAutoPan : true,
		searchTypes :[
		              BMAPLIB_TAB_SEARCH,
		              BMAPLIB_TAB_TO_HERE,
		              BMAPLIB_TAB_FROM_HERE
		              ]
	}) ;
	
	var radius = obj.seven_solar_halo * 1000;
	
	var color = getColor(radius);
	
	var circle = new BMap.Circle();
	circle.setCenter(positionPoint);
	circle.setRadius(radius);
	circle.setStrokeColor("#000000");
	circle.setStrokeWeight(1);
	circle.setStrokeOpacity(0.5);
	circle.setFillOpacity(0.3);
	circle.setFillColor(color);
	
	var opts = {position: positionPoint, offset: new BMap.Size(30,-30)};
	var label = new BMap.Label("欢迎使用百度地图，这是一个简单的文本标注",opts);
	label.setStyle({
		color : "red",
		fontSize : "20px",
		lineHeight : "20px",
		fontFamily :"微软雅黑"
	});
	
	function add(){
		map.addOverlay(circle);
		
		searchInfoWindow.open(marker);
	}
	
	function remove(){
		map.removeOverlay(circle);
		
		searchInfoWindow.close(marker);
	}
	
	marker.addEventListener("click", function(e){
		console.log(positionPoint.lng + "," + positionPoint.lat);
	});
	
	marker.addEventListener("mouseover", function(e){
		add();
	});
	
	marker.addEventListener("mouseout", function(e){
		setTimeout(function(){
			remove();
		},2000);
	});
	
	//add marker
	map.addOverlay(market);

	
}

function randomDraw(){
	var bounds = map.getBounds();
	var lngSpan = bounds.maxX - bounds.minX;
	var latSpan = bounds.maxY- bounds.minY;
	var wuhan_x = 114.329865;
	var wuhan_y = 30.762924;
	var taibei_x=121.541605;
	var taibei_y = 25.079514;
	
	var distance_y = taibei_y - wuhan_y;
	var distance_x = taibei_x - wuhan_x;
	var k = distance_y / distance_x;
	
	var positionfrom, pointto;
	var firstpoint = false;
	
	for (var i=0; i<= 10; i ++){
		var point = new BMap.Point(taibei_x - i*(distance_x/10), taibei_y - i*(distance_y/10));
		
		if(firstepoint){
			pointto = point;
			addLine(pointfron, pointto);
			pointfrom = pointto;
		}else{
			pointfrom = point;
			firstpoint = true;
		}
	}
}

function getData(){
	var result;
	
	$.ajax({
		url: "http://localhost:8080/testPost",
		async: false,
		type: "POST",
		data: {},
		success: function(data){
			result = data;
		}
	});
	return result;
}
 
function addPath(objson){
	var obj = JSON.parse(objson);
	var firstpoint = false;
	var pointfrom, pointto;
	
	var val;
	
	$(obj).each(function(index){
		val = obj[index];
		var point = new BMap.Point(val.lng, val.lat);
		
		addMarker(val);
		if(firstpoint){
			pointto = point;
			addLine(pointfrom,pointto);
			pointfrom = pointto;
		}else{
			pointfrom = point;
			firstpoint = true;
		}
	});
	
}

function getColor(radiusParam){
	var radius = parseInt(radiusParam / 10000);
	
	if(radius <= 10){
		return "white";
	}else if(10<radius && radius <=15){
		return "green";
	}else if(15<radius && radius <=20){
		return "blue";
	}else if(20<radius && radius <=25){
		return "yellow";
	}else if(25<radius && radius <=30){
		return "orange";
	}else{
		return "red";
	}
}

 function addLine(point1, point2){
	 var polyline = new BMap.Polyline([point1,point2], {strokeColor:"black", strokeWeight:1, strokeOpacity:0.5});
	 map.addOverlay(polyline);
 }

function clear(){
	map.clearOverlays();
}














