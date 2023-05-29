class Sigbla {
    #targetParent;
    #haveInit;

    #target;
    #corner;
    #end;
    #overlay;
    #ovl;
    #ohl;

    #socket;

    #lastTile = [-1, -1, -1, -1];

    #pendingContent = null;
    #pendingUpdate = false;
    #pendingScrolls = [];
    #pendingResize = [];

    #pendingMessages = [];
    #enablePendingMessages = false;

    #resizeTarget;
    #resizeStartX;
    #resizeStartY;
    #enableVerticalOverlay = false;
    #enableHorizontalOverlay = false;

    #swapBuffer = false;

    #clearListeners = new Map();
    #topicListeners = new Map();
    #listenerCount = 0;

    constructor(targetParent) {
        this.#targetParent = targetParent;
        this.#targetParent.innerHTML = "";

        this.#target = document.createElement("div");
    }

    #rbMousedown = (e) => {
        if (e.buttons === 1) {
            this.#resizeTarget = e.target.parentElement.parentElement.id;

            this.#enableVerticalOverlay = true;
            this.#enableHorizontalOverlay = false;

            this.#resizeStartX = e.clientX;
            this.#resizeStartY = e.clientY;

            this.#overlay.style.display = "block";
            this.#ohl.style.display = "none";
            this.#ovl.style.display = "block";
            this.#ovl.style.left = e.clientX + "px";
        }
    };

    #bbMousedown = (e) => {
        if (e.buttons === 1) {
            this.#resizeTarget = e.target.parentElement.parentElement.id;

            this.#enableVerticalOverlay = false;
            this.#enableHorizontalOverlay = true;

            this.#resizeStartX = e.clientX;
            this.#resizeStartY = e.clientY;

            this.#overlay.style.display = "block";
            this.#ovl.style.display = "none";
            this.#ohl.style.display = "block";
            this.#ohl.style.top = e.clientY + "px";
        }
    };

    #submitResize = (target, sizeChangeX, sizeChangeY) => {
        const resizeEvent = {type: 8, target: target, sizeChangeX: sizeChangeX, sizeChangeY: sizeChangeY};

        if (this.#pendingUpdate) {
            this.#pendingResize.push(resizeEvent);
        } else {
            this.#pendingUpdate = true;
            this.#socket.send(JSON.stringify(resizeEvent));
        }
    };

    #overlayMouseup = (e) => {
        if (this.#enableVerticalOverlay) {
            const resizeX = e.clientX - this.#resizeStartX;
            if (resizeX !== 0) {
                this.#submitResize(this.#resizeTarget, resizeX, 0);
            } else {
                this.#overlay.style.display = "none";
            }
        } else if (this.#enableHorizontalOverlay) {
            const resizeY = e.clientY - this.#resizeStartY;
            if (resizeY !== 0) {
                this.#submitResize(this.#resizeTarget, 0, resizeY);
            } else {
                this.#overlay.style.display = "none";
            }
        }

        this.#ovl.style.display = "none";
        this.#ohl.style.display = "none";

        this.#enableVerticalOverlay = false;
        this.#enableHorizontalOverlay = false;
    };

    #overlayMousemove = (e) => {
        if (this.#enableVerticalOverlay) this.#ovl.style.left = e.clientX + "px";
        if (this.#enableHorizontalOverlay) this.#ohl.style.top = e.clientY + "px";
    };

    #renderInit = () => {
        this.#target.innerHTML = "";

        this.#overlay = document.createElement("div");
        this.#overlay.className = "overlay";
        this.#overlay.onmouseup = this.#overlayMouseup;
        this.#overlay.onmousemove = this.#overlayMousemove;

        this.#ovl = document.createElement("div");
        this.#ovl.className = "ovl";

        this.#ohl = document.createElement("div");
        this.#ohl.className = "ohl";

        this.#overlay.appendChild(this.#ovl);
        this.#overlay.appendChild(this.#ohl);

        this.#targetParent.appendChild(this.#overlay);
    };

    #stateInit = () => {
        this.#lastTile = [-1, -1, -1, -1];
        this.#pendingContent = null;
        this.#pendingUpdate = false;
        this.#pendingScrolls = [];
        this.#pendingResize = [];
        this.#enableVerticalOverlay = false;
        this.#enableHorizontalOverlay = false;
        this.#swapBuffer = false;
    };

    #scroll = async () => {
        const inc = 100;

        const xoffset = window.pageXOffset;
        const yoffset = window.pageYOffset;

        const height = window.innerHeight - (window.innerHeight % inc) + inc;
        const width = window.innerWidth - (window.innerWidth % inc) + inc;

        const xtile = xoffset - (xoffset % inc);
        const ytile = yoffset - (yoffset % inc);

        if (this.#lastTile[0] === xtile && this.#lastTile[1] === ytile && this.#lastTile[2] === height && this.#lastTile[3] === width) return;

        this.#lastTile = [xtile, ytile, height, width];

        // type 1 is scroll event
        const scrollEvent = {type: 1, x: xtile, y: ytile, h: height, w: width};

        if (this.#pendingUpdate) {
            this.#pendingScrolls.push(scrollEvent);
        } else {
            this.#pendingUpdate = true;
            this.#socket.send(JSON.stringify(scrollEvent));
        }
    };

    #socketOpen = async (_) => {
        window.addEventListener("scroll", this.#scroll);
        window.addEventListener("resize", this.#scroll);
    };

    #dynamicallyLoadStyle = (url) => {
        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
        let fullURL = new URL(pathname + "resources/" + url, window.location.href).href;

        let existingUrls = document.head.querySelectorAll("link[href]");
        for (let i = 0; i < existingUrls.length; i++) {
            if (existingUrls[i].href === fullURL) {
                return;
            }
        }

        let link = document.createElement("link");
        link.rel = "stylesheet";
        link.type = "text/css";
        link.href = fullURL;
        document.head.appendChild(link);
    };

    #dynamicallyLoadScript = (url) => {
        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
        let fullURL = new URL(pathname + "resources/" + url, window.location.href).href;

        let existingUrls = document.head.querySelectorAll("script[src]");
        for (let i = 0; i < existingUrls.length; i++) {
            if (existingUrls[i].src === fullURL) {
                return;
            }
        }

        this.#enablePendingMessages = true;
        let script = document.createElement("script");
        script.src = fullURL;
        script.onload = async () => {
            this.#enablePendingMessages = false;
            const pendingMessages = this.#pendingMessages;
            this.#pendingMessages = []
            pendingMessages.forEach(await this.#handleMessage)
        }
        document.head.appendChild(script);
    };

    #handleMessage = async (message) => {
        if (this.#enablePendingMessages) {
            this.#pendingMessages.push(message);
            return;
        }

        switch (message.type) {
            case 0: { // clear
                this.#dispatchClear({
                    action: "clear",
                    target: this.#target
                });

                this.#target = document.createElement("div");
                this.#corner = null;
                this.#end = null;

                this.#lastTile = [-1, -1, -1, -1];
                this.#swapBuffer = true;
                this.#pendingScrolls.length = 0;
                this.#pendingResize.length = 0;
                this.#pendingUpdate = false;
                this.#pendingContent = null;

                break;
            }
            case 1: { // scroll..
                break;
            }
            case 2: { // add content
                const div = document.createElement("div");

                div.id = message.id;

                if (message.classes.startsWith("ch") || message.classes.startsWith("rh")) {
                    const child = document.createElement("div");
                    child.className = message.classes;
                    div.className = "container";
                    div.style.height = message.ch + "px";
                    div.style.width = message.cw + "px";

                    //if (message.z !== undefined) child.style.zIndex = message.z
                    if (message.x !== undefined) child.style.left = message.x + "px";
                    if (message.y !== undefined) child.style.top = message.y + "px";
                    if (message.mt !== undefined) child.style.marginTop = message.mt + "px";
                    if (message.ml !== undefined) child.style.marginLeft = message.ml + "px";
                    // TODO Maybe include a border width somewhere?
                    child.style.height = (message.h - 0) + "px";
                    child.style.width = (message.w - 0) + "px";
                    child.innerText = message.content;

                    const bb = document.createElement("div");
                    bb.className = "bb";
                    bb.onmousedown = this.#bbMousedown;

                    const rb = document.createElement("div");
                    rb.className = "rb";
                    rb.onmousedown = this.#rbMousedown;

                    child.appendChild(bb);
                    child.appendChild(rb);

                    div.appendChild(child);
                } else {
                    div.className = message.classes;

                    //if (message.z !== undefined) div.style.zIndex = message.z
                    if (message.x !== undefined) div.style.left = message.x + "px";
                    if (message.y !== undefined) div.style.top = message.y + "px";
                    if (message.mt !== undefined) div.style.marginTop = message.mt + "px";
                    if (message.ml !== undefined) div.style.marginLeft = message.ml + "px";
                    div.style.height = (message.h - 1) + "px";
                    div.style.width = (message.w - 1) + "px";

                    if (message.classes.startsWith("hc "))
                        div.innerHTML = message.content;
                    else
                        div.innerText = message.content;
                }

                const old = document.getElementById(message.id);

                if (old) {
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "hide",
                        target: old
                    }));
                    old.remove();

                    if (message.content !== null) this.#target.appendChild(div);
                } else if (message.content !== null) {
                    if (this.#pendingContent === null) {
                        this.#pendingContent = document.createDocumentFragment();
                    }

                    this.#pendingContent.appendChild(div);
                }

                (message.topics || []).forEach(topic => this.#dispatchTopic({
                    topic: topic,
                    action: "show",
                    target: div
                }));

                break;
            }
            case 3: { // add commit
                if (this.#pendingContent === null) break;
                this.#target.appendChild(this.#pendingContent);
                this.#pendingContent = null;

                break;
            }
            case 4: { // remove content
                const item = document.getElementById(message.id);
                if (item) {
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "hide",
                        target: item
                    }));
                    item.remove();
                }

                break;
            }
            case 5: { // update end
                if (this.#swapBuffer) {
                    this.#swapBuffer = false;

                    document.getElementById("target").remove();

                    this.#target.id = "target";
                    this.#targetParent.appendChild(this.#target);

                    this.#overlay.style.display = "none";
                }

                break;
            }
            case 6: { // package end
                this.#socket.send(JSON.stringify({type: 6, id: message.id}));

                let havePendingResize = false;
                let havePendingScroll = false;

                if (this.#pendingResize.length > 0) {
                    for (let i = 0; i < this.#pendingResize.length; i++) {
                        this.#socket.send(JSON.stringify(this.#pendingResize[i]));
                    }
                    this.#pendingResize.length = 0;
                    havePendingResize = true;
                }

                if (this.#pendingScrolls.length > 0) {
                    let pendingScroll = this.#pendingScrolls.pop();
                    this.#pendingScrolls.length = 0;
                    this.#socket.send(JSON.stringify(pendingScroll));
                    havePendingScroll = true;
                }

                this.#pendingUpdate = havePendingResize || havePendingScroll;

                break;
            }
            case 7: { // dims
                const corner = this.#corner || (() => {
                    const newCorner = document.createElement("div");
                    newCorner.className = "tc";
                    this.#target.appendChild(newCorner);
                    this.#corner = newCorner;
                    return newCorner;
                })();

                corner.style.height = (message.cornerY - 1) + "px";
                corner.style.width = (message.cornerX - 1) + "px";

                this.#target.style.height = message.maxY + "px";
                this.#target.style.width = message.maxX + "px";

                const end = this.#end || (() => {
                    const newEnd = document.createElement("div");
                    newEnd.style.position = "absolute";
                    newEnd.style.width = "1px";
                    newEnd.style.height = "1px";
                    newEnd.style.backgroundColor = "black";
                    this.#target.appendChild(newEnd);
                    this.#end = newEnd;
                    return newEnd;
                })();

                end.style.top = message.maxY + "px";
                end.style.left = message.maxX + "px";

                await this.#scroll();

                break;
            }
            case 8: { // resize
                break;
            }
            case 9: { // load css
                (message.urls || []).forEach(this.#dynamicallyLoadStyle);

                break;
            }
            case 10: { // load js
                (message.urls || []).forEach(this.#dynamicallyLoadScript);

                break;
            }
        }
    };

    #socketMessage = async (e) => {
        let messages = JSON.parse(e.data);
        if (!Array.isArray(messages)) await this.#handleMessage(messages);
        else messages.forEach(await this.#handleMessage);
    };

    init = () => {
        if (this.#haveInit) return false;
        this.#haveInit = true;

        this.#renderInit();
        this.#stateInit();

        this.#target.id = "target";
        this.#targetParent.appendChild(this.#target);

        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
        let url = new URL(pathname + "socket", window.location.href);
        url.protocol = url.protocol.replace('https', 'wss');
        url.protocol = url.protocol.replace('http', 'ws');

        this.#socket = new WebSocket(url.href);
        this.#socket.addEventListener("open", this.#socketOpen);
        this.#socket.addEventListener("message", this.#socketMessage);

        return true;
    };

    onTopic = (topic, listener) => {
        let listenerMap;
        if (this.#topicListeners.has(topic)) {
            listenerMap = this.#topicListeners.get(topic);
        } else {
            listenerMap = new Map()
            this.#topicListeners.set(topic, listenerMap);
        }

        const listenerIndex = this.#listenerCount++;
        listenerMap.set(listenerIndex, listener);

        return new ListenerRef(() => {
            return listenerMap.delete(listenerIndex);
        })
    }

    #dispatchTopic = (data) => {
        const itr = (this.#topicListeners.get(data.topic) || new Map()).values();
        for (const listener of itr) {
            listener(data);
        }
    }

    onClear = (listener) => {
        const listenerIndex = this.#listenerCount++;
        this.#clearListeners.set(listenerIndex, listener);

        return new ListenerRef(() => {
            return this.#clearListeners.delete(listenerIndex);
        })
    }

    #dispatchClear = (data) => {
        const itr = this.#clearListeners.values();
        for (const listener of itr) {
            listener(data);
        }
    }
}

class ListenerRef {
    unsubscribe = () => { return false };

    constructor(unsubscribe) {
        this.unsubscribe = unsubscribe;
    }
}
