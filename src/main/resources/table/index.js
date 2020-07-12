let socket
let lastTile = [-1, -1, -1, -1]
let pendingContent = null
let pendingUpdate = false
let pendingScrolls = []

if (document.readyState !== "loading") {
    init()
} else {
    document.addEventListener("DOMContentLoaded", init)
}

async function init() {
    let pathname = location.pathname.endsWith("/") ? location.pathname : location.pathname + "/"
    let url = new URL(pathname + "socket", window.location.href)
    url.protocol = url.protocol.replace('https', 'wss')
    url.protocol = url.protocol.replace('http', 'ws')

    socket = new WebSocket(url.href)
    socket.addEventListener("open", socketOpen)
    socket.addEventListener("message", socketMessage)
}

async function socketOpen(_) {
    scroll()
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
                child.style.height = (message.h-1) + "px"
                child.style.width = (message.w-1) + "px"
                child.innerText = message.content
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
            if (pendingScrolls.length > 0) {
                let pendingScroll = pendingScrolls.pop()
                pendingScrolls.length = 0
                socket.send(JSON.stringify(pendingScroll))
            } else {
                pendingUpdate = false
            }
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