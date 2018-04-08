var pingDis = document.getElementById('ping');
var droneXY = document.getElementById('drone-xy');
var droneZ = document.getElementById('drone-z');
var coordinateX = document.getElementById('cx');
var coordinateY = document.getElementById('cy');
var coordinateZ = document.getElementById('cz');
var coordinateA = document.getElementById('ca');
var connected = false;

setInterval(_ => {
    ping();
}, 1000 / 1);

setInterval(_ => {
    if (connected) updatePosition();
}, 1000 / 10);

function ping() {
    request('/ping', 'GET', { calledAt: new Date().getTime() }, res => {
        pingDis.innerText = `${new Date().getTime() - res.calledAt} ms`
        connected = true;
    }, error => {
        disconnect();
    })
}

function updatePosition() {
    request('/simulation-info', 'GET', undefined, res => {
        droneXY.style.visibility = 'visible';
        droneZ.style.visibility = 'visible';
        coordinateX.innerText = `${round(res.x, 1)} m`;
        coordinateY.innerText = `${round(res.y, 1)} m`;
        coordinateA.innerText = `${round(res.a, 1)} deg`;
        coordinateZ.innerText = `${round(res.z, 1)} m`;
        droneXY.style.left = `${mapRanges(res.x, -10, 10, 0, 100)}%`
        droneXY.style.bottom = `${mapRanges(res.y, -10, 10, 0, 100)}%`
        droneXY.style.transform = `translate(-50%, 50%) rotate(${res.a}deg)`
        droneZ.style.bottom = `${mapRanges(res.z, 0, 10, 0, 100)}%`
    }, error => {
        disconnect()
    })
}

function disconnect() {
    droneXY.style.visibility = 'hidden';
    droneZ.style.visibility = 'hidden';
    pingDis.innerText = "Not Connected"
    connected = false;
    coordinateX.innerText = "";
    coordinateY.innerText = "";
    coordinateZ.innerText = "";
    coordinateA.innerText = "";
}

function round(number, decimals) {
    let scalar = Math.pow(10, decimals);
    return Math.round(scalar * number) / scalar;
}