import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private String fileName;
    private List <String> list;
    private Lexer lexer;
    private Parser parser;

    public Compiler(String fileName) throws IOException, MyException {
        this.fileName = fileName;
        this.list = new ArrayList<>();
        readFile();
        this.lexer = new Lexer(list);

       // this.parser = new Parser(lexer);
       // scanner();
        parser();
    }

    private  void readFile() throws IOException{
        File file = new File(fileName);
        FileInputStream inputStream = new FileInputStream(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();

        if(file.exists() == false){
            throw new IOException("There is no file.");
        }

        try{
            while((line != null)){
                list.add(line.toLowerCase());
                line = reader.readLine();
            }
        } catch (FileNotFoundException nf){
            System.out.println("File not found.");
        } catch (IOException e){
            System.out.println("File can't be read.");
        }
    }

    private  void scanner(){
        Lexeme lex;
        Token token;
        do{
            lex = lexer.scanner();
            if(lex.getToken()==Token.COMMENT){
                token = lex.getToken();
                continue;
            }
            if(lex.getToken() != Token.ERROR){
//                System.out.println(lex.getToken() + ":" + "\t" + lex.getValue() + "\t" + "Line: " + (lex.getLine() + 1) + " Column: " + lex.getColumn());
                token = lex.getToken();
                continue;
            }
//            System.out.println("Error: line: " + (lex.getLine() + 1) + ", Column: " + lex.getColumn() + "\n\t" + lex.getErrorMessage());
            break;
        } while(token != Token.EOF);
    }

    private void parser() throws  MyException{
        try{
            parser = new Parser(lexer);
            parser.program();

        } catch(MyException myException){
            System.out.println("=============================================================================");
            System.out.println("Error in line: " + myException.getLine()+ ", column: " + myException.getColumn() +
                    "\nLast token read: " + myException.getLexeme().getValue() + "\n" + myException.getMessage());
            System.out.println("=============================================================================");
        }
    }
}
