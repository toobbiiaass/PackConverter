package org.example;

import org.example.Objects.Item;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;




public class Main {

    private static Scanner scanner = new Scanner(System.in);

    private static String oldTpPath="";
    private static String outputPathTP = "";
    private static String destinationFolder = "";
    private static String futurePackName ="";
    private static int packFormat =0;
    private static int futurePackFormat =0;
    private static boolean shouldNetheriteCreate = false;
    public static final String GREEN = "\u001B[32m";
    public static final String PURPLE = "\u001B[35m";
    public static final String RESET = "\u001B[0m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String LIGHT_BLUE = "\u001B[94m";




    static String[] foldersInTextures = {"block","misc","item","models","particle"};

    public static void main(String[] args) {
        userInformation();

        int packformat= userInput();
        createBasics();
        checkPackFormatAndStartConverting(packformat);
        printBlues("Pack was converted!");
        String doney= scanner.nextLine();
        System.out.println("done");

    }
    private static void userInformation(){
        AnsiConsole.systemInstall();
        System.out.println(LIGHT_BLUE +
                "+-----------------------------------------------------------------------------------------------------------------+");

        System.out.println("|                                             Minecraft Pack Converter                                            |");
        System.out.println("|-----------------------------------------------------------------------------------------------------------------|");
        System.out.println("| BY:" + PURPLE + " vuacy" + LIGHT_BLUE + "                                                                                                       |");
        System.out.println("| Version: 1.1                                                                                                    |");
        System.out.println("| Youtube Tutorial: "+PURPLE+"https://www.youtube.com/watch?v=J_RNlLS4k3w"+LIGHT_BLUE+"                                                   |");
        System.out.println("| Discord Server: " + PURPLE + "https://discord.gg/ExGSqUT6qk" + LIGHT_BLUE + "                                                                   |");
        System.out.println("|                                                                                                                 |");
        System.out.println("| IMPORTANT:                                                                                                      |");
        System.out.println("| Please change the name of the pack so that the pack does not contain any special characters/spaces!             |");
        System.out.println("| Type 'info' to see what the converter can do                                                                    |");
        System.out.println("| Press Enter to continue!                                                                                        |");
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+" + RESET);
        boolean isLoop = true;
        while (isLoop){

            String infoIg = scanner.nextLine();
            if(infoIg.contains("info")){
                showInfo();
            }else{
                isLoop = false;
            }
        }
    }
    private static void showInfo(){
        System.out.println(BRIGHT_BLUE +
                "+-----------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                        INFO                                                     |");
        System.out.println("| The PackConverter can convert packs from 1.8 - 1.20 into 1.20 or 1.21.4                                         |");
        System.out.println("| It automatically adds Netherite armor if needed (only for packs below 1.9)                                      |");
        System.out.println("| It renames files inside the pack to match the naming requirements of the higher version                         |");
        System.out.println("| It extracts all important textures from icons/particles/widgets                                                 |");
        System.out.println("|                                                                                                                 |");
        System.out.println("| Type 'info' to see what the converter can do                                                                    |");
        System.out.println("| Press Enter to continue!                                                                                        |");
        System.out.println("+-----------------------------------------------------------------------------------------------------------------+" + RESET);

    }
    private static int userInput(){
        printBlues("Choose a path where the converted tp should be saved!");
        outputPathTP = scanner.nextLine();

        printBlues("\nSpecify the folder that should be converter!\nThe pack must be unpacked (directory which contains the pack.mcmeta file) \n...");
        oldTpPath = scanner.nextLine();

        packFormat = checkMetaFile();
        if(packFormat == -1){
            printBlues("Packformat not found!");
            printBlues("Please enter the version of the pack! Anything below 1.9 please enter '1' If it is a higher version please enter '5'");
            String apackformat = scanner.nextLine();
            packFormat = Integer.valueOf(apackformat);
        }
        String versionNum = "> 1.8.9";
        if(packFormat >1){
            versionNum = "< 1.15";
        }
        printBlues("Your Pack is "+versionNum+"!");
        if(packFormat == 1) {
            printBlues("Do you want your pack for 1.20 or 1.21.4? If you want the pack for 1.20 write '120' else just press ENTER");
            String whichVersionUserWants = scanner.nextLine();
            if(whichVersionUserWants.equals("120")){
                futurePackFormat = 15;
            }else{
                futurePackFormat= 46;
            }
        }
        while (true){
            printBlues("Specify the name of the future tp:");
            futurePackName = scanner.nextLine();

            Path newFolderPath = Paths.get(outputPathTP, futurePackName);
            File newFolder = new File(newFolderPath.toUri());

            if(newFolder.exists()){
                printBlues("This name already exists!");
                return -2;
            }else {
                newFolder.mkdir();
                printWorkedShit(futurePackName+" folder was created!");
                destinationFolder = String.valueOf(newFolderPath);
                break;
            }
        }
        return packFormat;

    }
    private static int checkMetaFile(){
        Path packMetaPath = Paths.get(oldTpPath, "pack.mcmeta");

        if (Files.exists(packMetaPath)) {
            try (FileChannel channel = FileChannel.open(packMetaPath, StandardOpenOption.READ)) {
                // Datei lesen
                String content = new String(Files.readAllBytes(packMetaPath), Charset.forName("UTF-8")).trim();

                // BOM entfernen, falls vorhanden
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);
                }

                // JSON parsen
                JSONObject json = new JSONObject(content);
                if (json.has("pack") && json.getJSONObject("pack").has("pack_format")) {
                    return json.getJSONObject("pack").getInt("pack_format");
                } else {
                    System.out.println("Diese Datei hat keinen 'pack_format'-Wert.");
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Lesen der Datei (möglicherweise in Benutzung): " + e.getMessage());
            } catch (org.json.JSONException e) {
                System.err.println("Fehler beim Parsen der JSON-Eingabe: " + e.getMessage());
            }
        } else {
            System.out.println("Die Datei 'pack.mcmeta' existiert nicht.");
        }
        return -1;
    }
    private static void createBasics(){
        createPackMcMeta();
        copyPackImage();
    }

    private static void moveDircs(){
        Path oldPathBase = Paths.get(oldTpPath, "assets","minecraft");
        Path newPathBase = Paths.get(destinationFolder,"assets","minecraft");
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","environment").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","environment").toUri()));
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","font").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","font").toUri()));
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","misc").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","misc").toUri()));
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","models","armor").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","models","armor").toUri()));
        System.out.println("Do you want your Sky for optifine or fabric? (o/f)");
        String input = scanner.nextLine();
        if(input.equals("f")){
            moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"mcpatcher").toUri()),new File(Paths.get(String.valueOf(newPathBase),"fabricskyboxes").toUri()));

        }else{
            moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"mcpatcher").toUri()),new File(Paths.get(String.valueOf(newPathBase),"optifine").toUri()));

        }
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","blocks").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","block").toUri()));
        moveDirc(new File(Paths.get(String.valueOf(oldPathBase),"textures","items").toUri()),new File(Paths.get(String.valueOf(newPathBase),"textures","item").toUri()));
    }
    private static void moveDirc(File oldPath, File newPath){
        if(oldPath.exists()){
            try {
                Files.move(oldPath.toPath(), newPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                printWorkedShit("moving path");
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            System.err.println(oldPath+" dont exist");
        }
    }
    private static void createPackMcMeta(){
        Path packMCMetaPath = Paths.get(destinationFolder, "pack.mcmeta");
        File packMCMeta = new File(packMCMetaPath.toUri());

        try {
            packMCMeta.createNewFile();
            try {
                FileWriter myWriter = new FileWriter(destinationFolder+"\\pack.mcmeta");
                myWriter.write("{\n");
                myWriter.write("  \"pack\": {\n");
                myWriter.write(" \"pack_format\": "+futurePackFormat+",\n");
                myWriter.write("\"description\": \"\\u00A7bconverted by vuacy tool\"\n");
                myWriter.write("  }\n");
                myWriter.write("}\n");
                myWriter.close();
                printWorkedShit("pack.mcmeta was created");
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void copyPackImage(){
        String packString = "pack.png";
        Path packPNGPath = Paths.get(oldTpPath, packString);
        File packPNG = new File(packPNGPath.toUri());
        if(packPNG.exists()){
            try {
                Path packPNGPathNew = Paths.get(destinationFolder, packString);
                File packPNGNew = new File(packPNGPathNew.toUri());
                if(packPNG.renameTo(packPNGNew)) {
                    printWorkedShit(packString+ " got moved!");
                } else {
                    System.err.println(packString +" failed to move!");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private static void createAllFolders(){
        Path assetsPath = Paths.get(destinationFolder, "assets");
        File assetsFile = new File(assetsPath.toUri());
        assetsFile.mkdir();
        printWorkedShit("assets folder created");

        Path minecraftPath = Paths.get(String.valueOf(assetsPath), "minecraft");
        File minecraftFile = new File(minecraftPath.toUri());
        minecraftFile.mkdir();
        printWorkedShit("minecraft folder created");

        Path texturesPath = Paths.get(String.valueOf(minecraftPath), "textures");
        File newTextures = new File(texturesPath.toUri());
        newTextures.mkdir();
        printWorkedShit("textures folder created");

        for (int i = 0; i < foldersInTextures.length; i++) {
            Path filePath = Paths.get(String.valueOf(texturesPath), foldersInTextures[i]);
            File newFolderInTextures = new File(filePath.toUri());
            printWorkedShit(foldersInTextures[i]+" folder created");
            newFolderInTextures.mkdir();

            if(foldersInTextures[i].equals("models")){
                Path newModelsArmorPath = Paths.get(String.valueOf(filePath), "armor");
                File newModelsArmor = new File(newModelsArmorPath.toUri());
                newModelsArmor.mkdir();
                printWorkedShit("armor folder created");
            }
        }
        Path guiPath = Paths.get(String.valueOf(texturesPath), "gui");
        File newGui = new File(guiPath.toUri());
        newGui.mkdir();
        printWorkedShit("gui folder created");

        Path newGuiAdvancePath = Paths.get(String.valueOf(guiPath), "advancements");
        File newGuiAdvance = new File(newGuiAdvancePath.toUri());
        newGuiAdvance.mkdir();
        printWorkedShit("advancements folder created");

        Path spritesPath = Paths.get(String.valueOf(guiPath), "sprites");
        File newSprites = new File(spritesPath.toUri());
        newSprites.mkdir();
        printWorkedShit("sprites folder created");

        Path hudPath = Paths.get(String.valueOf(spritesPath), "hud");
        File newhud = new File(hudPath.toUri());
        newhud.mkdir();
        printWorkedShit("hud folder created");

        Path heartPath = Paths.get(String.valueOf(hudPath), "heart");
        File newHeart = new File(heartPath.toUri());
        newHeart.mkdir();
        printWorkedShit("heart folder created");

        Path entityPath = Paths.get(String.valueOf(texturesPath), "entity");
        File newEntity = new File(entityPath.toUri());
        newEntity.mkdir();
        printWorkedShit("entity folder created");

        Path equipmentPath = Paths.get(String.valueOf(entityPath), "equipment");
        File newEquipment = new File(equipmentPath.toUri());
        newEquipment.mkdir();
        printWorkedShit("equipment folder created");

        Path humanoidPath = Paths.get(String.valueOf(equipmentPath), "humanoid");
        File newHumanoid = new File(humanoidPath.toUri());
        newHumanoid.mkdir();
        printWorkedShit("humanoid folder created");

        Path humanoid_leggingsPath = Paths.get(String.valueOf(equipmentPath), "humanoid_leggings");
        File newHumanoid_leggings = new File(humanoid_leggingsPath.toUri());
        newHumanoid_leggings.mkdir();
        printWorkedShit("humanoid folder created");
    }

    private static void createAllFoldersForHigherVersion(){
        Path assetsPath = Paths.get(destinationFolder, "assets");

        Path minecraftPath = Paths.get(String.valueOf(assetsPath), "minecraft");

        Path texturesPath = Paths.get(String.valueOf(minecraftPath), "textures");

        for (int i = 0; i < foldersInTextures.length; i++) {
            Path filePath = Paths.get(String.valueOf(texturesPath), foldersInTextures[i]);
            File newFolderInTextures = new File(filePath.toUri());
            printWorkedShit(foldersInTextures[i]+" folder created");
            newFolderInTextures.mkdir();

            if(foldersInTextures[i].equals("models")){
                Path newModelsArmorPath = Paths.get(String.valueOf(filePath), "armor");
                File newModelsArmor = new File(newModelsArmorPath.toUri());
                newModelsArmor.mkdir();
                printWorkedShit("armor folder created");
            }
        }

        Path guiPath = Paths.get(String.valueOf(texturesPath), "gui");
        File newGui = new File(guiPath.toUri());
        newGui.mkdir();
        printWorkedShit("gui folder created");

        Path newGuiAdvancePath = Paths.get(String.valueOf(guiPath), "advancements");
        File newGuiAdvance = new File(newGuiAdvancePath.toUri());
        newGuiAdvance.mkdir();
        printWorkedShit("advancements folder created");

        Path spritesPath = Paths.get(String.valueOf(guiPath), "sprites");
        File newSprites = new File(spritesPath.toUri());
        newSprites.mkdir();
        printWorkedShit("sprites folder created");

        Path hudPath = Paths.get(String.valueOf(spritesPath), "hud");
        File newhud = new File(hudPath.toUri());
        newhud.mkdir();
        printWorkedShit("hud folder created");

        Path heartPath = Paths.get(String.valueOf(hudPath), "heart");
        File newHeart = new File(heartPath.toUri());
        newHeart.mkdir();
        printWorkedShit("heart folder created");

        Path entityPath = Paths.get(String.valueOf(texturesPath), "entity");
        File newEntity = new File(entityPath.toUri());
        newEntity.mkdir();
        printWorkedShit("entity folder created");

        Path equipmentPath = Paths.get(String.valueOf(entityPath), "equipment");
        File newEquipment = new File(equipmentPath.toUri());
        newEquipment.mkdir();
        printWorkedShit("equipment folder created");

        Path humanoidPath = Paths.get(String.valueOf(equipmentPath), "humanoid");
        File newHumanoid = new File(humanoidPath.toUri());
        newHumanoid.mkdir();
        printWorkedShit("humanoid folder created");

        Path humanoid_leggingsPath = Paths.get(String.valueOf(equipmentPath), "humanoid_leggings");
        File newHumanoid_leggings = new File(humanoid_leggingsPath.toUri());
        newHumanoid_leggings.mkdir();
        printWorkedShit("humanoid folder created");
    }
    private static void checkPackFormatAndStartConverting(int packformat){
        if(packformat == 1){
            //1.8
            createAllFolders();
            moveDircs();

            refactorPF1ToPF5();
            createAllNetheriteItems();

            if(futurePackFormat == 46){
                refactorPF5ToPF46(false);
            }else{
                Path oldPathBase = Paths.get(oldTpPath, "assets","minecraft","textures","gui");
                Path newPathBase = Paths.get(destinationFolder,"assets","minecraft","textures","gui");

                File oldItem = new File(oldPathBase.toFile(),"icons.png");
                File newItem = new File(newPathBase.toFile(),"icons.png");
                oldItem.renameTo(newItem);

                File oldItem2 = new File(oldPathBase.toFile(),"widgets.png");
                File newItem2 = new File(newPathBase.toFile(),"widgets.png");
                oldItem2.renameTo(newItem2);
            }

        }
        else if(packformat >= 5){
            //1.15
            Path oldPathBase = Paths.get(oldTpPath, "assets");
            Path newPathBase = Paths.get(destinationFolder,"assets");
            moveDirc(new File(Paths.get(String.valueOf(oldPathBase)).toUri()),new File(Paths.get(String.valueOf(newPathBase)).toUri()));
            createAllFoldersForHigherVersion();

            refactorPF5ToPF46(true);
        }
        else{
            System.err.println("This Converter doesnt support this version!");
            return;
        }
    }

    private static void refactorPF1ToPF5() {

        ArrayList<ArrayList<Item>> oldFullList = getOnePackFormatLowerVersion("packformat1.json");
        ArrayList<ArrayList<Item>> newFullList = getOnePackFormatLowerVersion("packformat5.json");

        for (int i = 0; i < oldFullList.size(); i++) {
            if (i == 0) { //particles
                Path particlePath = getPathOfItem(oldTpPath,oldFullList.get(i).get(0));
                File particlesImage = new File(Paths.get(String.valueOf(particlePath),oldFullList.get(i).get(0).getName()).toUri());

                Path destinationPathParticles = getPathOfItem(destinationFolder,oldFullList.get(i).get(0));
                objcutter(newFullList.get(i),particlesImage,destinationPathParticles);
            } else {
                for (int j = 0; j < oldFullList.get(i).size(); j++) {
                    Path newPathBase = getPathOfItem(destinationFolder, newFullList.get(i).get(j));
                    File oldItem = new File(Paths.get(String.valueOf(newPathBase), oldFullList.get(i).get(j).getName()).toUri());
                    File newItem = new File(Paths.get(String.valueOf(newPathBase), newFullList.get(i).get(j).getName()).toUri());
                    if (oldItem.exists()) {
                        oldItem.renameTo(newItem);
                    }
                    //MCMETA DATEI WENN VORHANDEN
                    File oldItemMeta = new File(Paths.get(String.valueOf(newPathBase), oldFullList.get(i).get(j).getName()+".mcmeta").toUri());
                    File newItemMeta = new File(Paths.get(String.valueOf(newPathBase),newFullList.get(i).get(j).getName()+".mcmeta").toUri());
                    if (oldItemMeta.exists()) {
                        oldItemMeta.renameTo(newItemMeta);
                    }
                }
            }
        }
    }

    private static void createAllNetheriteItems(){
        Path itemPath = Paths.get(destinationFolder, "assets","minecraft","textures","item");
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_sword.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_sword.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_axe.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_axe.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_hoe.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_hoe.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_shovel.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_shovel.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_pickaxe.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_pickaxe.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_boots.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_boots.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_chestplate.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_chestplate.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_helmet.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_helmet.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(itemPath),"diamond_leggings.png").toFile(), Paths.get(String.valueOf(itemPath),"netherite_leggings.png").toFile());

        Path armorPath = Paths.get(destinationFolder, "assets","minecraft","textures","models","armor");
        createNetheriteItem(Paths.get(String.valueOf(armorPath),"diamond_layer_1.png").toFile(), Paths.get(String.valueOf(armorPath),"netherite_layer_1.png").toFile());
        createNetheriteItem(Paths.get(String.valueOf(armorPath),"diamond_layer_2.png").toFile(), Paths.get(String.valueOf(armorPath),"netherite_layer_2.png").toFile());
    }

    private static void refactorPF5ToPF46(boolean isNewerVersion){
        ArrayList<ArrayList<Item>> oldFullList = getOnePackFormatHigherVersion("packformat5.json");
        ArrayList<ArrayList<Item>> newFullList = getOnePackFormatHigherVersion("packformat46.json");

        for (int i = 0; i < oldFullList.size(); i++) {
            if(i == 0 || i ==3){
                Path iconsPath = getPathOfItem(oldTpPath,oldFullList.get(i).get(0));
                if(isNewerVersion){
                    iconsPath = getPathOfItem(destinationFolder,oldFullList.get(i).get(0));
                }
                File iconsImage = new File(Paths.get(String.valueOf(iconsPath),oldFullList.get(i).get(0).getName()).toUri());
                if(iconsImage.exists()){
                    objcutterIconsAndWidgets(newFullList.get(i),iconsImage,256);
                }else{
                    System.err.println("Widgets doesnt exist!");
                }
            }else{
                for (int j = 0; j < oldFullList.get(i).size(); j++) {
                    Path oldPathBase = getPathOfItem(destinationFolder,oldFullList.get(i).get(j));
                    Path newPathBase = getPathOfItem(destinationFolder,newFullList.get(i).get(j));
                    File oldItem = new File(Paths.get(String.valueOf(oldPathBase),oldFullList.get(i).get(j).getName()).toUri());
                    File newItem = new File(Paths.get(String.valueOf(newPathBase), newFullList.get(i).get(j).getName()).toUri());
                    oldItem.renameTo(newItem);
                }
            }
        }
    }
    private static Path getPathOfItem(String pathFront, Item item){
        String[] ollPathis = item.getPath().split("/");
        Path FillPathLol = Paths.get(ollPathis[1]);
        for (int k = 2; k < ollPathis.length; k++) {
            FillPathLol= Paths.get(String.valueOf(FillPathLol),ollPathis[k]);
        }
        return Paths.get(pathFront, String.valueOf(FillPathLol));
    }
    private static ArrayList<ArrayList<Item>> getOnePackFormatLowerVersion(String packformatName){
        Path jsonPath = Paths.get("src", "packformats", packformatName);
        ArrayList<Item> oldItemsList = new ArrayList<>();
        ArrayList<Item> oldBlocksList = new ArrayList<>();
        ArrayList<Item> oldMiscList = new ArrayList<>();
        ArrayList<Item> oldParticlesList = new ArrayList<>();
        if (Files.exists(jsonPath)) {
            try {
                String content = new String(Files.readAllBytes(jsonPath)).trim();
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);  // Entferne BOM (Byte Order Mark), falls vorhanden
                }
                // Prüfen, ob JSON mit { oder [ beginnt
                if (content.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(content);

                    if (jsonObject.has("allItems")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("allItems");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            oldItemsList.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("allBlocks")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("allBlocks");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("blocks");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            oldBlocksList.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("allMisc")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("allMisc");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("misc");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            oldMiscList.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("allMisc")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("allMisc");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("misc");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            oldMiscList.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("allParticles")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("allParticles");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("particle");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            int x = item.getInt("x");
                            int y = item.getInt("y");
                            oldParticlesList.add(new Item(id,name,x,y,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                } else if (content.startsWith("[")) {
                    JSONObject jsonArray = new JSONObject(content);
                    System.out.println(jsonArray.get(""));
                } else {
                    System.err.println("Unbekanntes JSON-Format.");
                }

            } catch (IOException e) {
                System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            } catch (org.json.JSONException e) {
                System.err.println("Fehler beim Verarbeiten der JSON-Daten: " + e.getMessage());
            }
        } else {
            System.err.println("Die Datei existiert nicht: " + jsonPath.toAbsolutePath());
        }
        ArrayList<ArrayList<Item>> doneList = new ArrayList<>();
        doneList.add(oldParticlesList);
        doneList.add(oldItemsList);
        doneList.add(oldBlocksList);
        doneList.add(oldMiscList);
        return doneList;
    }

    private static ArrayList<ArrayList<Item>> getOnePackFormatHigherVersion(String packformatName){
        Path jsonPath = Paths.get("src", "packformats", packformatName);
        ArrayList<Item> upperBodyArmor = new ArrayList<>();
        ArrayList<Item> underBodyArmor = new ArrayList<>();
        ArrayList<Item> widgets = new ArrayList<>();
        ArrayList<Item> icons = new ArrayList<>();
        if (Files.exists(jsonPath)) {
            try {
                String content = new String(Files.readAllBytes(jsonPath)).trim();
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);
                }
                if (content.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(content);

                    if (jsonObject.has("UpperBodyArmor")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("UpperBodyArmor");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("humanoid");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            upperBodyArmor.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("UnderBodyArmor")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("UnderBodyArmor");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("humanoid_leggings");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            underBodyArmor.add(new Item(id,name,0,0,0,0,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("Widgets")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("Widgets");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("widgets");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            int x = item.getInt("x");
                            int y = item.getInt("y");
                            int xLength = item.getInt("xLength");
                            int yLength = item.getInt("yLength");
                            widgets.add(new Item(id,name,x,y,xLength,yLength,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                    if (jsonObject.has("Icons")) {
                        JSONObject blocksObj = jsonObject.getJSONObject("Icons");
                        String itemsPath = (String) blocksObj.get("path");
                        JSONArray itemsArray = blocksObj.getJSONArray("icons");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            int x = item.getInt("x");
                            int y = item.getInt("y");

                            int xLength = item.getInt("xLength");
                            int yLength = item.getInt("yLength");
                            icons.add(new Item(id,name,x,y,xLength,yLength,itemsPath));
                        }
                    } else {
                        System.err.println("JSON enthält kein 'blocks'-Array.");
                    }

                } else if (content.startsWith("[")) {
                    JSONObject jsonArray = new JSONObject(content);
                    System.err.println(jsonArray.get(""));
                } else {
                    System.err.println("Unbekanntes JSON-Format.");
                }

            } catch (IOException e) {
                System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            } catch (org.json.JSONException e) {
                System.err.println("Fehler beim Verarbeiten der JSON-Daten: " + e.getMessage());
            }
        } else {
            System.err.println("Die Datei existiert nicht: " + jsonPath.toAbsolutePath());
        }
        ArrayList<ArrayList<Item>> doneList = new ArrayList<>();
        doneList.add(icons);
        doneList.add(upperBodyArmor);
        doneList.add(underBodyArmor);
        doneList.add(widgets);
        return doneList;
    }
    private static void objcutter(ArrayList<Item> objList, File imageP, Path newPath) {
        if(!imageP.exists()){
            return;
        }
        int multi = 1;
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        multi = image.getWidth() / 128;
        for (int i = 0; i < objList.size(); i++) {
            try {

                BufferedImage background = ImageIO.read(new File("overlay" + multi * 8 +"_"+multi * 8+ ".png"));
                BufferedImage overlay = ImageIO.read(imageP);
                BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);

                int x1 = objList.get(i).getX() * multi;
                int y1 = objList.get(i).getY() * multi;
                int multiX = 8 * multi;
                int xOutLine = x1 + multiX;
                int yOutLine = y1 + multiX;
                for (int x = x1; x < xOutLine; x++) {
                    for (int y = y1; y < yOutLine; y++) {
                        Color overlayColor = new Color(overlay.getRGB(x, y), true);
                        if (!(overlayColor.getAlpha() == 0)) {
                            result.setRGB(x - x1, y - y1, overlay.getRGB(x, y));
                        }
                    }
                }
                File output = null;
                if(objList.get(i).getName().equals("fishing_hook.png")){
                    output = new File(Paths.get(String.valueOf(destinationFolder),"assets","minecraft","textures","entity",objList.get(i).getName()).toUri());
                    printWorkedShit(objList.get(i).getName()+ " added");
                }else{
                    output = new File(Paths.get(String.valueOf(newPath), objList.get(i).getName()).toUri());
                    printWorkedShit(objList.get(i).getName()+ " added");
                }
                ImageIO.write(result, "png", output);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void objcutterIconsAndWidgets(ArrayList<Item> objList, File imageP, int cutter) {
        if(!imageP.exists()){
            return;
        }
        int multi = 1;
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        multi = image.getWidth() / cutter;
        for (int i = 0; i < objList.size(); i++) {
            try {
                BufferedImage background = ImageIO.read(new File("overlay" + multi * objList.get(i).getxLength()+"_" +multi * objList.get(i).getyLength()+ ".png"));
                BufferedImage overlay = ImageIO.read(imageP);
                BufferedImage result = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);

                int x1 = objList.get(i).getX() * multi;
                int y1 = objList.get(i).getY() * multi;
                int multiX = objList.get(i).getxLength() * multi;
                int multiY = objList.get(i).getyLength() * multi;
                int xOutLine = x1 + multiX;
                int yOutLine = y1 + multiY;
                for (int x = x1; x < xOutLine; x++) {
                    for (int y = y1; y < yOutLine; y++) {
                        Color overlayColor = new Color(overlay.getRGB(x, y), true);
                        if (!(overlayColor.getAlpha() == 0)) {
                            result.setRGB(x - x1, y - y1, overlay.getRGB(x, y));
                        }
                    }
                }
                    Path newPathParticles = getPathOfItem(destinationFolder,objList.get(i));
                    if(objList.get(i).getName().contains(";")){
                        String[] splitted = objList.get(i).getName().split(";");
                        newPathParticles = Paths.get(String.valueOf(newPathParticles),splitted[1]);
                    }
                File output = null;
                if(objList.get(i).getName().contains(";")){
                    String[] splitted = objList.get(i).getName().split(";");
                    printWorkedShit(splitted[0]+ " added");
                    output = new File(Paths.get(String.valueOf(newPathParticles), splitted[0]).toUri());
                }else{
                    output = new File(Paths.get(String.valueOf(newPathParticles), objList.get(i).getName()).toUri());
                    printWorkedShit(objList.get(i).getName()+ " added");
                }
                ImageIO.write(result, "png", output);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void createNetheriteItem(File oldItemPath, File newItemPath){
        BufferedImage image;
        if(!oldItemPath.exists()){
            return;
        }
        try {
            image = ImageIO.read(oldItemPath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                int pixel = image.getRGB(col, row);
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                // System.out.println(alpha);
                if(alpha == 255){
                    double L = 0.4126*red + 0.7152*green + 0.0722*blue; // double L = 0.2126*red + 0.7152*green + 0.0722*blue
                    double newR = 71*L/255; //74 -> 35
                    double newG = 58*L/255; // 41 -> 16
                    double newB = 65*L/255; //64 ->18
                    Color newColor = new Color((int) newR, (int) newG, (int) newB);
                    image.setRGB(col, row, newColor.getRGB());
                    // System.out.println("Row: "+row +" Col: "+col+" R: "+red+" G: "+green+" B: "+blue+" NEWR: "+newR+" NEWG: "+newG+" NEWB: "+newB);
                }
            }
        }
        try {
            ImageIO.write(image, "png", newItemPath);
            printWorkedShit("Netherite Item created!");
        } catch (IOException ex) {
        }
    }

    private static void printWorkedShit(String text){
        System.out.println(GREEN+text+RESET);
    }
    private static void printBlues(String text){
        System.out.println(LIGHT_BLUE+text+RESET);
    }
}
