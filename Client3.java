import java.io.BufferedReader;
import java.io.IOException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
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

public class Client3 {

    public static void main(String[] args) {
        try (CloseableHttpClient client = HttpClients.createDefault();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                // Saisie du titre au clavier
                System.out.print("Entrez le titre du film (ou 'exit' pour quitter) : ");
                String title = reader.readLine();

                if ("exit".equalsIgnoreCase(title)) {
                    System.out.println("Programme terminé.");
                    break;
                }

                // Construction de l'URL avec le titre fourni
                String apiUrl = "http://www.omdbapi.com/?apikey=751ea6aa&t=" + title;

                // Création de la requête HTTP
                HttpGet request = new HttpGet(apiUrl);

                System.out.println("Executing request " + request.getRequestLine());
                try (CloseableHttpResponse response = client.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    System.out.println("Response Code: " + statusCode);

                    if (statusCode == 200) {
                        Header contentTypeHeader = response.getFirstHeader("Content-Type");
                        if (contentTypeHeader != null && contentTypeHeader.getValue().startsWith("application/json")) {
                            InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());

                            // Créer le lecteur JSON et l'objet JSON
                            JsonReader jsonReader = Json.createReader(isr);
                            JsonObject jsonObject = jsonReader.readObject();

                            if (jsonObject.containsKey("Error")) {
                                // Film non trouvé
                                System.out.println("Erreur : Film non trouvé. Message : " + jsonObject.getString("Error"));
                            } else {
                                // Afficher les informations essentielles
                                System.out.println("Titre : " + jsonObject.getString("Title"));
                                System.out.println("Année de sortie : " + jsonObject.getString("Year"));

                                // Afficher la date de sortie et les acteurs principaux si disponibles
                                if (jsonObject.containsKey("Released")) {
                                    System.out.println("Date de sortie : " + jsonObject.getString("Released"));
                                }

                                if (jsonObject.containsKey("Actors")) {
                                    System.out.println("Acteurs principaux : " + jsonObject.getString("Actors"));
                                }

                                // Ajouter la mention suivant le score des critiques de Rotten Tomatoes
                                if (jsonObject.containsKey("Ratings")) {
                                    int rottenTomatoesScore = getRottenTomatoesScore(jsonObject.getJsonArray("Ratings"));
                                    System.out.println("Score Rotten Tomatoes : " + rottenTomatoesScore);

                                    // Ajouter la mention
                                    if (rottenTomatoesScore < 20) {
                                        System.out.println("Mention : Nul");
                                    } else if (rottenTomatoesScore <= 50) {
                                        System.out.println("Mention : Bof");
                                    } else if (rottenTomatoesScore <= 70) {
                                        System.out.println("Mention : Bien");
                                    } else {
                                        System.out.println("Mention : Très bien");
                                    }
                                }
                            }

                            // Fermer le lecteur JSON et le flux
                            jsonReader.close();
                            isr.close();
                        } else {
                            System.out.println("La réponse n'est pas du JSON. Contenu de la réponse :");
                            System.out.println(EntityUtils.toString(response.getEntity()));
                        }
                    } else {
                        System.out.println("La requête n'a pas abouti avec le code : " + statusCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getRottenTomatoesScore(JsonArray ratings) {
        for (JsonValue rating : ratings) {
            JsonObject ratingObject = (JsonObject) rating;
            if ("Rotten Tomatoes".equals(ratingObject.getString("Source"))) {
                String scoreStr = ratingObject.getString("Value");
                return Integer.parseInt(scoreStr.replaceAll("\\D+", ""));
            }
        }
        return 0; // Retourne 0 si le score n'est pas trouvé
    }
}
