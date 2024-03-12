package org.lkg.simple;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * description:
 * author: 李开广
 * date: 2023/6/1 4:17 PM
 */
public class FileUtil {
    public static String readFile(String url) throws IOException, URISyntaxException {
        InputStream resourceAsStream = FileUtil.class.getClassLoader().getResourceAsStream(url);
        if (Objects.isNull(resourceAsStream)) {
            String workDir = System.getProperty("user.dir");
            System.out.println(workDir + File.separator + url);
            resourceAsStream = Files.newInputStream(Paths.get(workDir + File.separator + url));
        }
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append(System.lineSeparator());
        }
        bufferedReader.close();
        inputStreamReader.close();
        resourceAsStream.close();
        return stringBuilder.delete(stringBuilder.lastIndexOf(System.lineSeparator()), stringBuilder.length()).toString();
    }

    //默认追加写
    public static void writeFile(String str, String url) {
        writeFile(str, url, false);
    }

    public static void writeFile(String str, String url, boolean append) {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(url);
            if (!file.exists()) {
                file.createNewFile();
            }
            //可追加
            writer = new FileWriter(url, append);
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(str + System.lineSeparator());
            bufferedWriter.flush();
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        try {
            System.out.println(readFile("Common-Arhat/src/main/java/org.lkg/simple/a.txt"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
