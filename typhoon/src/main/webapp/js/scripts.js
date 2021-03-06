$(document).ready(function() {

    var evnts = function() {
        return {
            "event": [{
                "date": "01/01/2012",
                "title": "1"
            },
            {
                "date": "02/02/2012",
                "title": "2"
            },
            {
                "date": "03/03/2012",
                "title": "34"
            },
            {
                "date": "04/04/2012",
                "title": "123"
            },
            {
                "date": "05/05/2012",
                "title": "223"
            },
            {
                "date": "06/06/2012",
                "title": "4"
            },
            {
                "date": "07/07/2012",
                "title": "5"
            },
            {
                "date": "08/08/2012",
                "title": "14"
            },
            {
                "date": "09/09/2012",
                "title": "10"
            },
            {
                "date": "10/10/2012",
                "title": "10"
            },
            {
                "date": "11/11/2012",
                "title": "10"
            },
            {
                "date": "12/12/2012",
                "title": "10"
            }]
        }
    };

    $('#calendar').Calendar({
        'events': evnts,
        'weekStart': 7
    }).on('changeDay',
    function(event) {         
         alert(event.day.valueOf() + '-' + event.month.valueOf() + '-' + event.year.valueOf());
    }).on('onEvent',
    function(event) {
        alert(event.day.valueOf() + '-' + event.month.valueOf() + '-' + event.year.valueOf());
    }).on('onNext',
    function(event) {
        alert("Next");
    }).on('onPrev',
    function(event) {
        alert("Prev");
    }).on('onCurrent',
    function(event) {
        alert("Current");
    });

});

