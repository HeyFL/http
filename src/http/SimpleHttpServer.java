package http;

import java.util.concurrent.*;
import java.nio.charset.*;
import java.net.*;

import com.sun.deploy.util.StringUtils;
import com.sun.net.httpserver.*;
import java.util.*;
import java.io.*;

public class SimpleHttpServer
{
    public static void main(final String[] args) throws Exception {
        int port = 9999;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/ok", new MyHandler(args[1]));
        server.setExecutor(null);
        server.start();
        System.out.println("http server started! port:" + port);
    }

    static class MyHandler implements HttpHandler
    {
        private String responseFilePath;
        public MyHandler(String arg) {
            responseFilePath = arg;
        }

        @Override
        public void handle(final HttpExchange t) throws IOException {
            System.out.println("===================================================");
            System.out.println("time: " + new Date());
            System.out.println("method: " + t.getRequestMethod());
            System.out.println("URI: " + t.getRequestURI());
            final Headers headers = t.getRequestHeaders();
            final Set<String> keys = headers.keySet();
            System.out.println("headers: {");
            for (final String key : keys) {
                System.out.println("\t" + key + " = " + headers.get((Object)key));
            }
            System.out.println("}");
            t.getRequestBody();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody(), Charset.forName("UTF-8")));
            final StringBuffer content = new StringBuffer();
            System.out.println("content: {");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("\t" + line);
                content.append(line);
            }
            System.out.println("}");
            System.out.println("decoder content: {");
            System.out.println("\t" + URLDecoder.decode(content.toString(), "UTF-8"));
            System.out.println("}");
            System.out.println("decoder decoder content: {");
            System.out.println("\t" + URLDecoder.decode(URLDecoder.decode(content.toString(), "UTF-8"), "UTF-8"));
            System.out.println("}");

            if (null==responseFilePath||"".equals(responseFilePath)) {
                responseFilePath = "response";
            }
            final File responsetxt = new File(responseFilePath+".txt");
            if (!responsetxt.exists()) {
                responsetxt.createNewFile();
            }
            final StringBuilder response = new StringBuilder();
            final BufferedReader br = new BufferedReader(new FileReader(responsetxt));
            String xx = null;
            while ((xx = br.readLine()) != null) {
                response.append(xx);
            }
            String responsess = response.toString();
            if (responsess == null || responsess.length() == 0) {
                responsess = "<Response service=\"OrderFilterPushService\"><Head>OK</Head></Response>";
            }
            final byte[] data = responsess.getBytes(Charset.forName("UTF-8").name());
            t.sendResponseHeaders(200, data.length);
            final OutputStream os = t.getResponseBody();
            try {
                os.write(data);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            os.close();
        }
    }
}
