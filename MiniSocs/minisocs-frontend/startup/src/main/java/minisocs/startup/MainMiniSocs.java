package minisocs.startup;

import org.apache.logging.log4j.Level;

import layer.amqp.ForDetectingAmqpAdapter;
import layer.rest.RestServer;
import layer.sse.SseAdapter;
import minisocs.MiniSocs;
import minisocs.common.Log;

public final class MainMiniSocs {
    private MainMiniSocs() {
    }

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("usage: REST base URI, SSE base URI [, broker URI]");
        }
        var restURI = args[0];
        var sseURI = args[1];

        Log.setLevel(Log.MINISOCS, Level.INFO);

        // driven - facade - driving
        var sse_adapter = new SseAdapter(sseURI);
        sse_adapter.startSseServer();
        var detecting_adapter = args.length > 2 ? new ForDetectingAmqpAdapter(args[2]) : null;
        var facade = new MiniSocs(sse_adapter, detecting_adapter);
        var rest_server = new RestServer(restURI, facade);
        rest_server.startRestServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            rest_server.shutRestServerDown();
            sse_adapter.shutSseServerDown();
            if (detecting_adapter != null) {
                try {
                    detecting_adapter.close();
                } catch (Exception problem) {
                }
            }
        }));

        System.out.println("Started MiniSocs Frontend, rest at " + restURI + ", SSE at " + sseURI);
        Thread.currentThread().join();
    }
}
