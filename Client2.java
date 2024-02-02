import java.io.InputStreamReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Client2 {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Client2 <server_url>");
            System.exit(1);
        }

        String serverUrl = args[0];

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(serverUrl);

            System.out.println("Executing request " + request.getRequestLine());
            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Code: " + statusCode);

                if (statusCode == 200) {
                    Header contentTypeHeader = response.getFirstHeader("Content-Type");
                    if (contentTypeHeader != null && contentTypeHeader.getValue().startsWith("application/json")) {
                        InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());

                        JsonReader reader = Json.createReader(isr);
                        JsonObject jsonObject = reader.readObject();

                        System.out.println("JSON Response:");
                        System.out.println(jsonObject.toString());

                        reader.close();
                        isr.close();
                    } else {
                        System.out.println("La réponse n'est pas du JSON. Contenu de la réponse :");
                        System.out.println(EntityUtils.toString(response.getEntity()));
                    }
                } else {
                    System.out.println("La requête n'a pas abouti avec le code : " + statusCode);
                }
            }

        } catch (Exception e) {
            // Gérer l'exception de manière appropriée (par exemple, journalisation, affichage d'un message d'erreur, etc.)
            e.printStackTrace();
        }
    }
}

