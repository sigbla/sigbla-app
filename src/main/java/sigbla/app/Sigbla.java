package sigbla.app;

import sigbla.app.internals.SigblaApp;

public class Sigbla {
    public static void main(String... args) throws Exception {
        new SigblaApp().start();
        Thread.sleep(Long.MAX_VALUE);
    }
}
