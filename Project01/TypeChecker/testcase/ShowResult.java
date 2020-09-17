/**
 * @author banbao
 * 展示 testcase 的正误
 */

import java.io.*;
import java.util.*;

public class ShowResult {
    private static final String OK
        = "Program type checked successfully";
    private static final String ERROR
        = "Type error";
    private static boolean onlyShowTE = true;


    public static void main(String...args) {
        if(args.length != 0) {
            onlyShowTE = false;
        } else {
            System.out.println(ERROR + ":");
        }
        walk(new File("testcase"));
    }

    public static void walk(File dir) {
        if(!dir.exists() || !dir.isDirectory()) return;
        for(String fileInDir : dir.list()) {
            File file = new File(dir, fileInDir);
            if(file.isFile()) {
                dealWithTheFile(
                    dir + "" + File.separatorChar + fileInDir);
            } else {
                walk(file);
            }
        }
    }

    private static boolean dealWithTheFile(String fileName) {
        boolean ret = true;
        try {
            BufferedReader br =
                new BufferedReader(
                new FileReader(fileName));
            String s = null;
            int commentStart = 0;
            while((s = br.readLine()) != null) {
                if(((commentStart = s.indexOf("//")) != -1)
                        && (s.indexOf("TE", commentStart) != -1)) {
                    ret = false;
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(onlyShowTE) {
            if(!ret) {
                // testcase\testcase\test54.java
                // ->
                // testcase\test54.java
                System.out.println(
                    fileName.substring(
                        fileName.indexOf(File.separatorChar) + 1));
            }
        } else {
            System.out.println(fileName + " : " + (ret ? OK : ERROR));
        }
        return ret;
    }
}
