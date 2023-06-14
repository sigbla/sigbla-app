window.sigbla.onTopic("sigbla-widgets-button", (data) => {
    if (data.action === "show") {
        const input = data.target.querySelector("input");
        const callback = input.attributes.getNamedItem("callback").value
        if (callback === null || callback === undefined || callback.trim() === "") return;

        input.onclick = (e) => fetch("resources/" + callback, {
            method: "POST"
        });
    }
})
