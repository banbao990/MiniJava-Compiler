import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import global.Global;
import myVisitor.GenerateSpiglet;
import myVisitor.GetMaxTempNum;
import parser.ParseException;
import parser.PigletParser;
import parser.TokenMgrError;
import syntaxtree.Node;

public class Main {

    public static void main(String[] args) {
        try {
            String fileName = args[0];
            // String fileName = "test.pg";
            InputStream in = new FileInputStream(fileName);
            new PigletParser(in);
            Node root = PigletParser.Goal();

            // 获取最大编号的 TEMP
            root.accept(new GetMaxTempNum());

            // 将输入完全正确的 piglet转化为 spiglet
            root.accept(new GenerateSpiglet(), null);

            // 将输出调整下格式
            Global.normOfOutputString();

            // 输出到标准输出和文件
            System.out.println("// Generate Spiglet successfully!");
            writeAndPrint(fileName.replaceAll(".pg", ".spg"));

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
