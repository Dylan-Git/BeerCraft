package com.kaupenjoe.beercraft.util;

import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class JsonBlockstateGenerator
{
    public static void CreateJSON()
    {
        JsonObject jsonObject = new JsonObject();
        JsonObject variants = new JsonObject();
        List<JsonObject> models = new ArrayList<JsonObject>();

        jsonObject.add("variants", variants);

        JsonObject model = new JsonObject();
        model.addProperty("model", "beercraft:block/pipes/fluid_pipe");
        variants.add("neighbours=" + 0, model);

        for(int i = 1; i < 64; i++)
        {
            model = new JsonObject();
            model.addProperty("model", "beercraft:block/pipes/" + getString(i));
            variants.add("neighbours=" + i, model);
        }

        //Write JSON file
        try (FileWriter file = new FileWriter("generatedModelFile2.json")) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(jsonObject.toString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(int id)
    {
        String s = "fluid_pipe_";

        if((id & 4) == 4) // NORTH ACTIVE
            s += "north";

        if((id & 32) == 32) // EAST ACTIVE
            s += s.endsWith("north") ? "_east" : "east";

        if((id & 8) == 8) // SOUTH ACTIVE
            s += s.endsWith("north") || s.endsWith("east") ? "_south" : "south";

        if((id & 16) == 16) // WEST ACTIVE
            s += s.endsWith("north") || s.endsWith("east") || s.endsWith("south") ? "_west" : "west";

        return s;
    }

}
