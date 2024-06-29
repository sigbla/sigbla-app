# View resources

Because the UI runs in the browser, it doesn't take much imagination to think that it would be handy to be able to
add extensions to the frontend. As seen in other chapters, this is how we're able to add charts and user input
to table views.

First step in allowing for such extensions is to be able to extend the HTTP endpoints exposed, something we'll cover
next. We then move on to describing some helper functions making it easier to add custom JavaScript and CSS.

## Adding HTTP endpoints

When viewing the table in the browser via a table view, certain endpoints are used by Sigbla in order to push
cell content to the frontend. These endpoints are hosted by the Ktor framework, embedded into Sigbla. Through what's
known as resources we're able to add custom endpoints to a selected table view.

These endpoints can accept the usual HTTP commands, such as GET and POST. This allows us to add endpoints that both
accept input and provide output.

There's no need to fiddle around with Ktor directly, because the table view exposes them through more convenient
functions. Here's an example that adds a simple endpoint returning just a text message:

``` kotlin
import sigbla.app.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun main() {
    TableView[Port] = 8080

    val table = Table["MyTable"]
    val tableView = TableView[table]

    tableView[Resource["my-resources/service-1"]] = {
        call.respondText(text = "Hello from my resources")
    }

    val url = show(tableView)
    println(url)
}
```

If you run this, you can open your browser to http://127.0.0.1:8080/t/MyTable/my-resources/service-1 and get the message.

![Example showing output from a table view resource](img/resources_get_endpoint_example.png)

You'll notice that when we specify the resource we provide a relative URL, which is relative to the table view. If you
were to specify it as `Resource["/my-resources/service-1"]`, the leading slash would be automatically removed. An empty
path is not allowed when adding resources to a table view.

Next is an example of how you'd allow for a basic POST request:

``` kotlin
import sigbla.app.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*

fun main() {
    TableView[Port] = 8080

    val table = Table["MyTable"]
    val tableView = TableView[table]

    tableView[Resource["my-resources/service-2"]] = {
        val name = call.receiveText()
        call.respondText(text = "Hello $name")
    }

    val url = show(tableView)
    println(url)
}
```

You could test this out with curl for example:

``` shell
$ curl --data "World" http://127.0.0.1:8080/t/MyTable/my-resources/service-2
Hello World
```

You can add as many resources as you need, and replace existing resources by reassigning them. Assigning a `Unit` to
any resource will remove it, as we're used to from other assignments.

## Making available static resources

Often you just need to include some static content, and there are a few options for that:

``` kotlin
tableView[Resource["my-resources/file-1.jpg"]] = staticResource("/some-folder/file.jpg")
tableView[Resource["my-resources/file-2.jpg"]] = staticFile(File("/file-path/file.jpg"))
```

For the first resource, we use `staticResource` to pass the path to a file within our resources. Resources are usually
packaged together with your code in a jar file, located within the project `resources` folder, making it easy to
include these without needing separate files.

The second example uses `staticFile`, which instead looks for the file somewhere on your filesystem.

There's also one called `staticText`, which allows us to simplify our first example service somewhat:

``` kotlin
import sigbla.app.*
import io.ktor.http.*

fun main() {
    TableView[Port] = 8080

    val table = Table["MyTable"]
    val tableView = TableView[table]

    tableView[Resource["my-resources/service-1"]] = staticText(
        contentType = ContentType.Text.Plain,
        text = "Hello from my resources"
    )

    val url = show(tableView)
    println(url)
}
```

You see that we specify the content type, which might give you some ideas as to how you could serve CSS and JavaScript.
But if you did it through `staticText`, or `staticFile` or `staticResource` (who also would work out the right
content type by looking at the file extension), how would you ensure the browser actually loaded these resources?

## Automatically loading CSS and JavaScript

It's not enough to just add some CSS or JavaScript code as a resource if you want to make use of it in the browser.
You'll also need to ensure the browser actually loads these resources so that they become available to make use of.

This is where functions like `css`, `cssFile`, `cssResource` for CSS content, together with `js`, `jsFile`, and
`jsResource` for JavaScript, get involved.

The next chapter on view extensions cover this in more detail, but let's look at a quick example changing the CSS so
that we can change the default colors of a table:

```
TableView[Port] = 8080

val table = Table["MyTable"]
val tableView = TableView[table]

table["A", 0] = 100
table["A", 1] = 200

tableView[Resource["my.css"]] = css {
    """
        #tc, .ch, .rh {
            background-color: lightgreen;
        }
        .c {
            background-color: lightgray;
        }
        .c:hover {
            background-color: gray;
        }
    """
}

val url = show(tableView)
println(url)
```

This produces a lovely looking table with our custom color scheme:

![Table with custom CSS](img/resources_css_example.png)

## Root resources

So far, whenever we've done `tableView[Resource["some-path"]]` we've defined resources that are placed under the URL
of the table view. If the table reference was `my-table`, the `some-path` resource would be accessible from
`http://host:post/t/my-table/some-path`.

It's also possible to define resources outside the table view, known as root resources. This is how you'd do that:

``` kotlin
TableView[Port] = 8080

Resource["root-resource"] = staticText("Root resource")

// We need to show something to start the web server
show(Table["dummy"])
```

Running this and you can then access `http://127.0.0.1:8080/root-resource`. Root resources are good for shared
resources used across several tables. It also allows you to create a new root index page at `http://127.0.0.1:8080/`:

`Resource["/"] = staticText("Root resource")`

Note that leading slashes are automatically removed, so the above is the same as:

`Resource[""] = staticText("Root resource")`

But it might be more indicative to use a `/` for the root if you prefer.