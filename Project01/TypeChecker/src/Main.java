
// package
// library
import static java.lang.System.out;

import java.io.FileInputStream;
import java.io.InputStream;

import global.Global;
import myVisitor.BuildSymbolTableVisitor;
import myVisitor.TypeCheckVisitor;
import parser.MiniJavaParser;
import parser.ParseException;
import parser.TokenMgrError;
import symbol.MyClassList;
import syntaxtree.Node;
import typecheck.ErrorPrinter;

public class Main {
    public static void main(String args[]) {
        try {
            InputStream in = new FileInputStream(args[0]);
            // InputStream in = new FileInputStream("test.java");
            new MiniJavaParser(in);
            Node root = MiniJavaParser.Goal();
            // 建立符号表
            Global.allClass = new MyClassList();
            root.accept(new BuildSymbolTableVisitor(), Global.allClass);
            // 一些全局的检查(循环继承,继承类的声明)
            Global.check();
            // 符号检查
            root.accept(new TypeCheckVisitor(), Global.allClass);
            // 输出错误
            if (ErrorPrinter.getSize() == 0) {
                out.println("Program type checked successfully!");
            } else {
                out.println("Type error!\n    " + ErrorPrinter.getSize()
                        + " error(s) detected!");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (TokenMgrError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
