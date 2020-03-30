let socket;
let lastTile = [-1, -1, -1, -1];
let pendingContent = null;
let pendingUpdate = false;
let pendingScrolls = [];

if (document.readyState !== "loading") {
    init();
} else {
    document.addEventListener("DOMContentLoaded", init);
}

async function init() {
    let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
    let url = new URL(pathname + "socket", window.location.href);
    url.protocol = url.protocol.replace('https', 'wss');
    url.protocol = url.protocol.replace('http', 'ws');

    socket = new WebSocket(url.href);
    socket.addEventListener("open", socketOpen);
    socket.addEventListener("message", socketMessage);
}

async function socketOpen(_) {
    scroll();
    window.addEventListener("scroll", scroll);
    window.addEventListener("resize", scroll);
}

async function scroll() {
    let inc = 100;

    let xoffset = window.pageXOffset;
    let yoffset = window.pageYOffset;

    let height = window.innerHeight - (window.innerHeight % inc) + inc;
    let width = window.innerWidth - (window.innerWidth % inc) + inc;

    let xtile = xoffset - (xoffset % inc);
    let ytile = yoffset - (yoffset % inc);

    document.getElementById("pos").innerText = xoffset + ", " + yoffset + " | " + xtile + ", " + ytile;

    if (lastTile[0] === xtile && lastTile[1] === ytile && lastTile[2] === height && lastTile[3] === width) return;

    lastTile = [xtile, ytile, height, width];

    let scrollEvent = {"type": "scroll", "x": xtile, "y": ytile, "h": height, "w": width};

    if (pendingUpdate) {
        pendingScrolls.push(scrollEvent);
    } else {
        pendingUpdate = true;
        socket.send(JSON.stringify(scrollEvent));
    }
}

async function socketMessage(e) {
    //document.getElementById("message").innerText = e.data;
    let messages = JSON.parse(e.data);
    if (!Array.isArray(messages)) messages = [messages];
    messages.forEach(await handleMessage);
}

async function handleMessage(message, index, messages) {
    switch (message.type) {
        case "add": {
            let div = document.createElement("div");
            div.id = message.id;
            div.style.position = "absolute";
            div.style.left = message.x + "px";
            div.style.top = message.y + "px";
            div.style.height = message.h + "px";
            div.style.width = message.w + "px";
            div.innerText = message.content;

            if (pendingContent === null) {
                pendingContent = document.createDocumentFragment();
            }

            pendingContent.appendChild(div);
            break;
        }
        case "add-commit": {
            if (pendingContent === null) break;
            document.body.appendChild(pendingContent);
            pendingContent = null;
            break;
        }
        case "rm": {
            document.getElementById(message.id).remove();
            break;
        }
        case "update-end": {
            if (pendingScrolls.length > 0) {
                let pendingScroll = pendingScrolls.pop();
                pendingScrolls.length = 0;
                socket.send(JSON.stringify(pendingScroll));
            } else {
                pendingUpdate = false;
            }
            break;
        }
    }
}