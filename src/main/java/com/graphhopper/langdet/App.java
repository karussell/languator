package com.graphhopper.langdet;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.util.LangProfile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.arnx.jsonic.JSON;

/**
 */
public class App {

    public static void main(String[] args) throws Exception {
        // xy.pbf file - download it e.g. from download.geofabrik.de and specify as first argument
        //
        // /media/SAMSUNG/maps/germany.pbf de (from http://download.geofabrik.de/europe/germany-latest.osm.pbf)
        // /media/SAMSUNG/maps/france.pbf fr (from http://download.geofabrik.de/europe/france-latest.osm.pbf)
        // from http://download.geofabrik.de/europe/italy-latest.osm.pbf
        // from http://download.geofabrik.de/europe/great-britain-latest.osm.pbf

        if (args.length != 2)
            throw new RuntimeException("Please use it via 'App <pbf file> <lang-code>");

        new App().start(new File(args[0]), args[1]);
    }

    void start(File file, String lang) throws Exception {
        OSMStream stream = new OSMStream(file);
        stream.start();

        init(stream, lang);

        // myLangDet(stream);
        // langDet(stream);
    }

    public void init(OSMStream stream, String lang) throws Exception {
        List<String> grams = new ArrayList<String>();
        LangProfile profile = new LangProfile(lang);
        int counter = 0;
        while (stream.hasMore()) {
            String name = stream.getNext();
            if (name.isEmpty())
                continue;

            if (name.length() < 5)
                continue;

            if (isNotName(name))
                continue;

            name = name.replaceAll("[\\\"\\:\\;\\&\\.\\!\\?\\)\\(\\[\\]\\,\\>\\<\\-\\n\\t\\&]", " ");
            // System.out.println(name + ", " + stream.getCurrentSize());

            grams.add(name);
            MyLangDet.gram(grams);
            for (String gram : grams) {
                if (isNotName(gram))
                    continue;

                profile.add(gram);
            }
            grams.clear();
            counter++;

            if (counter % 1000000 == 0)
                System.out.println(counter / 1e6f + " mio words, mem:" + getUsedMB() + "/" + getTotalMB() + ", current queue size:" + stream.getCurrentSize());
        }

        profile.omitLessFreq();

        File profile_path = new File("profiles.map/" + lang);
        FileOutputStream os = new FileOutputStream(profile_path);
        JSON.encode(profile, os);
    }

    private static void langDet(OSMStream stream, String lang) throws Exception {
        // DetectorFactory.loadProfile(new File("profiles.sm"));
        DetectorFactory.loadProfile(new File("profiles.map/"));

        // avoid randomness
        DetectorFactory.setSeed(0);
        // prefer english
        HashMap priorMap = new HashMap();
        priorMap.put("en", .3d);
        priorMap.put("de", .1d);
        priorMap.put("fr", .1d);
        priorMap.put("it", .1d);

        int counter = 0;
        int errors = 0;
        MyLangDet myLangDet = new MyLangDet();
        myLangDet.init();
        while (stream.hasMore()) {
            String name = stream.getNext();
            if (name.isEmpty())
                continue;

            if (name.startsWith("A ") || name.startsWith("B ") || name.startsWith("S ") || name.startsWith("L ") || name.startsWith("K "))
                continue;

            try {
                counter++;
//                if (!lang.equals(myLangDet.getLang(name))) {

                Detector detector = DetectorFactory.create();
                detector.setPriorMap(priorMap);
                detector.append(name);
                if (!lang.equals(detector.detect())) {
                    errors++;
                    // System.out.println(name + ": " + detector.getProbabilities());
                }
//                }
            } catch (LangDetectException ex) {
                if (!ex.getMessage().equals("no features in text"))
                    System.out.println("ERROR:" + name + ", " + ex.getMessage() + ", " + ex.getClass());
            }
        }

        System.out.println("counter " + counter + ", errors:" + errors + ", " + (float) errors / counter);
    }

    private void myLangDet(OSMStream stream, String lang) throws IOException {
        int counter = 0;
        int errors = 0;
        MyLangDet myLangDet = new MyLangDet();
        myLangDet.init();
        while (stream.hasMore()) {
            String name = stream.getNext();
            if (name.isEmpty())
                continue;
            if (name.startsWith("A ") || name.startsWith("B ") || name.startsWith("S ")
                    || name.startsWith("L ") || name.startsWith("K "))
                continue;

            counter++;
            if (!lang.equals(myLangDet.getLang(name))) {
                errors++;
                System.out.println("name " + name);
            }
        }
        System.out.println("counter " + counter + ", errors:" + errors + ", " + (float) errors / counter);
    }

    public static final long MB = 1L << 20;

    public static long getTotalMB() {
        return Runtime.getRuntime().totalMemory() / MB;
    }

    public static long getUsedMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB;
    }

    private boolean isNotName(String name) {
        if (name.startsWith("A ") || name.startsWith("B ")
                || name.startsWith("S ")
                || name.startsWith("H ")
                || name.startsWith("K ")
                || name.startsWith("L "))
            return true;

        // if number, skip
        try {
            int value = Integer.parseInt(name);
            if (value > Integer.MIN_VALUE)
                return true;
        } catch (Exception ex) {
        }

        // if road + number e.g. A4, skip
        try {
            int value = Integer.parseInt(name.substring(1));
            if (value > Integer.MIN_VALUE)
                return true;
        } catch (Exception ex) {
        }

        // now it could be a name
        return false;
    }
}
