
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MyFirstCompiler {
    public static void main(String[] args) throws IOException , MyException {
        if(args.length == 1){
            Compiler compiler = new Compiler(args[0]);
        } else {
            System.out.println("Its required at least a file");
        }
    }
}
