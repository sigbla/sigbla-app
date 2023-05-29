console.log("Hello from test.js", window.sigbla)

const topicListener1 = window.sigbla.onTopic("resourceTopic1", (data) => {
    console.log("topic", data);

    if (data.action === "show") {
        data.target.innerHTML = "<div style='background-color: red; width: 100%; height: 100%;'>" + data.target.innerText + "</div>";
    }
})

const clearListener1 = window.sigbla.onClear((data) => {
    console.log("clear", data);
});