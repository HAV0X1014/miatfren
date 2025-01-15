package miat.features;

import miat.filehandler.ConfigHandler;
import miat.filehandler.Reader;
import miat.util.TypeContinuously;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KF3Summary {
    public static void friendStory(String friendId, MessageReceivedEvent mc) {
        try (TypeContinuously tc = new TypeContinuously(mc.getMessage().getChannel())) {
            Map<Integer, JSONObject> idMap = new HashMap<>();
            Map<String, JSONObject> nameMap = new HashMap<>();
            JSONArray charaData = new JSONArray(Reader.readFull("CHARA_DATA.json"));

            // Populate the map with data from the chara data list
            for (int i = 0; i < charaData.length(); i++) {
                JSONObject jsonObject = charaData.getJSONObject(i);
                Integer friendID = jsonObject.getInt("id");
                idMap.put(friendID, jsonObject);
                String name = jsonObject.getString("name");
                nameMap.put(name, jsonObject);
            }

            int idUnformattedInt = Integer.parseInt(friendId);
            String id = String.format("%04d", idUnformattedInt);
            File folder = new File("char/");
            File[] files = folder.listFiles((dir, name) -> name.startsWith("scenario_c_" + id + "_") && name.endsWith(".prefab.json"));

            String nameEn = "NO-MATCH";
            //look up the id of the character in the Map and match that to their respective name and english name
            for (Object friend : idMap.keySet().toArray()) {
                JSONObject result = idMap.get(Integer.parseInt(friend.toString()));
                if (result.getInt("id") == Integer.parseInt(id)) {
                    int friendID = result.getInt("id");
                    nameEn = result.getString("nameEn");
                    System.out.println("ID: " + friendID + ", NameEN: " + nameEn);
                }
            }


            Arrays.sort(files, Comparator.naturalOrder());
            int half = files.length;
            int stoppingPoint = half;                                   //"half" does not mean division by two.
            for (int iter = 0; iter < 1; iter++) {
                JSONArray charaNames = new JSONArray();                 //holds all of the character names used + duplicates
                int startingPoint = 0;
                if (iter > 0) {
                    //if we have already iterated once, start from the old stopping point
                    startingPoint = stoppingPoint;
                    stoppingPoint = stoppingPoint + half;
                }
                //this reads through all of the scenario files in the allotted space we have (from starting point to stopping point)
                //and adds the contents of each to an array that contains all of the read-through scenarios
                JSONArray allScenarios = new JSONArray();
                for (int f = startingPoint; f < stoppingPoint; f++) {
                    File file = files[f];
                    JSONObject scenario = new JSONObject(Reader.readFull("char/" + file.getName()));
                    allScenarios.put(scenario);
                }
                //this loop gets the character names from the currently available scenarios and puts them in charaName
                for (int s = 0; s < allScenarios.length(); s++) {
                    JSONObject scenario = allScenarios.getJSONObject(s);
                    for (int i = 0; i < scenario.getJSONArray("charaDatas").length(); i++) {
                        charaNames.put(scenario.getJSONArray("charaDatas").getJSONObject(i).getString("name"));
                    }
                }
                //remove duplicates from the list of charaNames and also remove the brackets from that string
                StringBuilder names = new StringBuilder();
                List<Object> charaNamesList = charaNames.toList().stream().distinct().collect(Collectors.toList());

                //look up the name of the character in the Map and match that to their respective friend ID and english name
                for (Object name : charaNamesList.toArray()) {
                    JSONObject result = nameMap.get(name.toString());
                    if (result != null) {
                        String nameEnUsed = result.getString("nameEn");
                        names.append(name).append("|").append(nameEnUsed).append(", ");
                        //format is "nameJP|nameEN,"
                    }
                }

                StringBuilder fullScene = new StringBuilder();
                String dialog = null;
                for (int i = 0; i < allScenarios.length(); i++) {
                    JSONObject scenarioData = allScenarios.getJSONObject(i);
                    JSONArray rowDatas = scenarioData.getJSONArray("rowDatas");
                    for (Object rowData : rowDatas) {
                        JSONObject jsonObject = (JSONObject) rowData;
                        if (jsonObject.has("mSerifCharaName")) {
                            String charName = jsonObject.getString("mSerifCharaName");
                            if (!charName.isEmpty()) {
                                JSONArray dialogArray = jsonObject.getJSONArray("mStrParams");
                                for (Object sentence : dialogArray) {
                                    if (!sentence.toString().equals("none")) {
                                        if (!sentence.toString().isEmpty()) {
                                            String cleanedSentence = sentence.toString().replaceAll("<.*?>", "");
                                            //if the name we are looking up (the current speaking character's name) ISNT in
                                            //the name map, then dont add it to the dialog pair. (cellien, human characters, etc)
                                            JSONObject result = nameMap.get(charName);
                                            if (result == null) {
                                                dialog = charName + ": " + cleanedSentence + "\n";
                                            } else {
                                                dialog = charName + "|" + result.getString("nameEn") + ": " + cleanedSentence + "\n";
                                            }
                                        }
                                    }
                                }
                                fullScene.append(dialog).append("\n");
                            }
                        }
                    }
                }

                System.out.println("---");
                System.out.println("Summarizing " + id + "...");
                //String translatedNames = Translate.translator(names);   //translate the names with google translate first UNUSED
                String mainCharacterName = "";
                JSONObject tempObj = idMap.get(Integer.parseInt(id));
                if (tempObj != null) {
                    String tempName = tempObj.getString("nameEn");
                    if (tempName != null) {
                        mainCharacterName = tempName;
                    }
                }
                String summarizedScene = send(fullScene.toString(), mainCharacterName);  //send the scenarios to the AI
                System.out.println(summarizedScene);

                //now write the summary into a file
                String filename = nameEn + "_char_" + id + ".txt";
                String finalSummary = summarizedScene + "\n[End of Summary.]\n[Involved Characters - " + names.toString() + "]";
                InputStream inputStream = new ByteArrayInputStream(finalSummary.getBytes());

                mc.getMessage().getChannel().sendMessage("Summarized character story.").addFiles(FileUpload.fromData(inputStream, filename)).queue();
        }
        }
    }
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static String send(String dialog, String mainCharacter) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
        JSONObject payload = new JSONObject();          //holds the whole object of JSON sent to google
        JSONArray contents = new JSONArray();           //holds the text content sent to the AI
        //how gemini works is that the turns HAVE to be user -> model -> user -> model -> user and ALWAYS start and end with user
        //its really stupid, and making the JSON for this is a pain in the ass.

        String prompt = "Create a complete summary for the following story: \n" +
                "- Respond only in English.\n" +
                "- The summary should be 5-8 paragraphs in length, and include all major events.\n" +
                "- Be thorough describing the events.\n" +
                "- Specify which characters are involved in each event.\n" +
                "- Be specific about the tone of the characters. Describe their expressions and tone in detail.\n" +
                "- This story happens in the main story of Kemono Friends 3, where human girls with animal features go on adventures, and fight against Celliens.\n";
        /*
        //this would have been for a "system" prompt but apparently gemini doesnt have that.
        //not going to delete this but instead i shall comment it out.
        JSONObject modelPrompt = new JSONObject();
        modelPrompt.put("role", "model");
        JSONArray parts = new JSONArray();
        JSONObject text = new JSONObject();
        text.put("text","Answer the request.");
        parts.put(text);
        modelPrompt.put("parts", parts);
        contents.put(modelPrompt);
        */

        JSONObject userPrompt = new JSONObject();
        userPrompt.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        userText.put("text", prompt + "\n\n" + dialog + "\n\nSummarize the story, and respond in English.");

        userParts.put(userText);
        userPrompt.put("parts", userParts);
        contents.put(userPrompt);
        //this hunk of shit puts the text into googles special little array with an object inside or something
        //i hate google's stupid fucking API formatting

        JSONArray safetySettings = new JSONArray();
        JSONObject catHar = new JSONObject();
        catHar.put("category", "HARM_CATEGORY_HARASSMENT");
        catHar.put("threshold", "BLOCK_NONE");

        JSONObject catHtSp = new JSONObject();
        catHtSp.put("category", "HARM_CATEGORY_HATE_SPEECH");
        catHtSp.put("threshold", "BLOCK_NONE");

        JSONObject catSxExp = new JSONObject();
        catSxExp.put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT");
        catSxExp.put("threshold", "BLOCK_NONE");

        JSONObject catDngCnt = new JSONObject();
        catDngCnt.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        catDngCnt.put("threshold", "BLOCK_NONE");

        safetySettings.put(catHar);
        safetySettings.put(catHtSp);
        safetySettings.put(catSxExp);
        safetySettings.put(catDngCnt);

        payload.put("safetySettings", safetySettings);
        payload.put("contents",contents);

        RequestBody requestBody = RequestBody.create(JSON, payload.toString());
        String output;
        String responseContent = "";

        try {
            //to change model, change the gemini-1.5-flash-latest thing to whichever model you want.
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + ConfigHandler.getString("GeminiKey"));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            try (Response resp = client.newCall(request).execute()) {
                responseContent = resp.body().string();
            }
            //you know what i hate more than googles INPUT json?????? OUTPUT JSON!!!!
            JSONObject response = new JSONObject(responseContent);
            output = response.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
        } catch (Exception e) {
            System.out.println("[Gemini API error.]\n" + responseContent);
            throw new RuntimeException(e);
        }
        return output;
    }
}
