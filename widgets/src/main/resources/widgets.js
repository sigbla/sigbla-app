window.sigbla.onTopic("sigbla-widgets-button", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value
        if (callback === null || callback === undefined || callback.trim() === "") return;

        const onclick = async (e) => {
            const response = await fetch(callback, {
                method: "POST"
            });
            e.target.disabled = true;
            window.sigbla.lastWidgetId = e.target.id
            const result = await response.json();
            if (result) e.target.disabled = false;
        }

        input.onclick = onclick;
    } else if (data.action === "attached") {
        const input = data.target.querySelector("input");
        if (input == null) return;
        if (input.id === window.sigbla.lastWidgetId) {
            input.focus();
            window.sigbla.lastWidgetId = undefined;
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-checkbox", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        const onclick = async (e) => {
            const response = await fetch(callback, {
                method: "POST",
                body: e.target.checked ? "true" : "false"
            });
            e.target.disabled = true;
            window.sigbla.lastWidgetId = e.target.id
            const result = await response.json();
            if (result) e.target.disabled = false;
        }

        input.onclick = onclick;
    } else if (data.action === "attached") {
        const input = data.target.querySelector("input");
        if (input == null) return;
        if (input.id === window.sigbla.lastWidgetId) {
            input.focus();
            window.sigbla.lastWidgetId = undefined;
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-radio", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        const onclick = async (e) => {
            const response = await fetch(callback, {
                method: "POST",
                body: e.target.checked ? "true" : "false"
            });
            e.target.disabled = true;
            window.sigbla.lastWidgetId = e.target.id
            const result = await response.json();
            if (result) e.target.disabled = false;
        }

        input.onclick = onclick;
    } else if (data.action === "attached") {
        const input = data.target.querySelector("input");
        if (input == null) return;
        if (input.id === window.sigbla.lastWidgetId) {
            input.focus();
            window.sigbla.lastWidgetId = undefined;
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-textfield", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        const onblur = async (e) => {
            const response = await fetch(callback, {
                method: "POST",
                body: e.target.value
            });
            e.target.disabled = true;
            window.sigbla.lastWidgetId = e.target.id
            const result = await response.json();
            if (result) e.target.disabled = false;
        }

        input.onblur = onblur
    } else if (data.action === "attached") {
        const input = data.target.querySelector("input");
        if (input == null) return;
        if (input.id === window.sigbla.lastWidgetId) {
            // because we do onblur, no input.focus();
            window.sigbla.lastWidgetId = undefined;
        }
    }
});

