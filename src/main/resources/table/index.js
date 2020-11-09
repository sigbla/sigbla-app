class Sigbla {
    target
    socket
    lastTile = [-1, -1, -1, -1]
    pendingContent = null
    pendingUpdate = false
    pendingScrolls = []
    pendingResize = []

    resizeTarget
    resizeStartX
    resizeStartY
    enableVerticalOverlay = false
    enableHorizontalOverlay = false

    constructor(target) {
        this.target = target
    }

    rbMousedown = (e) => {
        if (e.buttons === 1) {
            this.resizeTarget = e.target.parentElement.parentElement.id

            this.enableVerticalOverlay = true
            this.enableHorizontalOverlay = false

            this.resizeStartX = e.clientX
            this.resizeStartY = e.clientY

            document.getElementById("overlay").style.display = "block"
            document.getElementById("ovl").style.display = "block"
            document.getElementById("ohl").style.display = "none"
            document.getElementById("ovl").style.left = e.clientX + "px"
        }
    }

    bbMousedown = (e) => {
        if (e.buttons === 1) {
            this.resizeTarget = e.target.parentElement.parentElement.id

            this.enableVerticalOverlay = false
            this.enableHorizontalOverlay = true

            this.resizeStartX = e.clientX
            this.resizeStartY = e.clientY

            document.getElementById("overlay").style.display = "block"
            document.getElementById("ovl").style.display = "none"
            document.getElementById("ohl").style.display = "block"
            document.getElementById("ohl").style.top = e.clientY + "px"
        }
    }

    submitResize = (target, sizeChangeX, sizeChangeY) => {
        const resizeEvent = {"type": "resize", "target": target, "sizeChangeX": sizeChangeX, "sizeChangeY": sizeChangeY}

        if (this.pendingUpdate) {
            this.pendingResize.push(resizeEvent)
        } else {
            this.pendingUpdate = true
            this.socket.send(JSON.stringify(resizeEvent))
        }
    }

    overlayMouseup = (e) => {
        if (this.enableVerticalOverlay) {
            const resizeX = e.clientX - this.resizeStartX
            this.submitResize(this.resizeTarget, resizeX, 0)
        } else if (this.enableHorizontalOverlay) {
            const resizeY = e.clientY - this.resizeStartY
            this.submitResize(this.resizeTarget, 0, resizeY)
        }

        document.getElementById("overlay").style.display = "none"

        this.enableVerticalOverlay = false
        this.enableHorizontalOverlay = false
    }

    overlayMousemove = (e) => {
        if (this.enableVerticalOverlay) document.getElementById("ovl").style.left = e.clientX + "px"
        if (this.enableHorizontalOverlay) document.getElementById("ohl").style.top = e.clientY + "px"
    }

    scroll = async () => {
        const inc = 100

        const xoffset = window.pageXOffset
        const yoffset = window.pageYOffset

        const height = window.innerHeight - (window.innerHeight % inc) + inc
        const width = window.innerWidth - (window.innerWidth % inc) + inc

        const xtile = xoffset - (xoffset % inc)
        const ytile = yoffset - (yoffset % inc)

        //document.getElementById("pos").innerText = xoffset + ", " + yoffset + " | " + xtile + ", " + ytile;

        if (this.lastTile[0] === xtile && this.lastTile[1] === ytile && this.lastTile[2] === height && this.lastTile[3] === width) return

        this.lastTile = [xtile, ytile, height, width]

        const scrollEvent = {"type": "scroll", "x": xtile, "y": ytile, "h": height, "w": width}

        if (this.pendingUpdate) {
            this.pendingScrolls.push(scrollEvent)
        } else {
            this.pendingUpdate = true
            this.socket.send(JSON.stringify(scrollEvent))
        }
    }

    socketOpen = async (_) => {
        await this.scroll()
        window.addEventListener("scroll", this.scroll)
        window.addEventListener("resize", this.scroll)
    }

    handleMessage = async (message) => {
        switch (message.type) {
            case "add": {
                const div = document.createElement("div")
                div.id = message.id
                if (message.classes === "ch" || message.classes === "rh") {
                    const child = document.createElement("div")
                    child.className = message.classes
                    div.className = "container"
                    div.style.height = message.ch + "px"
                    div.style.width = message.cw + "px"

                    //if (message.z !== undefined) child.style.zIndex = message.z
                    if (message.x !== undefined) child.style.left = message.x + "px"
                    if (message.y !== undefined) child.style.top = message.y + "px"
                    if (message.mt !== undefined) child.style.marginTop = message.mt + "px"
                    if (message.ml !== undefined) child.style.marginLeft = message.ml + "px"
                    // TODO Maybe include a border width somewhere?
                    child.style.height = (message.h-0) + "px"
                    child.style.width = (message.w-0) + "px"
                    child.innerText = message.content

                    const bb = document.createElement("div")
                    bb.className = "bb"
                    bb.onmousedown = this.bbMousedown

                    const rb = document.createElement("div")
                    rb.className = "rb"
                    rb.onmousedown = this.rbMousedown

                    child.appendChild(bb)
                    child.appendChild(rb)

                    div.appendChild(child)
                } else {
                    div.className = message.classes

                    //if (message.z !== undefined) div.style.zIndex = message.z
                    if (message.x !== undefined) div.style.left = message.x + "px"
                    if (message.y !== undefined) div.style.top = message.y + "px"
                    if (message.mt !== undefined) div.style.marginTop = message.mt + "px"
                    if (message.ml !== undefined) div.style.marginLeft = message.ml + "px"
                    div.style.height = (message.h-1) + "px"
                    div.style.width = (message.w-1) + "px"

                    if (message.classes.startsWith("hc "))
                        div.innerHTML = message.content
                    else
                        div.innerText = message.content
                }

                if (this.pendingContent === null) {
                    this.pendingContent = document.createDocumentFragment()
                }

                this.pendingContent.appendChild(div)
                break
            }
            case "add-commit": {
                if (this.pendingContent === null) break
                document.body.appendChild(this.pendingContent)
                this.pendingContent = null
                break
            }
            case "rm": {
                document.getElementById(message.id).remove()
                break
            }
            case "update-end": {
                let havePendingResize = false
                let havePendingScroll = false

                if (this.pendingResize.length > 0) {
                    for (let i = 0; i < this.pendingResize.length; i++) {
                        this.socket.send(JSON.stringify(this.pendingResize[i]))
                    }
                    this.pendingResize.length = 0
                    havePendingResize = true
                }

                if (this.pendingScrolls.length > 0) {
                    let pendingScroll = this.pendingScrolls.pop()
                    this.pendingScrolls.length = 0
                    this.socket.send(JSON.stringify(pendingScroll))
                    havePendingScroll = true
                }

                this.pendingUpdate = havePendingResize || havePendingScroll

                break
            }
            case "dims": {
                const corner = document.getElementById("tc") || (() => {
                    const newCorner = document.createElement("div")
                    newCorner.id = "tc"
                    document.body.appendChild(newCorner)
                    return newCorner
                })()

                corner.style.height = (message.cornerY-1) + "px"
                corner.style.width = (message.cornerX-1) + "px"

                document.body.style.height = message.maxY + "px"
                document.body.style.width = message.maxX + "px"

                const end = document.getElementById("end") || (() => {
                    const newEnd = document.createElement("div")
                    newEnd.id = "end"
                    newEnd.style.position = "absolute"
                    newEnd.style.width = "1px"
                    newEnd.style.height = "1px"
                    newEnd.style.backgroundColor = "black"
                    document.body.appendChild(newEnd)
                    return newEnd
                })()

                end.style.top = message.maxY + "px"
                end.style.left = message.maxX + "px"
            }
        }
    }

    socketMessage = async (e) => {
        let messages = JSON.parse(e.data)
        if (!Array.isArray(messages)) messages = [messages]
        messages.forEach(await this.handleMessage)
    }

    init = () => {
        const overlay = document.createElement("div")
        overlay.id = "overlay"
        overlay.onmouseup = this.overlayMouseup
        overlay.onmousemove = this.overlayMousemove

        const ovl = document.createElement("div")
        ovl.id = "ovl"

        const ovh = document.createElement("div")
        ovh.id = "ohl"

        overlay.appendChild(ovl)
        overlay.appendChild(ovh)

        this.target.appendChild(overlay)

        let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/"
        let url = new URL(pathname + "socket", window.location.href)
        url.protocol = url.protocol.replace('https', 'wss')
        url.protocol = url.protocol.replace('http', 'ws')

        this.socket = new WebSocket(url.href)
        this.socket.addEventListener("open", this.socketOpen)
        this.socket.addEventListener("message", this.socketMessage)
    }
}
