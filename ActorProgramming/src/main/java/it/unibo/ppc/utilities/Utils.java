package it.unibo.ppc.utilities;

import it.unibo.ppc.utils.RangeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    static final Logger logger = LoggerFactory.getLogger("bho");
    public static void log(String message) {
        logger.info("{} | " + message, Thread.currentThread());
    }

    static void logResult(String message){
        logger.trace(message);
    }

    public static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }


    public static Map<RangeClass, Integer> makeResult(final int nInterval, final int maxLines){
        Map<RangeClass, Integer> mapResult = new HashMap<>();
        int mod = Math.abs(maxLines / (nInterval - 1));
        int m = 0;
        for (int i = 0; i < nInterval - 1; i++, m += mod) {
            mapResult.put(new RangeClass((mod + m - 1), m), 0);
        }
        mapResult.put(new RangeClass(-1, maxLines), 0);
        return mapResult;
    }
    public static void populateListOfPaths(List<String> pathList, String directoryPath) throws IOException {
        final String dir = System.getProperty("user.dir");
        final String sep = System.getProperty("file.separator");
        System.out.println("Checking: " +dir + sep + ".." + sep + directoryPath );
        Files.walkFileTree(Paths.get(dir + sep + directoryPath ), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    pathList.add(file.toString());

                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static synchronized Pair<String, Integer> linesWithBufferInputStream(String fileName) throws InterruptedException {
        Thread.sleep(400);
        long lines = 0;

        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            lines = count;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(fileName, (int)lines);
    }


    public static synchronized int linesWithBufferInputStream(String fileName, Flag flag, Controller controller) throws InterruptedException {
//        Thread.sleep(800);
        long lines = 0;

        while (flag.isSet()){
            log("INTERRUPTED");
        }
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            lines = count;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        GUIResponsive.updateCountValue("BOI: " + String.valueOf((int)lines));
        controller.updateMap((int)lines);
//        controller.makeLuis((int)lines);
        return (int)lines;
    }
}
