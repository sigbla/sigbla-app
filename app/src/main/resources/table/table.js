/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
class Sigbla {
    #targetParent;
    #haveInit;

    #target;
    #corner;
    #topBanner;
    #topBannerLeft;
    #topBannerRight;
    #leftBanner;
    #leftBannerTop;
    #leftBannerBottom;
    #cellBannerLeft;
    #cellBannerRight;
    #cellBannerTop;
    #cellBannerBottom;
    #end;
    #marker;
    #markerCell;
    #overlay;
    #ovl;
    #ohl;

    #socket;

    #lastTile = [-1, -1, -1, -1];

    #pendingRemove = [];
    #pendingContent = null;
    #pendingContentTopics = [];
    #pendingUpdate = false;
    #pendingScrolls = [];
    #pendingResize = [];

    // TODO IDEA message?
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
            this.#resizeTarget = e.target.parentElement.id;

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
            this.#resizeTarget = e.target.parentElement.id;

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
        this.#pendingRemove = [];
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

        // TODO Delaying scroll events per inc is fine, but less fine on resize?
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

        const mkr = document.getElementById("mkr");

        if (this.#markerCell) {
            const co = this.#markerCell.querySelector(".co");
            if (co) {
                co.style.visibility = "visible";
            }
            this.#markerCell.classList.remove("dblclicked");
        }

        if (mkr && newCell) {
            const cellContainer = newCell.parentElement;
            const mkrContainer = mkr.parentElement;

            mkrContainer.style.height = cellContainer.style.height;
            mkrContainer.style.width = cellContainer.style.width;

            mkr.style.left = newCell.style.left;
            mkr.style.top = newCell.style.top;
            mkr.style.height = newCell.style.height;
            mkr.style.width = newCell.style.width;
            mkr.style.marginTop = newCell.style.marginTop;
            mkr.style.marginLeft = newCell.style.marginLeft;
            if (newCell.classList.contains("cl")
                || newCell.classList.contains("cr")
                || newCell.classList.contains("ct")
                || newCell.classList.contains("cb")
            ) {
                mkr.style.position = "sticky";
            } else {
                mkr.style.position = "absolute";
            }
            if (newCell.classList.contains("cl")) {
                mkr.classList.add("ml");
            } else {
                mkr.classList.remove("ml");
            }
            if (newCell.classList.contains("cr")) {
                mkr.classList.add("mr");
            } else {
                mkr.classList.remove("mr");
            }
            if (newCell.classList.contains("ct")) {
                mkr.classList.add("mt");
            } else {
                mkr.classList.remove("mt");
            }
            if (newCell.classList.contains("cb")) {
                mkr.classList.add("mb");
            } else {
                mkr.classList.remove("mb");
            }

            mkr.focus();
        }

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
        const findCell = (x, y) => {
            if (isNaN(x) || isNaN(y)) return undefined;
            const preferLocked = this.#markerCell && (
                this.#markerCell.classList.contains("cl")
                || this.#markerCell.classList.contains("cr")
                || this.#markerCell.classList.contains("ct")
                || this.#markerCell.classList.contains("cb")
            )

            const elements = document.elementsFromPoint(x, y);

            if (preferLocked) {
                // Look for locked c first
                for (const element of elements) {
                    if (element.classList.contains("co")
                        && element.parentElement.classList.contains("c")
                        && (
                            element.parentElement.classList.contains("cl")
                            || element.parentElement.classList.contains("cr")
                            || element.parentElement.classList.contains("ct")
                            || element.parentElement.classList.contains("cb")
                        )
                    ) {
                        return element.parentElement;
                    }
                }

                // Look for normal c next
                for (const element of elements) {
                    if (element.classList.contains("co")
                        && element.parentElement.classList.contains("c")
                        && !(
                            element.parentElement.classList.contains("cl")
                            || element.parentElement.classList.contains("cr")
                            || element.parentElement.classList.contains("ct")
                            || element.parentElement.classList.contains("cb")
                        )
                    ) {
                        return element.parentElement;
                    }
                }
            } else {
                // Look for normal c first
                for (const element of elements) {
                    if (element.classList.contains("co")
                        && element.parentElement.classList.contains("c")
                        && !(
                            element.parentElement.classList.contains("cl")
                            || element.parentElement.classList.contains("cr")
                            || element.parentElement.classList.contains("ct")
                            || element.parentElement.classList.contains("cb")
                        )
                    ) {
                        return element.parentElement;
                    }
                }

                // Look for locked c next
                for (const element of elements) {
                    if (element.classList.contains("co")
                        && element.parentElement.classList.contains("c")
                        && (
                            element.parentElement.classList.contains("cl")
                            || element.parentElement.classList.contains("cr")
                            || element.parentElement.classList.contains("ct")
                            || element.parentElement.classList.contains("cb")
                        )
                    ) {
                        return element.parentElement;
                    }
                }
            }

            return undefined;
        }

        const findNextLeft = () => {
            const lb = document.getElementById("lb");
            const cbl = document.getElementById("cbl");
            const lbWidth = parseInt(lb.style.width);
            const cblWidth = parseInt(cbl.style.width);
            const leftOffset = lbWidth > cblWidth ? lbWidth : cblWidth;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);

            const horizontalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("cl")
                || this.#markerCell.classList.contains("cr")
            )

            // Bring current marker more in view if needed
            if (!horizontalLockedCell && markerX < leftOffset) {
                window.scrollBy({
                    left: -100,
                    behavior: "smooth"
                });

                return;
            }

            for (let x = markerX - 5; x > markerX - 1000; x -= 5) {
                for (let y = markerY + 5; y < markerY + markerHeight; y += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const horizontalLockedCell = cell.classList.contains("cl") || cell.classList.contains("cr");

                        // Bring current marker more in view if needed
                        const markerX = this.#marker.getBoundingClientRect().x;
                        if (!horizontalLockedCell && markerX < leftOffset) {
                            window.scrollBy({
                                left: -100,
                                behavior: "smooth"
                            });
                        }

                        return;
                    }
                }
            }

            // No cell found, scroll a bit
            window.scrollBy({
                left: -100,
                behavior: "smooth"
            });
        }

        const findNextRight = () => {
            const cbr = document.getElementById("cbr");
            const rightOffset = cbr.getBoundingClientRect().x;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            const horizontalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("cl")
                || this.#markerCell.classList.contains("cr")
            )

            // Bring current marker more in view if needed
            if (!horizontalLockedCell && markerX + markerWidth > rightOffset) {
                window.scrollBy({
                    left: 100,
                    behavior: "smooth"
                });

                return;
            }

            for (let x = markerX + markerWidth + 5; x < markerX + markerWidth + 1000; x += 5) {
                for (let y = markerY + 5; y < markerY + markerHeight; y += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const horizontalLockedCell = cell.classList.contains("cl") || cell.classList.contains("cr");

                        // Bring current marker more in view if needed
                        const markerX = this.#marker.getBoundingClientRect().x;
                        if (!horizontalLockedCell && markerX > rightOffset) {
                            window.scrollBy({
                                left: 100,
                                behavior: "smooth"
                            });
                        }

                        return;
                    }
                }
            }

            // No cell found, scroll a bit
            window.scrollBy({
                left: 100,
                behavior: "smooth"
            });
        }

        const findNextTab = (retry) => {
            const cbr = document.getElementById("cbr");
            const rightOffset = cbr.getBoundingClientRect().x;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            const horizontalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("cl")
                || this.#markerCell.classList.contains("cr")
            )

            for (let x = markerX + markerWidth + 5; x < markerX + markerWidth + 1000; x += 5) {
                for (let y = markerY + 5; y < markerY + markerHeight; y += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const horizontalLockedCell = cell.classList.contains("cl") || cell.classList.contains("cr");

                        // Bring current marker more in view if needed
                        const markerX = this.#marker.getBoundingClientRect().x;
                        const markerWidth = parseInt(this.#marker.style.width);
                        if (!horizontalLockedCell && markerX + markerWidth > rightOffset) {
                            window.scrollBy({
                                left: markerX + markerWidth - rightOffset,
                                behavior: "instant"
                            });
                        }

                        return;
                    }
                }
            }

            if (!horizontalLockedCell && retry > 0) {
                // No cell found, scroll a bit
                window.scrollBy({
                    left: 100,
                    behavior: "instant"
                });

                findNextTab(retry - 1);
            }
        }

        const findNextUp = () => {
            const tb = document.getElementById("tb");
            const cbt = document.getElementById("cbt");
            const tbHeight = parseInt(tb.style.height);
            const cbtHeight = parseInt(cbt.style.height);
            const topOffset = tbHeight > cbtHeight ? tbHeight : cbtHeight;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerWidth = parseInt(this.#marker.style.width);

            const verticalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("ct")
                || this.#markerCell.classList.contains("cb")
            )

            // Bring current marker more in view if needed
            if (!verticalLockedCell && markerY < topOffset) {
                window.scrollBy({
                    top: -25,
                    behavior: "smooth"
                });

                return;
            }

            for (let y = markerY - 5; y > markerY - 1000; y -= 5) {
                for (let x = markerX + 5; x < markerX + markerWidth; x += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const verticalLockedCell = cell.classList.contains("ct") || cell.classList.contains("cb");

                        // Bring current marker more in view if needed
                        const markerY = this.#marker.getBoundingClientRect().y;
                        if (!verticalLockedCell && markerY < topOffset) {
                            window.scrollBy({
                                top: -25,
                                behavior: "smooth"
                            });
                        }

                        return;
                    }
                }
            }

            // No cell found, scroll a bit
            window.scrollBy({
                top: -25,
                behavior: "smooth"
            });
        }

        const findNextDown = () => {
            const cbb = document.getElementById("cbb");
            const bottomOffset = cbb.getBoundingClientRect().y;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            const verticalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("ct")
                || this.#markerCell.classList.contains("cb")
            )

            // Bring current marker more in view if needed
            if (!verticalLockedCell && markerY + markerHeight > bottomOffset) {
                window.scrollBy({
                    top: 25,
                    behavior: "smooth"
                });

                return;
            }

            for (let y = markerY + markerHeight + 5; y < markerY + markerHeight + 1000; y += 5) {
                for (let x = markerX + 5; x < markerX + markerWidth; x += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const verticalLockedCell = cell.classList.contains("ct") || cell.classList.contains("cb");

                        // Bring current marker more in view if needed
                        const markerY = this.#marker.getBoundingClientRect().y;
                        if (!verticalLockedCell && markerY > bottomOffset) {
                            window.scrollBy({
                                top: 25,
                                behavior: "smooth"
                            });
                        }

                        return;
                    }
                }
            }

            // No cell found, scroll a bit
            window.scrollBy({
                top: 25,
                behavior: "smooth"
            });
        }

        const findNextEnter = (retry) => {
            const cbb = document.getElementById("cbb");
            const bottomOffset = cbb.getBoundingClientRect().y;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            const verticalLockedCell = this.#markerCell && (
                this.#markerCell.classList.contains("ct")
                || this.#markerCell.classList.contains("cb")
            )

            for (let y = markerY + markerHeight + 5; y < markerY + markerHeight + 1000; y += 5) {
                for (let x = markerX + 5; x < markerX + markerWidth; x += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        const verticalLockedCell = cell.classList.contains("ct") || cell.classList.contains("cb");

                        // Bring current marker more in view if needed
                        const markerY = this.#marker.getBoundingClientRect().y;
                        const markerHeight = parseInt(this.#marker.style.height);
                        if (!verticalLockedCell && markerY + markerHeight > bottomOffset) {
                            window.scrollBy({
                                top: markerY + markerHeight - bottomOffset,
                                behavior: "instant"
                            });
                        }

                        return;
                    }
                }
            }

            if (!verticalLockedCell && retry > 0) {
                // No cell found, scroll a bit and try again
                window.scrollBy({
                    top: 25,
                    behavior: "instant"
                });

                findNextEnter(retry - 1);
            }
        }

        const findPageUp = () => {
            const tb = document.getElementById("tb");
            const tbHeight = parseInt(tb.style.height);
            const topOffset = tbHeight;
            const bottomOffset = window.innerHeight;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            window.scrollBy({
                top: -bottomOffset,
                behavior: "instant"
            });

            const yStart = markerY > bottomOffset ? bottomOffset : markerY
            for (let y = yStart; y > yStart - 1000; y -= 5) {
                for (let x = markerX + 5; x < markerX + markerWidth; x += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        // Bring current marker more in view if needed
                        const markerY = this.#marker.getBoundingClientRect().y;
                        if (markerY + markerHeight > bottomOffset) {
                            window.scrollBy({
                                top: bottomOffset - markerY - markerHeight,
                                behavior: "smooth"
                            });
                        } else if (markerY < topOffset) {
                            window.scrollBy({
                                top: markerY - topOffset,
                                behavior: "smooth"
                            });
                        }


                        return;
                    }
                }
            }
        }

        const findPageDown = () => {
            const tb = document.getElementById("tb");
            const tbHeight = parseInt(tb.style.height);
            const topOffset = tbHeight;
            const markerX = this.#marker.getBoundingClientRect().x;
            const markerY = this.#marker.getBoundingClientRect().y;
            const markerHeight = parseInt(this.#marker.style.height);
            const markerWidth = parseInt(this.#marker.style.width);

            window.scrollBy({
                top: window.innerHeight - topOffset,
                behavior: "instant"
            });

            for (let y = markerY < topOffset ? topOffset : markerY; y < markerY + markerHeight + 1000; y += 5) {
                for (let x = markerX + 5; x < markerX + markerWidth; x += 5) {
                    const cell = findCell(x, y);

                    if (cell) {
                        this.#manageMarkerCell(cell);

                        // Bring current marker more in view if needed
                        const markerY = this.#marker.getBoundingClientRect().y;
                        if (markerY < topOffset) {
                            window.scrollBy({
                                top: markerY - topOffset,
                                behavior: "smooth"
                            });
                        }

                        return;
                    }
                }
            }
        }

        const marker = this.#marker || (() => {
            const mkrContainer = document.getElementById("mkrContainer") || document.createElement("div");
            while (mkrContainer.lastChild) {
                mkrContainer.removeChild(mkrContainer.lastChild);
            }

            mkrContainer.id = "mkrContainer";

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

            mkrContainer.appendChild(newMarker);
            this.#target.appendChild(mkrContainer);
            this.#marker = newMarker;

            /*
            newMarker.addEventListener("transitionend", (e) => {
                newMarker.focus({
                    preventScroll: true
                });
            });
             */

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
                        // TODO Shift+Enter?
                        findNextEnter(100);
                        break;
                    case "Tab":
                        // TODO Shift+Tab
                        findNextTab(100);
                        break;
                    case "PageUp":
                        findPageUp();
                        break;
                    case "PageDown":
                        findPageDown();
                        break;
                    case "ArrowLeft":
                        findNextLeft();
                        break;
                    case "ArrowRight":
                        findNextRight();
                        break;
                    case "ArrowUp":
                        findNextUp();
                        break;
                    case "ArrowDown":
                        findNextDown();
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
                this.#topBannerLeft = null;
                this.#topBannerRight = null;
                this.#leftBanner = null;
                this.#leftBannerTop = null;
                this.#leftBannerBottom = null;
                this.#cellBannerLeft = null;
                this.#cellBannerRight = null;
                this.#cellBannerTop = null;
                this.#cellBannerBottom = null;
                this.#end = null;
                this.#marker = null;
                this.#manageMarkerCell(null);

                this.#lastTile = [-1, -1, -1, -1];
                this.#swapBuffer = true;
                this.#pendingScrolls.length = 0;
                this.#pendingResize.length = 0;
                this.#pendingRemove = [];
                this.#pendingUpdate = false;
                this.#pendingContent = null;
                this.#pendingContentTopics = [];

                break;
            }
            case 1: { // scroll..
                break;
            }
            case 2: { // add content
                const container = document.createElement("div");
                container.className = "container";
                container.style.height = message.ch + "px";
                container.style.width = message.cw + "px";

                const div = document.createElement("div");

                div.id = message.id;
                div.className = message.cellClasses;
                div.style.height = message.h + "px";
                div.style.width = message.w + "px";

                const cc = document.createElement("div");
                cc.className = message.contentClasses;

                if (div.classList.contains("chl")) {
                    div.style.left = message.left + "px";
                    div.style.top = message.top + "px";
                } else if (div.classList.contains("chr")) {
                    div.style.left = "calc(min(100%, " + message.cw + "px) - " + message.right + "px)";
                    div.style.top = message.top + "px";
                } else if (div.classList.contains("ch")) {
                    div.style.marginLeft = message.left + "px";
                    div.style.top = message.top + "px";
                } else if (div.classList.contains("rht")) {
                    div.style.left = message.left + "px";
                    div.style.top = message.top + "px";
                } else if (div.classList.contains("rhb")) {
                    div.style.left = message.left + "px";
                    div.style.top = "calc(min(100%, " + message.ch + "px) - " + message.bottom + "px)";
                } else if (div.classList.contains("rh")) {
                    div.style.left = message.left + "px";
                    div.style.marginTop = message.top + "px";
                } else if (div.classList.contains("cl")) {
                    div.style.left = message.left + "px";
                    div.style.marginTop = message.top + "px";
                } else if (div.classList.contains("cr")) {
                    div.style.left = "calc(min(100%, " + message.cw + "px) - " + message.right + "px)";
                    div.style.marginTop = message.top + "px";
                } else if (div.classList.contains("ct")) {
                    div.style.marginLeft = message.left + "px";
                    div.style.top = message.top + "px";
                } else if (div.classList.contains("cb")) {
                    div.style.marginLeft = message.left + "px";
                    div.style.top = "calc(min(100%, " + message.ch + "px) - " + message.bottom + "px)";
                } else if (div.classList.contains("c")) {
                    div.style.left = message.left + "px";
                    div.style.top = message.top + "px";
                } else {
                    // TODO Probably don't need this else?
                    if (message.left !== undefined) div.style.left = message.left + "px";
                    if (message.right !== undefined) div.style.right = message.right + "px";
                    if (message.top !== undefined) div.style.top = message.top + "px";
                    if (message.bottom !== undefined) div.style.bottom = message.bottom + "px";
                    if (message.mt !== undefined) div.style.marginTop = message.mt + "px";
                    if (message.ml !== undefined) div.style.marginLeft = message.ml + "px";
                }

                if (message.html)
                    cc.innerHTML = message.html;
                else if (message.text)
                    cc.innerText = message.text;

                div.appendChild(cc)

                const co = document.createElement("div");
                co.className = "co";

                div.appendChild(co);

                if (message.marker) {
                    co.addEventListener("click", (e) => {
                        this.#manageMarkerPosition(e.clientX, e.clientY);
                    });
                }

                if (message.resize) {
                    const bb = document.createElement("div");
                    bb.className = "bb";
                    bb.onmousedown = this.#bbMousedown;

                    const rb = document.createElement("div");
                    rb.className = "rb";
                    rb.onmousedown = this.#rbMousedown;

                    div.appendChild(cc);
                    div.appendChild(co);
                    div.appendChild(bb);
                    div.appendChild(rb);
                }

                container.appendChild(div);

                const old = document.getElementById(message.id);

                if (old) {
                    (message.topics || []).forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removing",
                        target: old,
                        message: message
                    }));

                    this.#pendingRemove.push([old, message.topics || []]);
                }

                if (message.text !== null || message.html !== null) {
                    if (this.#pendingContent === null) {
                        this.#pendingContent = document.createDocumentFragment();
                        this.#pendingContentTopics = [];
                    }

                    this.#pendingContent.appendChild(container);
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
                this.#pendingRemove.forEach(data => {
                    const old = data[0];

                    if (old.parentElement.classList.contains("container")) {
                        old.parentElement.remove();
                    } else {
                        old.remove();
                    }

                    data[1].forEach(topic => this.#dispatchTopic({
                        topic: topic,
                        action: "removed",
                        target: old,
                        message: message
                    }));
                });
                this.#pendingRemove = [];

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
                    if (item.parentElement.classList.contains("container")) {
                        item.parentElement.remove();
                    } else {
                        item.remove();
                    }
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
                const topBannerLeft = this.#topBannerLeft || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "tbl";
                    this.#target.appendChild(newBanner);
                    this.#topBannerLeft = newBanner;
                    return newBanner;
                })();
                const topBannerRight = this.#topBannerRight || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "tbr";
                    this.#target.appendChild(newBanner);
                    this.#topBannerRight = newBanner;
                    return newBanner;
                })();
                const leftBanner = this.#leftBanner || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "lb";
                    this.#target.appendChild(newBanner);
                    this.#leftBanner = newBanner;
                    return newBanner;
                })();
                const leftBannerTop = this.#leftBannerTop || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "lbt";
                    this.#target.appendChild(newBanner);
                    this.#leftBannerTop = newBanner;
                    return newBanner;
                })();
                const leftBannerBottom = this.#leftBannerBottom || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "lbb";
                    this.#target.appendChild(newBanner);
                    this.#leftBannerBottom = newBanner;
                    return newBanner;
                })();
                const cellBannerLeft = this.#cellBannerLeft || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "cbl";
                    this.#target.appendChild(newBanner);
                    this.#cellBannerLeft = newBanner;
                    return newBanner;
                })();
                const cellBannerRight = this.#cellBannerRight || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "cbr";
                    this.#target.appendChild(newBanner);
                    this.#cellBannerRight = newBanner;
                    return newBanner;
                })();
                const cellBannerTop = this.#cellBannerTop || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "cbt";
                    this.#target.appendChild(newBanner);
                    this.#cellBannerTop = newBanner;
                    return newBanner;
                })();
                const cellBannerBottom = this.#cellBannerBottom || (() => {
                    const newBanner = document.createElement("div");
                    newBanner.id = "cbb";
                    this.#target.appendChild(newBanner);
                    this.#cellBannerBottom = newBanner;
                    return newBanner;
                })();
                const end = this.#end || (() => {
                    const newEnd = document.createElement("div");
                    newEnd.id = "end";
                    this.#target.appendChild(newEnd);
                    this.#end = newEnd;
                    return newEnd;
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

                topBannerLeft.style.height = (message.cornerY + message.cornerBottomMargin) + "px";
                topBannerLeft.style.width = message.topBannerLeft + "px";
                if (message.topBannerLeft === 0) {
                    topBannerLeft.style.visibility = "hidden";
                } else {
                    topBannerLeft.style.visibility = "visible";
                }

                topBannerRight.style.height = (message.cornerY + message.cornerBottomMargin) + "px";
                topBannerRight.style.width = message.topBannerRight + "px";
                topBannerRight.style.left = "calc(min(100%, " + message.maxX + "px) - " + message.topBannerRight + "px)";
                if (message.topBannerRight === 0) {
                    topBannerRight.style.visibility = "hidden";
                } else {
                    topBannerRight.style.visibility = "visible";
                }

                leftBanner.style.height = message.maxY + "px";
                leftBanner.style.width = (message.cornerX + message.cornerRightMargin) + "px";

                leftBannerTop.style.height = message.leftBannerTop + "px";
                leftBannerTop.style.width = (message.cornerX + message.cornerRightMargin) + "px";
                if (message.leftBannerTop === 0) {
                    leftBannerTop.style.visibility = "hidden";
                } else {
                    leftBannerTop.style.visibility = "visible";
                }

                leftBannerBottom.style.height = message.leftBannerBottom + "px";
                leftBannerBottom.style.width = (message.cornerX + message.cornerRightMargin) + "px";
                leftBannerBottom.style.top = "calc(min(100%, " + message.maxY + "px) - " + message.leftBannerBottom + "px)";
                if (message.leftBannerBottom === 0) {
                    leftBannerBottom.style.visibility = "hidden";
                } else {
                    leftBannerBottom.style.visibility = "visible";
                }

                cellBannerLeft.style.top = (message.cornerY + message.cornerBottomMargin) + "px";
                cellBannerLeft.style.height = (message.maxY - message.cornerY - message.cornerBottomMargin) + "px";
                cellBannerLeft.style.width = message.topBannerLeft + "px";
                if (message.topBannerLeft === 0) {
                    cellBannerLeft.style.visibility = "hidden";
                } else {
                    cellBannerLeft.style.visibility = "visible";
                }

                cellBannerRight.style.top = (message.cornerY + message.cornerBottomMargin) + "px";
                cellBannerRight.style.height = (message.maxY - message.cornerY - message.cornerBottomMargin) + "px";
                cellBannerRight.style.width = message.topBannerRight + "px";
                cellBannerRight.style.left = "calc(min(100%, " + message.maxX + "px) - " + message.topBannerRight + "px)";
                if (message.topBannerRight === 0) {
                    cellBannerRight.style.visibility = "hidden";
                } else {
                    cellBannerRight.style.visibility = "visible";
                }

                cellBannerTop.style.left = (message.cornerX + message.cornerRightMargin) + "px";
                cellBannerTop.style.height = message.leftBannerTop + "px";
                cellBannerTop.style.width = (message.maxX - message.cornerX - message.cornerRightMargin) + "px";
                if (message.leftBannerTop === 0) {
                    cellBannerTop.style.visibility = "hidden";
                } else {
                    cellBannerTop.style.visibility = "visible";
                }

                cellBannerBottom.style.left = (message.cornerX + message.cornerRightMargin) + "px";
                cellBannerBottom.style.height = message.leftBannerBottom + "px";
                cellBannerBottom.style.width = (message.maxX - message.cornerX - message.cornerRightMargin) + "px";
                cellBannerBottom.style.top = "calc(min(100%, " + message.maxY + "px) - " + message.leftBannerBottom + "px)";
                if (message.leftBannerBottom === 0) {
                    cellBannerBottom.style.visibility = "hidden";
                } else {
                    cellBannerBottom.style.visibility = "visible";
                }

                end.style.top = (message.maxY - 1) + "px";
                end.style.left = (message.maxX - 1) + "px";

                this.#target.style.height = message.maxY + "px";
                this.#target.style.width = message.maxX + "px";

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
