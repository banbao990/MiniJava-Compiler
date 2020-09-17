import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import global.Global;
import myVisitor.GenerateKanga;
import myVisitor.GenerateLiveMap;
import parser.ParseException;
import parser.SpigletParser;
import parser.TokenMgrError;
import syntaxtree.Node;

public class Main {
    public static void main(String[] args) {
        try {
            String fileName = args[0];
            // String fileName = "TEST.spg";
            InputStream in = new FileInputStream(fileName);
            new SpigletParser(in);
            Node root = SpigletParser.Goal();
            // 构造出初始用于规则推导的约束变量(结点+变量+后继)
            root.accept(new GenerateLiveMap());
            // 活性分析(规则推导), 迭代至不动点
            Global.deal();
            // 寄存器分配(线性扫描)
            // 代码转换
            root.accept(new GenerateKanga(), null);
            // 输出到标准输出和文件
            System.out.println("// Generate Kanga successfully!");
            writeAndPrint(fileName.replaceAll(".spg", ".kg"));
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
