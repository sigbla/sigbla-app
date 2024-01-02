/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
window.sigbla.onTopic("sigbla-charts", (data) => {
    if (data.action === "preparing") {
        const canvas = data.target.querySelector("canvas");
        if (canvas == null) return;

        canvas.parentNode.style.height = data.target.style.height;
        canvas.parentNode.style.width = data.target.style.width;

        const callback = canvas.parentNode.attributes.getNamedItem("callback").value
        if (callback === null || callback === undefined || callback.trim() === "") return;

        fetch(callback)
            .then(response => response.json())
            .then(config => {
                new Chart(canvas, config);
            });
    }
});
