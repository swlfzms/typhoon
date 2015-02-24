/**
 * 
 */
var map = new BMap.Map("content", {minZoom: 5});
var point = new BMap.Point(113.41,29.58);
map.centerAndZoom(point,5);
map.enableScrollWheelZoom();