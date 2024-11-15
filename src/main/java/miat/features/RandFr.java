package miat.features;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandFr {
    public static String randomFriend() {
        String[] unwantedResult = new String[]{"Alisa_Southerncross" , "Aliyan", "Category:Real_Animal_Friends", "Category:Cryptid_Friends",
                "Category:EX_Friends", "Category:Crossover_Friends", "Chestnut_Horse", "Chibi_Kumamon", "Coco", "Crunchyroll-Hime",
                "Debiru-sama", "De_Brazza's_Monkey", "Dororo", "Draco_Centauros", "Beast_King_Elephant", "Elephant_(Beast_King)", "Gachapin", "Giraffe_(Beast_King)",
                "Beast_King_Giraffe", "Giroro", "Beast_King_Gorilla", "Gorilla_(Beast_King)", "HAW-206", "Hello_Kitty_Serval", "Hello_Mimmy_Serval",
                "Hi-no-Tori", "Higejii", "Higumamon", "Beast_King_Hippopotamus", "Hippopotamus_(Beast_King)", "Keroro", "Kururu", "Leo",
                "Beast_King_Lion", "Lion_(Beast_King)", "Logikoma", "Beast_King_Mongoose", "Mongoose_(Beast_King)", "Mukku", "Beast_King_Old_World_Vulture",
                "Old_World_Vulture_(Beast_King)", "Beast_King_Ostrich", "Ostrich_(Beast_King)", "Palcoarai-san", "Palcoarai-san2", "Rabbit_Yukine", "Seal_Brown_Horse",
                "Slow_Loris", "Tachikoma_Type-A", "Tachikoma_Type-B", "Tachikoma_Type-C", "Tachikoma_Type-H",
                "Tachikoma_Type-S", "Tamama", "Tommy", "Uchikoma", "Unico", "Valcoara", "White_Horse", "White_Ezo_Red_Fox", "Beast_King_Wildebeest",
                "Wildebeest_(Beast_King)", "Witch", "Beast_King_Zebra", "Zebra_(Beast_King)"};
        String responseContent = null;
        try {
            URL url = new URL("https://japari-library.com/wiki/Special:RandomInCategory/Friends");
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
            Request request = new Request.Builder().url(url).build();

            Response resp = client.newCall(request).execute();
            if (resp.body() != null) {
                responseContent = resp.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseContent != null) {
            Pattern pattern = Pattern.compile("\"wgPageName\":\"([^\"]+)");
            Matcher matcher = pattern.matcher(responseContent);
            if (matcher.find()) {
                String pageName = matcher.group(1);
                if (Arrays.asList(unwantedResult).contains(pageName)) {        //checks for unwanted results.
                    System.out.println("Unwanted result.");                    //post to console if there is an unwanted result that gets re-queried.
                    return randomFriend();
                }                                    //restart the page query process.
                String urlLocation = "https://japari-library.com/wiki/" + pageName;        //the site as far down as you can go to the page.
                return urlLocation.replace(" ", "_");            //any remaining spaces get replaced with underscores.
            } else {
                return "Failed to get a page.";
            }
        } else {
            return "Failed to get a page.";
        }
    }
}
