var settings = {
    async: true,
    crossDomain: true,
    dataType: "json",

}
var hostURL = "http://192.168.86.35:8000"

function request(endpoint, method, body, response, errorCallback) {
    var set = settings
    set.url = hostURL + endpoint
    set.type = method
    set.data = body
    if(errorCallback === undefined){
        //do nothing
    } else if(errorCallback === null) {
        set.error = e => { console.log(e) }
    } else {
        set.error = errorCallback
    }
    $.ajax(set).done(response)
}

function mapRanges(num, in_min, in_max, out_min, out_max) {
    return (num - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}