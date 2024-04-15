/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
window.sigbla.onTopic("sigbla-widgets-button", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value
        if (callback === null || callback === undefined || callback.trim() === "") return;

        data.target.onkeydown = async (e) => {
            if (e.key === " ") {
                await fetch(callback, {
                    method: "POST"
                });
            }
        }

        input.onclick = async (e) => {
            await fetch(callback, {
                method: "POST"
            });
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-checkbox", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        data.target.onkeydown = async (e) => {
            if (e.key === " ") {
                await fetch(callback, {
                    method: "POST",
                    body: input.checked ? "false" : "true" // Note: swapped to toggle
                });
            }
        }

        input.onclick = async (e) => {
            await fetch(callback, {
                method: "POST",
                body: e.target.checked ? "true" : "false"
            });
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-radio", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        data.target.onkeydown = async (e) => {
            if (e.key === " ") {
                await fetch(callback, {
                    method: "POST",
                    body: input.checked ? "false" : "true" // Note: swapped to toggle
                });
            }
        }

        input.onclick = async (e) => {
            await fetch(callback, {
                method: "POST",
                body: e.target.checked ? "true" : "false"
            });
        }
    }
});

window.sigbla.onTopic("sigbla-widgets-textfield", (data) => {
    if (data.action === "preparing") {
        const input = data.target.querySelector("input");
        if (input == null) return;

        const callback = input.attributes.getNamedItem("callback").value;
        if (callback === null || callback === undefined || callback.trim() === "") return;

        let firstKey = true;
        let original = input.value

        data.target.onkeydown = async (e) => {
            switch (e.key) {
                case "Escape":
                    firstKey = true;
                    input.value = original;
                    break;
                case "Enter":
                    firstKey = true;
                    if (input.value !== original) {
                        original = input.value;
                        await fetch(callback, {
                            method: "POST",
                            body: input.value
                        });
                    }
                    break;
                case "Tab":
                    firstKey = true;
                    if (input.value !== original) {
                        original = input.value;
                        await fetch(callback, {
                            method: "POST",
                            body: input.value
                        });
                    }
                    break;
                case "Backspace":
                    input.value = input.value.slice(0, -1);
                    break;
                default:
                    if (/^.$/u.test(e.key)) {
                        if (firstKey) {
                            original = input.value;
                            input.value = "";
                            firstKey = false;
                        }
                        input.value += e.key;
                    }
            }
        }

        data.target.onblur = async (e) => {
            if (input.value !== original) {
                original = input.value;
                await fetch(callback, {
                    method: "POST",
                    body: input.value
                });
            }
        }

        input.onblur = async (e) => {
            if (e.target.value !== original) {
                original = input.value;
                await fetch(callback, {
                    method: "POST",
                    body: e.target.value
                });
            }
        }
    }
});

