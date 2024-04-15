/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
class Sigbla {
    #targetParent;
    #haveInit;

    #target;
    #corner;
    #topBanner;
    #leftBanner;
    #end;
    #marker;
    #markerCell;
    #overlay;
    #ovl;
    #ohl;

    #socket;

    #lastTile = [-1, -1, -1, -1];

    #pendingContent = null;
    #pendingContentTopics = [];
    #pendingUpdate = false;
    #pendingScrolls = [];
    #pendingResize = [];

    #pendingMessages = [];
    #pendingMessageBlockers = 0;

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
        this.#pendingContentTopics = [];
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

    #clear = async () => {
        // type 0 is clear event
        const clearEvent = {type: 0}

        if (this.#pendingUpdate) {
            this.#pendingScrolls.push(clearEvent);
        } else {
            this.#pendingUpdate = true;
            this.#socket.send(JSON.stringify(clearEvent));
        }
    }

    #socketOpen = async (_) => {
        window.addEventListener("scroll", this.#scroll);
        window.addEventListener("resize", this.#scroll);
    };

    #dynamicallyLoadStyle = (url) => {
        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
        let fullURL = new URL(pathname + url, window.location.href).href;

        let existingUrls = document.head.querySelectorAll("link[href]");
        for (let i = 0; i < existingUrls.length; i++) {
            if (existingUrls[i].href === fullURL) {
                return false;
            }
        }

        let link = document.createElement("link");
        link.rel = "stylesheet";
        link.type = "text/css";
        link.href = fullURL;
        document.head.appendChild(link);

        return true;
    };

    #dynamicallyLoadScript = (url) => {
        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/";
        let fullURL = new URL(pathname + url, window.location.href).href;

        let existingUrls = document.head.querySelectorAll("script[src]");
        for (let i = 0; i < existingUrls.length; i++) {
            if (existingUrls[i].src === fullURL) {
                return false;
            }
        }

        this.#pendingMessageBlockers++;
        let script = document.createElement("script");
        script.src = fullURL;
        script.async = false;
        script.defer = false;
        script.onload = async () => {
            this.#pendingMessageBlockers--;
            if (this.#pendingMessageBlockers <= 0) {
                const pendingMessages = this.#pendingMessages;
                this.#pendingMessages = [];
                this.#pendingMessageBlockers = 0;
                pendingMessages.forEach(await this.#handleMessage)
            }
        }
        document.head.appendChild(script);

        return true;
    };

    #manageMarkerCell = (newCell) => {
        if (this.#markerCell === newCell) return;

        if (this.#markerCell) {
            const co = this.#markerCell.querySelector(".co");
            if (co) {
                co.style.visibility = "visible";
            }
            this.#markerCell.classList.remove("dblclicked");
        }
        const mkr = document.getElementById("mkr");
        if (mkr) {
            mkr.classList.remove("dblclicked");
        }
        const innerMkr = document.getElementById("mkrInner");
        if (innerMkr) {
            innerMkr.classList.remove("dblclicked");
        }

        if (this.#markerCell) {
            // Send on blur
            this.#markerCell.dispatchEvent(new FocusEvent("blur"));
        }

        this.#markerCell = newCell;

        if (this.#markerCell) {
            // Send on focus
            this.#markerCell.dispatchEvent(new FocusEvent("focus"));
        }
    }

    // Note: x and y are viewport x and y, not element left/top
    #manageMarkerPosition = (x, y) => {
        const findCell = (left, top) => {
            if (isNaN(left) || isNaN(top)) return undefined;

            const elements = document.elementsFromPoint(left - window.scrollX, top - window.scrollY);

            for (const element of elements) {
                if (element.classList.contains("co") && element.parentElement.classList.contains("c")) {
                    return element.parentElement;
                }
            }
            return undefined;
        }

        const findNextLeft = (scroll) => {
            const tc = document.getElementById("tc");
            const tcWidth = parseInt(tc.style.width);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top);
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            for (let left = markerLeft - 5; left > markerLeft - 1000; left -= 5) {
                for (let top = markerTop + 5; top < markerTop + markerHeight; top += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellLeft) - tcWidth < window.scrollX + 10) {
                                window.scroll({
                                    left: parseInt(cellLeft) - tcWidth - 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cell.style.height;
                            this.#marker.style.width = cell.style.width;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerWidth / 2 < window.innerWidth ? markerWidth / 2 : window.innerWidth;
                        const cappedLength = length > window.innerWidth - tcWidth ? length - tcWidth : length

                        window.scrollBy({
                            left: cappedLength > 0 ? -cappedLength : -10,
                            behavior: "smooth"
                        });

                        findNextLeft(false);

                        return;
                    }
                }
            }
        }

        const findNextRight = (scroll) => {
            const tc = document.getElementById("tc");
            const tcWidth = parseInt(tc.style.width);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top);
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            for (let left = markerLeft + markerWidth + 5; left < markerLeft + markerWidth + 1000; left += 5) {
                for (let top = markerTop + 5; top < markerTop + markerHeight; top += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;
                        const cellWidth = cell.style.width;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellLeft) + parseInt(cellWidth) + 10 > window.scrollX + window.innerWidth) {
                                const length = markerWidth * 2 < window.innerWidth ? markerWidth * 2 : window.innerWidth;
                                const cappedLength = length > window.innerWidth - tcWidth ? length - tcWidth : length

                                window.scrollBy({
                                    left: cappedLength > 0 ? cappedLength : 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cell.style.height;
                            this.#marker.style.width = cellWidth;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerWidth / 2 < window.innerWidth ? markerWidth / 2 : window.innerWidth;
                        const cappedLength = length > window.innerWidth - tcWidth - 10 ? length - tcWidth - 10 : length

                        window.scrollBy({
                            left: cappedLength > 0 ? cappedLength : 10,
                            behavior: "smooth"
                        });

                        findNextRight(false);

                        return;
                    }
                }
            }
        }

        const findNextUp = (scroll) => {
            const tc = document.getElementById("tc");
            const tcHeight = parseInt(tc.style.height);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top);
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            for (let top = markerTop - 5; top > markerTop - 1000; top -= 5) {
                for (let left = markerLeft + 5; left < markerLeft + markerWidth; left += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellTop) - tcHeight < window.scrollY + 10) {
                                window.scroll({
                                    top: parseInt(cellTop) - tcHeight - 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cell.style.height;
                            this.#marker.style.width = cell.style.width;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerHeight / 2 < window.innerHeight ? markerHeight / 2 : window.innerHeight;
                        const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                        window.scrollBy({
                            top: cappedLength > 0 ? -cappedLength : -10,
                            behavior: "smooth"
                        });

                        findNextUp(false);

                        return;
                    }
                }
            }
        }

        const findNextDown = (scroll) => {
            const tc = document.getElementById("tc");
            const tcHeight = parseInt(tc.style.height);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top);
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            for (let top = markerTop + markerHeight + 5; top < markerTop + markerHeight + 1000; top += 5) {
                for (let left = markerLeft + 5; left < markerLeft + markerWidth; left += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;
                        const cellHeight = cell.style.height;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellTop) + parseInt(cellHeight) > window.scrollY + window.innerHeight - 10) {
                                const length = markerHeight * 2 < window.innerHeight ? markerHeight * 2 : window.innerHeight;
                                const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                                window.scrollBy({
                                    top: cappedLength > 0 ? cappedLength : 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cellHeight;
                            this.#marker.style.width = cell.style.width;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerHeight / 2 < window.innerHeight ? markerHeight / 2 : window.innerHeight;
                        const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                        window.scrollBy({
                            top: cappedLength > 0 ? cappedLength : 10,
                            behavior: "smooth"
                        });

                        findNextDown(false);

                        return;
                    }
                }
            }
        }

        const findPageUp = (scroll) => {
            const tc = document.getElementById("tc");
            const tcHeight = parseInt(tc.style.height);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top) - window.innerHeight + tcHeight;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            window.scrollBy({
                top: -window.innerHeight + tcHeight,
            });

            for (let top = markerTop - 5; top > markerTop - 1000; top -= 5) {
                for (let left = markerLeft + 5; left < markerLeft + markerWidth; left += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellTop) - tcHeight < window.scrollY + 10) {
                                window.scroll({
                                    top: parseInt(cellTop) - tcHeight - 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cell.style.height;
                            this.#marker.style.width = cell.style.width;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerHeight / 2 < window.innerHeight ? markerHeight / 2 : window.innerHeight;
                        const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                        window.scrollBy({
                            top: cappedLength > 0 ? -cappedLength : -10,
                            behavior: "smooth"
                        });

                        findNextUp(false);

                        return;
                    }
                }
            }
        }

        const findPageDown = (scroll) => {
            const tc = document.getElementById("tc");
            const tcHeight = parseInt(tc.style.height);
            const markerLeft = parseInt(this.#marker.style.left);
            const markerTop = parseInt(this.#marker.style.top) + window.innerHeight - tcHeight;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            window.scrollBy({
                top: window.innerHeight - tcHeight,
            });

            for (let top = markerTop + 5; top < markerTop + 1000; top += 5) {
                for (let left = markerLeft + 5; left < markerLeft + markerWidth; left += 5) {
                    const cell = findCell(left, top);

                    if (cell) {
                        const cellLeft = cell.style.left;
                        const cellTop = cell.style.top;
                        const cellHeight = cell.style.height;

                        if (cellLeft && cellTop) {
                            if (parseInt(cellTop) + parseInt(cellHeight) > window.scrollY + window.innerHeight - 10) {
                                const length = markerHeight * 2 < window.innerHeight ? markerHeight * 2 : window.innerHeight;
                                const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                                window.scrollBy({
                                    top: cappedLength > 0 ? cappedLength : 10,
                                    behavior: "smooth"
                                });
                            }

                            this.#marker.style.left = cellLeft;
                            this.#marker.style.top = cellTop;
                            this.#marker.style.height = cellHeight;
                            this.#marker.style.width = cell.style.width;

                            this.#manageMarkerCell(cell);

                            return;
                        }
                    } else if (scroll) {
                        const length = markerHeight / 2 < window.innerHeight ? markerHeight / 2 : window.innerHeight;
                        const cappedLength = length > window.innerHeight - tcHeight ? length - tcHeight : length

                        window.scrollBy({
                            top: cappedLength > 0 ? cappedLength : 10,
                            behavior: "smooth"
                        });

                        findNextDown(false);

                        return;
                    }
                }
            }
        }

        const marker = this.#marker || (() => {
            const newMarker = document.createElement("div");
            newMarker.id = "mkr";
            newMarker.tabIndex = -1;
            newMarker.style.top = "0";
            newMarker.style.left = "0";
            newMarker.style.height = "0px";
            newMarker.style.width = "0px";

            const innerMarker = document.createElement("div");
            innerMarker.id = "mkrInner";
            newMarker.appendChild(innerMarker);

            this.#target.appendChild(newMarker);
            this.#marker = newMarker;

            newMarker.addEventListener("transitionend", (e) => {
                newMarker.focus({
                    preventScroll: true
                });
            });

            const fireEvent = (e) => {
                if (this.#markerCell) {
                    const event = new KeyboardEvent("keydown", e);
                    this.#markerCell.dispatchEvent(event);
                }
            }

            // TODO Add Tab to create marker if not present (put marker on previous location if available)
            // TODO Add home / end support
            newMarker.addEventListener("keydown", (e) => {
                e.preventDefault();
                fireEvent(e);

                switch (e.key) {
                    case "Escape":
                        newMarker.remove();
                        this.#marker = null;
                        this.#manageMarkerCell(null);
                        break;
                    case "Enter":
                        findNextDown(true);
                        break;
                    case "Tab":
                        findNextRight(true);
                        break;
                    case "PageUp":
                        findPageUp(true);
                        break;
                    case "PageDown":
                        findPageDown(true);
                        break;
                    case "ArrowLeft":
                        findNextLeft(true);
                        break;
                    case "ArrowRight":
                        findNextRight(true);
                        break;
                    case "ArrowUp":
                        findNextUp(true);
                        break;
                    case "ArrowDown":
                        findNextDown(true);
                        break;
                }
            });

            newMarker.addEventListener("dblclick", (e) => {
                if (this.#markerCell) {
                    const co = this.#markerCell.querySelector(".co");
                    if (co) {
                        co.style.visibility = "hidden";
                    }
                    this.#markerCell.classList.add("dblclicked");
                    newMarker.classList.add("dblclicked");
                    innerMarker.classList.add("dblclicked");

                    // Send dblclick to c
                    this.#markerCell.dispatchEvent(new MouseEvent("dblclick", e));
                }
            });

            return newMarker;
        })();

        const elements = document.elementsFromPoint(x, y)
        for (const element of elements) {
            if (element.classList.contains("co") && element.parentElement.classList.contains("c")) {
                marker.style.top = element.parentElement.style.top;
                marker.style.left = element.parentElement.style.left;
                marker.style.height = element.parentElement.style.height;
                marker.style.width = element.parentElement.style.width;

                this.#manageMarkerCell(element.parentElement);

                return;
            }
        }
    }

    #handleMessage = async (message) => {
        if (this.#pendingMessageBlockers > 0) {
            this.#pendingMessages.push(message);
            return;
        }

        switch (message.type) {
            case 0: { // clear
                this.#dispatchClear({
                    action: "clear",
                    target: this.#target,
                    message: message
                });

                this.#target = document.createElement("div");
                this.#corner = null;
                this.#topBanner = null;
                this.#leftBanner = null;
                this.#end = null;
                this.#marker = null;
                this.#manageMarkerCell(null);

                this.#lastTile = [-1, -1, -1, -1];
                this.#swapBuffer = true;
                this.#pendingScrolls.length = 0;
                this.#pendingResize.length = 0;
                this.#pendingUpdate = false;
                this.#pendingContent = null;
                this.#pendingContentTopics = [];

                break;
            }
            case 1: { // scroll..
                break;
            }
            case 2: { // add content
                const div = document.createElement("div");

                // TODO Change structure of this so id is attached to ch, rh, or c, never container
                div.id = message.id;

                if (message.classes === "ch" || message.classes === "rh" || message.classes.startsWith("ch ") || message.classes.startsWith("rh ")) {
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
                    // TODO Remove math
                    child.style.height = (message.h + 0) + "px";
                    child.style.width = (message.w + 0) + "px";

                    const cc = document.createElement("div");
                    cc.className = "cc";
                    cc.innerText = message.content;

                    const co = document.createElement("div");
                    co.className = "co";

                    div.appendChild(co);

                    const bb = document.createElement("div");
                    bb.className = "bb";
                    bb.onmousedown = this.#bbMousedown;

                    const rb = document.createElement("div");
                    rb.className = "rb";
                    rb.onmousedown = this.#rbMousedown;

                    child.appendChild(cc);
                    child.appendChild(co);
                    child.appendChild(bb);
                    child.appendChild(rb);

                    div.appendChild(child);
                } else {
                    const cc = document.createElement("div");
                    cc.className = message.classes;
                    div.className = "c";

                    //if (message.z !== undefined) div.style.zIndex = message.z
                    if (message.x !== undefined) div.style.left = message.x + "px";
                    if (message.y !== undefined) div.style.top = message.y + "px";
                    if (message.mt !== undefined) div.style.marginTop = message.mt + "px";
                    if (message.ml !== undefined) div.style.marginLeft = message.ml + "px";
                    // TODO Remove math
                    div.style.height = (message.h + 0) + "px";
                    div.style.width = (message.w + 0) + "px";

                    if (message.classes.startsWith("hc "))
                        cc.innerHTML = message.content;
                    else
                        cc.innerText = message.content;

                    div.appendChild(cc)

                    const co = document.createElement("div");
                    co.className = "co";

                    div.appendChild(co);

                    co.addEventListener("click", (e) => {
                        this.#manageMarkerPosition(e.clientX, e.clientY);
                    });
                }

                const old = document.getElementById(message.id);

                if (old) {
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removing",
                        target: old,
                        message: message
                    }));
                    old.remove();
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removed",
                        target: old,
                        message: message
                    }));

                    if (message.content !== null) this.#target.appendChild(div);
                } else if (message.content !== null) {
                    if (this.#pendingContent === null) {
                        this.#pendingContent = document.createDocumentFragment();
                        this.#pendingContentTopics = [];
                    }

                    this.#pendingContent.appendChild(div);
                }

                (message.topics || []).forEach(topic => this.#dispatchTopic({
                    topic: topic,
                    action: "preparing",
                    target: div,
                    message: message
                }));

                this.#pendingContentTopics.push([div, message.topics || []]);

                break;
            }
            case 3: { // add commit
                if (this.#pendingContent !== null) {
                    this.#target.appendChild(this.#pendingContent);
                    this.#pendingContent = null;
                }

                this.#pendingContentTopics.forEach(data => {
                    data[1].forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "attached",
                        target: data[0],
                        message: message
                    }));
                });
                this.#pendingContentTopics = [];

                break;
            }
            case 4: { // remove content
                const item = document.getElementById(message.id);
                if (item) {
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removing",
                        target: item,
                        message: message
                    }));
                    item.remove();
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removed",
                        target: item,
                        message: message
                    }));
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

                if (this.#markerCell && document.getElementById(this.#markerCell.id)) {
                    this.#manageMarkerCell(document.getElementById(this.#markerCell.id));
                }

                break;
            }
            case 7: { // dims
                const corner = this.#corner || (() => {
                    const newCorner = document.createElement("div");
                    newCorner.id = "tc";
                    this.#target.appendChild(newCorner);
                    this.#corner = newCorner;
                    return newCorner;
                })();
                const topBanner = this.#topBanner || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "tb";
                    this.#target.appendChild(newBanner);
                    this.#topBanner = newBanner;
                    return newBanner;
                })();
                const leftBanner = this.#leftBanner || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "lb";
                    this.#target.appendChild(newBanner);
                    this.#leftBanner = newBanner;
                    return newBanner;
                })();

                corner.style.height = message.cornerY + "px";
                corner.style.width = message.cornerX + "px";

                if (message.cornerRightMargin >= 0) {
                    corner.style.borderRightWidth = message.cornerRightMargin + "px";
                }
                if (message.cornerBottomMargin >= 0) {
                    corner.style.borderBottomWidth = message.cornerBottomMargin + "px";
                }

                topBanner.style.height = (message.cornerY + message.cornerBottomMargin) + "px";
                topBanner.style.width = message.maxX + "px";

                leftBanner.style.height = message.maxY + "px";
                leftBanner.style.width = (message.cornerX + message.cornerRightMargin) + "px";

                this.#target.style.height = message.maxY + "px";
                this.#target.style.width = message.maxX + "px";

                const end = this.#end || (() => {
                    const newEnd = document.createElement("div");
                    newEnd.id = "end";
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
                const newJS = (message.urls || []).map(this.#dynamicallyLoadScript).some(n => n);
                if (newJS && message.dirty) {
                    // Because this is unseen JS code, request a clear to redo all rendering
                    await this.#clear();
                }

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
