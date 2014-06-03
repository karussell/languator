package com.graphhopper.languator;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.util.LangProfile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.arnx.jsonic.JSON;

/**
 */
public class App {

    public static void main(String[] args) throws Exception {
        // xy.pbf file - download it e.g. from download.geofabrik.de and specify as second argument
        //  
        // e.g. great-britain.pbf from http://download.geofabrik.de/europe/great-britain-latest.osm.pbf)

        if (args.length > 0 && "runall".equals(args[0])) {
            String dir = args[1];
//            runAll("myinit", dir);
            runAll("mylangdet", dir);
        } else if (args.length == 3) {
            new App().start(args[0], new File(args[1]), args[2]);
        } else
            log("Usage\n1. App <init|langdet|mylangdet> <pbf file> <lang-code>"
                    + "\n Example to detect italian languages from italy.pbf => mylangdet /media/SAMSUNG/maps/italy.pbf it"
                    + "\n2. App runall <folder with pbf files>"
                    + "\n Example => runall /media/SAMSUNG/maps");

        System.exit(0);
    }

    public static void runAll(String mode, String dir) throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("en", dir + "/great-britain.pbf");
        map.put("de", dir + "/germany.pbf");
        map.put("it", dir + "/italy.pbf");
        map.put("fr", dir + "/france.pbf");

        for (Entry<String, String> e : map.entrySet()) {
            new App().start(mode, new File(e.getValue()), e.getKey());
        }
    }
    private final int minNgram = 5;

    void start(String mode, File file, String lang) throws Exception {
        OSMStream stream = new OSMStream(file);
        stream.start();

        if (mode.equals("init"))
            init(stream, lang);
        else if (mode.equals("langdet"))
            langDet(stream, lang);
        else if (mode.equals("myinit")) {
            MyLang myLang = myInit(stream, lang, 5);
            myLang.store("myprofile/" + lang + ".txt");
        } else if (mode.equals("mylangdet"))
            myLangDet(stream, lang);
    }

    /**
     * Creates one profile for the specified stream and language.
     */
    public void init(StringStream stream, String lang) throws Exception {
        log("init " + lang + " from " + stream.getName());
        LangProfile profile = new LangProfile(lang);
        int counter = 0;
        while (stream.hasNext()) {
            String name = stream.next();
            name = MyLang.normalize(name);
            for (String token : name.split(" ")) {
                token = token.trim();
                if (token.length() < minNgram)
                    continue;

                if (isNotName(token))
                    continue;

                // log(name + ", " + stream.getCurrentSize());
                for (String gram : MyLangDet.gram(token, minNgram, token.length())) {
                    if (isNotName(gram))
                        continue;

                    profile.add(gram);
                }
            }
            counter++;

            if (counter % 1000000 == 0)
                log(counter / 1e6f + " mio words, mem:" + getUsedMB() + "/" + getTotalMB()
                        + ", current queue size:" + stream.getCurrentSize());
        }

        profile.omitLessFreq();

        File profile_path = new File("profiles.map/" + lang);
        FileOutputStream os = new FileOutputStream(profile_path);
        JSON.encode(profile, os);
    }

    /**
     * Determines the language of the specified stream via the
     * language-detection project tuned towards local names in profiles.map/
     */
    public void langDet(StringStream stream, String lang) throws Exception {
        // String profiles = "profiles.sm/";
        String profiles = "profiles.map/";
        log("run content of " + stream.getName() + " and detect language '" + lang + "' using " + profiles);
        DetectorFactory.clear();
        DetectorFactory.loadProfile(new File(profiles));

        // avoid randomness
        DetectorFactory.setSeed(0);
        // slightly prefer english
        HashMap priorMap = new HashMap();
        priorMap.put("en", 12d);
        priorMap.put("de", 10d);
        priorMap.put("fr", 10d);
        priorMap.put("it", 10d);

        int counter = 0;
        int errors = 0;
        while (stream.hasNext()) {
            String name = stream.next();
            if (isNotName(name))
                continue;

            try {
                counter++;
                Detector detector = DetectorFactory.create();
                detector.setPriorMap(priorMap);
                detector.append(name);
                if (!lang.equals(detector.detect())) {
                    errors++;
                    // log(name + ": " + detector.getProbabilities());
                }

                if (counter % 1000000 == 0)
                    log(counter / 1000000f + " mio");

            } catch (LangDetectException ex) {
                if (!ex.getMessage().equals("no features in text"))
                    log("ERROR:" + name + ", " + ex.getMessage() + ", " + ex.getClass());
            }
        }

        log("counter " + counter + ", errors:" + errors + ", " + (float) errors / counter);
    }

    /**
     * Faster language detection but probably does not scale for many languages
     */
    public void myLangDet(StringStream stream, String lang) throws IOException {
        log("run content of " + stream.getName() + " and detect " + lang);
        int counter = 0;
        int errors = 0;
        MyLangDet myLangDet = new MyLangDet();
        myLangDet.init();
        while (stream.hasNext()) {
            String name = stream.next();
            if (isNotName(name))
                continue;

            counter++;
            if (!lang.equals(myLangDet.getLang(name))) {
                errors++;
                // log("name " + name + ", " + myLangDet.getPrios(name));
            }
        }
        log("counter " + counter + ", errors:" + errors + ", " + (float) errors / counter);
    }

    public static final long MB = 1L << 20;

    public static long getTotalMB() {
        return Runtime.getRuntime().totalMemory() / MB;
    }

    public static long getUsedMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB;
    }

    private boolean isNotName(String name) {
        if (name.isEmpty())
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

    MyLang myInit(StringStream stream, String lang, int minFreq) throws Exception {
        log("init " + lang + " from " + stream.getName());
        int counter = 0;
        Map<String, Integer> map = new HashMap<String, Integer>();
        while (stream.hasNext()) {
            String name = stream.next();
            if (name.toLowerCase().contains("trento centro"))
                name = name;

            if (isNotName(name))
                continue;

            name = MyLang.normalize(name);
            for (String token : name.split(" ")) {
                token = token.trim();
                if (token.length() < minNgram)
                    continue;

                if (isNotName(token))
                    continue;

                // log(name + ", " + stream.getCurrentSize());
                for (String gram : MyLangDet.gram(token, minNgram, 6)) {
                    if (isNotName(gram))
                        continue;

                    Integer old = map.put(gram, 1);
                    if (old != null)
                        map.put(gram, old + 1);
                }
            }
            counter++;

            if (counter % 1000000 == 0)
                log(counter / 1e6f + " mio words, mem:" + getUsedMB() + "/" + getTotalMB() + ", current queue size:" + stream.getCurrentSize());
        }

        return MyLang.build(lang, map, minFreq);
    }

    static void log(String str) {
        System.out.println(new Date() + "| " + str);
    }
}
