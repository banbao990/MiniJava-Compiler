import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import global.Global;
import myVisitor.Kanga2Mips;
import parser.KangaParser;
import parser.ParseException;
import parser.TokenMgrError;
import syntaxtree.Node;

public class Main {
    public static void main(String[] args) {
        try {
            String fileName = args[0];
            // String fileName = "TEST.kg";
            InputStream in = new FileInputStream(fileName);
            new KangaParser(in);
            Node root = KangaParser.Goal();
            root.accept(new Kanga2Mips());
            System.out.println("// Generate Mips successfully!");
            writeAndPrint(fileName.replaceAll(".kg", ".s"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (TokenMgrError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAndPrint(String fileName) throws IOException {
        Global.normOfOutputString();
        System.out.println(Global.outputString);
        FileWriter outputFile = new FileWriter(fileName);
        BufferedWriter br = new BufferedWriter(outputFile);
        br.write(Global.outputString);
        br.close();
    }
}
