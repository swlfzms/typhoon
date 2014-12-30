/**
 * 
 */
var map = new BMap.Map("container", {minZoom: 6});
var point = new BMap.Point(113.41,29.58);
map.centerAndZoom(point,6);
map.enableScrollWheelZoom();