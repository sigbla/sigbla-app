let socket
let lastTile = [-1, -1, -1, -1]
let pendingContent = null
let pendingUpdate = false
let pendingScrolls = []
let pendingResize = []

let resizeTarget
let resizeStartX
let resizeStartY
let enableVerticalOverlay = false
let enableHorizontalOverlay = false

function init() {
    let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/"
    let url = new URL(pathname + "socket", window.location.href)
    url.protocol = url.protocol.replace('https', 'wss')
    url.protocol = url.protocol.replace('http', 'ws')

    socket = new WebSocket(url.href)
    socket.addEventListener("open", socketOpen)
    socket.addEventListener("message", socketMessage)
}

const rbMousedown = (e) => {
    if (e.buttons === 1) {
        resizeTarget = e.target.parentElement.parentElement.id

        enableVerticalOverlay = true
        enableHorizontalOverlay = false

        resizeStartX = e.clientX
        resizeStartY = e.clientY

        document.getElementById("overlay").style.display = "block"
        document.getElementById("ovl").style.display = "block"
        document.getElementById("ohl").style.display = "none"
        document.getElementById("ovl").style.left = e.clientX + "px"
    }
}

const bbMousedown = (e) => {
    if (e.buttons === 1) {
        resizeTarget = e.target.parentElement.parentElement.id

        enableVerticalOverlay = false
        enableHorizontalOverlay = true

        resizeStartX = e.clientX
        resizeStartY = e.clientY

        document.getElementById("overlay").style.display = "block"
        document.getElementById("ovl").style.display = "none"
        document.getElementById("ohl").style.display = "block"
        document.getElementById("ohl").style.top = e.clientY + "px"
    }
}

const submitResize = (target, sizeChangeX, sizeChangeY) => {
    const targetElement = document.getElementById(target)

    console.log(targetElement)

    const resizeEvent = {"type": "resize", "target": target, "sizeChangeX": sizeChangeX, "sizeChangeY": sizeChangeY}

    if (pendingUpdate) {
        pendingResize.push(resizeEvent)
    } else {
        pendingUpdate = true
        socket.send(JSON.stringify(resizeEvent))
    }
}

const overlayMouseup = (e) => {
    if (enableVerticalOverlay) {
        const resizeX = e.clientX - resizeStartX
        submitResize(resizeTarget, resizeX, 0)
        console.log("Resize target: " + resizeTarget)
        console.log("Resize X: " + resizeX)
    } else if (enableHorizontalOverlay) {
        const resizeY = e.clientY - resizeStartY
        submitResize(resizeTarget, 0, resizeY)
        console.log("Resize target: " + resizeTarget)
        console.log("Resize Y: " + resizeY)
    }

    document.getElementById("overlay").style.display = "none"

    enableVerticalOverlay = false
    enableHorizontalOverlay = false
}

const overlayMousemove = (e) => {
    if (enableVerticalOverlay) document.getElementById("ovl").style.left = e.clientX + "px"
    if (enableHorizontalOverlay) document.getElementById("ohl").style.top = e.clientY + "px"
}

async function socketOpen(_) {
    await scroll()
    window.addEventListener("scroll", scroll)
    window.addEventListener("resize", scroll)
}

async function scroll() {
    const inc = 100

    const xoffset = window.pageXOffset
    const yoffset = window.pageYOffset

    // const colHeaders = document.getElementsByClassName("ch")
    // for (let i = 0; i < colHeaders.length; i++) {
    //     const header = colHeaders.item(i)
    //     header.style.marginLeft = "-" + xoffset + "px"
    // }
    // const rowHeaders = document.getElementsByClassName("rh")
    // for (let i = 0; i < rowHeaders.length; i++) {
    //     const header = rowHeaders.item(i)
    //     header.style.marginTop = "-" + yoffset + "px"
    // }

    const height = window.innerHeight - (window.innerHeight % inc) + inc
    const width = window.innerWidth - (window.innerWidth % inc) + inc

    const xtile = xoffset - (xoffset % inc)
    const ytile = yoffset - (yoffset % inc)

    //document.getElementById("pos").innerText = xoffset + ", " + yoffset + " | " + xtile + ", " + ytile;

    if (lastTile[0] === xtile && lastTile[1] === ytile && lastTile[2] === height && lastTile[3] === width) return

    lastTile = [xtile, ytile, height, width]

    const scrollEvent = {"type": "scroll", "x": xtile, "y": ytile, "h": height, "w": width}

    if (pendingUpdate) {
        pendingScrolls.push(scrollEvent)
    } else {
        pendingUpdate = true
        socket.send(JSON.stringify(scrollEvent))
    }
}

async function socketMessage(e) {
    //document.getElementById("message").innerText = e.data
    let messages = JSON.parse(e.data)
    if (!Array.isArray(messages)) messages = [messages]
    messages.forEach(await handleMessage)
}

async function handleMessage(message) {
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
                bb.onmousedown = bbMousedown

                const rb = document.createElement("div")
                rb.className = "rb"
                rb.onmousedown = rbMousedown

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

                if (message.classes.startsWith("chtml "))
                    div.innerHTML = message.content
                else
                    div.innerText = message.content
            }

            if (pendingContent === null) {
                pendingContent = document.createDocumentFragment()
            }

            pendingContent.appendChild(div)
            break
        }
        case "add-commit": {
            if (pendingContent === null) break
            document.body.appendChild(pendingContent)
            pendingContent = null
            break
        }
        case "rm": {
            document.getElementById(message.id).remove()
            break
        }
        case "update-end": {
            let havePendingResize = false
            let havePendingScroll = false

            if (pendingResize.length > 0) {
                for (let i = 0; i < pendingResize.length; i++) {
                    socket.send(JSON.stringify(pendingResize[i]))
                }
                pendingResize.length = 0
                havePendingResize = true
            }

            if (pendingScrolls.length > 0) {
                let pendingScroll = pendingScrolls.pop()
                pendingScrolls.length = 0
                socket.send(JSON.stringify(pendingScroll))
                havePendingScroll = true
            }

            pendingUpdate = havePendingResize || havePendingScroll

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

if (document.readyState !== "loading") {
    init()
} else {
    document.addEventListener("DOMContentLoaded", init)
}
