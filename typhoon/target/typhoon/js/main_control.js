/**
 * 
 */
var optsNavi = {type: BMAP_NAVIGATION_CONTROL_LARGE}
map.addControl(new BMap.NavigationControl (optsNavi));

map.addControl(new BMap.OverviewMapControl());

var optsScale = {offset: new BMap.Size(50,50)}
map.addControl(new BMap.ScaleControl(optsScale));

map.setCurrentCity("武汉");
map.addControl(new BMap.MapTypeControl());