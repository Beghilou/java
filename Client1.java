import java.io.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javax.json.*;

public class Client1 {

    public static void main(String[] args) {
        // Q1.2 - Vérifier si des arguments ont été fournis en ligne de commande
        if (args.length == 0) {
            System.out.println("Usage: java Client1 <server_url>");
            System.exit(1);
        }

        // Récupérer le premier argument comme URL du serveur
        String serverUrl = args[0];

        // Q1.3 - Créer le client HTTP
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Créer la requête HTTP "GET"
            String url = serverUrl.startsWith("http://") ? serverUrl : "http://" + serverUrl;
            HttpGet request = new HttpGet(url);

            // Exécuter la requête HTTP et obtenir la réponse
            System.out.println("Executing request " + request.getRequestLine());
            try (CloseableHttpResponse response = client.execute(request)) {
                // Afficher la réponse du serveur
                System.out.println("Response Line: " + response.getStatusLine());
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                // Récupérer la page envoyée par le serveur dans une chaîne
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                    result.append("\n"); // pour avoir le saut de ligne
                }

                // Afficher la page à l'écran
                String page = result.toString();
                System.out.println(page);

                // Le reste du code pour traiter la réponse peut être ajouté ici
                // ...
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

