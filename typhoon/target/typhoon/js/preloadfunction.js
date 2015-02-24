selectYearChange = function() {
    var selectedYear = document.getElementById("selectedYear");
    var selectedYearIndex = selectedYear.selectedIndex;

    if (selectedYearIndex === 0) {
        //clear data show in the page.
        //var serialNumber = document.getElementById("serialNumber");
        var name_zh = document.getElementById("name_zh");
        var name_en = document.getElementById("name_en");

        //serialNumber.innerText = "";
        name_zh.innerText = "";
        name_en.innerText = "";

        $('#selectedSN').empty();
        $('#scrolltable').empty();
    } else {
        selectedYearIndex = selectedYearIndex - 1;

        var obj = dataList[selectedYearIndex].data;
        var option = "<option></option>";

        $(obj).each(function(index) {
            var pointData = obj[index];
            option = option + "<option>" + pointData.serialNumber + "</option>";
        });

        $('#selectedSN').empty();
        $('#selectedSN').append($(option));
    }
}
selectSNChange = function() {
    var selectedYear = document.getElementById("selectedYear");
    var selectedSN = document.getElementById("selectedSN");
    var selectedYearIndex = selectedYear.selectedIndex;
    var selectedSNIndex = selectedSN.selectedIndex;

    //var serialNumber = document.getElementById("serialNumber");
    var name_zh = document.getElementById("name_zh");
    var name_en = document.getElementById("name_en");

    if (selectedYearIndex === 0) {
        //clear data        
    } else {

        if (selectedSNIndex === 0) {
            //serialNumber.innerText = "";
            name_zh.innerText = "";
            name_en.innerText = "";

            $('#scrolltable').empty();
        } else {
            selectedYearIndex = selectedYearIndex - 1;
            selectedSNIndex = selectedSNIndex - 1;

            var yearObj = dataList[selectedYearIndex].data;
            var snObject = yearObj[selectedSNIndex];

            //serialNumber.innerText = snObject.serialNumber;
            name_zh.innerText = snObject.name_zh;
            name_en.innerText = snObject.name_en;

            updateScrollTableData(snObject.serialNumber);
        }
    }
}

updateScrollTableData = function(serialNumber) {

    $.ajax({
        url: "http://localhost:8080/typhoon/dataroute",
        async: false,
        contentType: "application/json",
        dataType: "json",
        type: "POST",
        data: JSON.stringify({
            "serialNumber": serialNumber
        }),
        success: function(data) {
            $('#scrolltable').empty();

            var content = "<tr><th style='width: 134px;'>时间</th>" + "<th style='width: 49px;'>经度</th><th >纬度</th>" + "</tr><tr></tr>";
            var trcontent = "";
            $(data).each(function(index) {
                var pointObj = data[index];
                content = content + "<tr>" + "<td>" + pointObj.time + "</td>" + "<td>" + pointObj.lng + "</td>" + "<td>" + pointObj.lat + "</td>" + "</tr>";
            });
            content = content + "";
            $('#scrolltable').append($(content));
            $('#fixedheight .bootstrap-table .fixed-table-container .fixed-table-header').css("height","0px");
        },
        error: function(data) {
            console.log("something wrong");
        }
    });
}
showPath = function() {
    var selectedYear = document.getElementById("selectedYear");
    var selectedSN = document.getElementById("selectedSN");
    var selectedYearIndex = selectedYear.selectedIndex;
    var selectedSNIndex = selectedSN.selectedIndex;

    if (selectedYearIndex != 0 && selectedSNIndex != 0) {
        var serialNumber = selectedSN.options[selectedSNIndex].value;
        //console.log(serialNumber);
        $.ajax({
            url: "http://localhost:8080/typhoon/routedatainformation",
            async: false,
            contentType: "application/json",
            dataType: "json",
            type: "POST",
            data: JSON.stringify({
                "serialNumber": serialNumber
            }),
            success: function(data) {
                //console.log(data);
                addPath(data);
            },
            error: function(data) {
                console.log("something wrong");
            }
        });
    }
}

showSimilarPath = function(){
     var selectedYear = document.getElementById("selectedYear");
    var selectedSN = document.getElementById("selectedSN");
    var selectedYearIndex = selectedYear.selectedIndex;
    var selectedSNIndex = selectedSN.selectedIndex;

    if (selectedYearIndex != 0 && selectedSNIndex != 0) {
         var serialNumber = selectedSN.options[selectedSNIndex].value;
        //console.log(serialNumber);
        $.ajax({
            url: "http://localhost:8080/typhoon/similarroute",
            async: false,
            contentType: "application/json",
            dataType: "json",
            type: "POST",
            data: JSON.stringify({
                "serialNumber": serialNumber
            }),
            success: function(data) {
                //console.log(data);
                addPath(data);
            },
            error: function(data) {
                console.log("something wrong");
            }
        });
    }
}
