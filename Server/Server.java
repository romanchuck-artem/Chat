
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements HttpHandler {
    private List<Message> history = new ArrayList<Message>();
    private MessageExchange messageExchange = new MessageExchange();
    private int currentId = 0;
    private Lock lock = new ReentrantLock();
    
    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java Server port");
        else {
            try {
                System.out.println("Server is starting...");
                Integer port = Integer.parseInt(args[0]);
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                System.out.println("Server started.");
                String serverHost = InetAddress.getLocalHost().getHostAddress();
                System.out.println("Get list of messages: GET http://" + serverHost + ":" + port + "/chat?token={token}");
                System.out.println("Send message: POST http://" + serverHost + ":" + port + "/chat provide body json in format {\"message\" : \"{message}\"} ");

                server.createContext("/chat", new Server());
                server.setExecutor(null);
                server.start();
            } catch (IOException e) {
                System.out.println("Error creating http server: " + e);
            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";

        if ("GET".equals(httpExchange.getRequestMethod())) {
            response = doGet(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            doPost(httpExchange);
        } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
        	doDelete(httpExchange);
        } else if ("PUT".equals(httpExchange.getRequestMethod())) {
        	doPut(httpExchange);
        }else if ("OPTIONS".equals(httpExchange.getRequestMethod())) {
            response = "";
        }
          else {
            response = "Unsupported http method: " + httpExchange.getRequestMethod();
        }

        sendResponse(httpExchange, response);
    }

    private String doGet(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> map = queryToMap(query);
            String token = map.get("token");
            if (token != null && !"".equals(token)) {
                int index = messageExchange.getIndex(token);
                return messageExchange.getServerResponse(history.subList(index, history.size()));
            } else {
                return "Token query parameter is absent in url: " + query;
            }
        }
        return  "Absent query in url";
    }

    private void doPost(HttpExchange httpExchange) {
        try {
            Message message = messageExchange.getClientMessage(httpExchange.getRequestBody());
            
            lock.lock();
            try {
            	message.setID(currentId++);
            } finally {
            	lock.unlock();
            }
            
            message.setRequst("POST");
            history.add(message);
            System.out.println("Get Message from " + message);
        } catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }
    private void doDelete(HttpExchange httpExchange) { 	
    	String query = httpExchange.getRequestURI().getQuery();
    	if(query != null) {
    		 Map<String, String> map = queryToMap(query);
    		 String token = map.get("token");
    		 int index = -1;
    		 if (token != null && !"".equals(token)) {
                  index = messageExchange.getIndex(token);
    		 }
    		 if(index != -1) {
    		 Message message = history.get(index);
    		 message.setRequst("DELETE");
    		 String messageForDelete = message.getText();
    		 message.deleteMessage();
    		 System.out.println("Delete Message : " + message.getNameUser() + " : " + messageForDelete);
    		 }
    	}    		 
    }
    private void doPut(HttpExchange httpExchange) {
    try {
    	Message messageChange = messageExchange.getClientMessage(httpExchange.getRequestBody());
    	int idOfChangeMessage =  messageChange.getID();
    	if(idOfChangeMessage >= 0 && idOfChangeMessage < history.size()) {
    	   if(messageChange.isDelete() == false) {
    		Message infoMessage = history.get(idOfChangeMessage);
    		infoMessage.setText(messageChange.getText());
    		infoMessage.setChange(true);
    		infoMessage.setRequst("PUT");
    		System.out.println("Chnage :"+infoMessage.toJSONString());
    	    }
    	   }
        } catch (ParseException e) {
          System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }	
    }
    private void sendResponse(HttpExchange httpExchange, String response) {
        try {
            byte[] bytes = response.getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin","*");
            if("OPTIONS".equals(httpExchange.getRequestMethod())) {
                headers.add("Access-Control-Allow-Methods","PUT, DELETE, POST, GET, OPTIONS");
            }
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write( bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}