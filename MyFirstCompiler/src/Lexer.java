import java.io.IOException;
import java.util.List;

public class Lexer {
    private List<String> list;
    private char lookAhead;
    private int line;
    private int column;

    public Lexer(List<String> list) {
        this.list = list;
        this.lookAhead = ' ';
        this.line = 0;
        this.column = 0;
    }

    public Lexeme scanner(){


        while(lookAhead=='\n' || lookAhead==' ' || lookAhead=='\t'){
                if(lookAhead=='\n'){
                    line = line + 1;
                    column = 0;
                } else if(lookAhead=='\t') {
                    column = column + 3;
                }
                lookAhead = nextlookahead();
        }

        StringBuilder strBuilder = new StringBuilder();

        if(Character.isDigit(lookAhead)){                   //starting with number
            while(Character.isDigit(lookAhead)){
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
            }

            if(Character.isAlphabetic(lookAhead) || lookAhead==' '){
                return  new Lexeme(line, column,Token.INT,strBuilder.toString());
            }

            if(lookAhead=='.'){
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                if(Character.isDigit(lookAhead)){
                    while(Character.isDigit(lookAhead)) {
                        strBuilder.append(lookAhead);
                        lookAhead = nextlookahead();
                    }
                    return new Lexeme( line, column, Token.FLOAT,strBuilder.toString());
                } else{
                        Lexeme lex = new Lexeme(line, column, Token.ERROR, strBuilder.toString());
                        lex.setErrorMessage("Bad format float.");
                        return lex;
                }
            }
            return  new Lexeme(line, column,Token.INT,strBuilder.toString());


        }

        if(lookAhead=='.'){                     //starting with dot
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(Character.isDigit(lookAhead)){
                while(Character.isDigit(lookAhead)) {
                    strBuilder.append(lookAhead);
                    lookAhead = nextlookahead();
                }
                return new Lexeme(line, column, Token.FLOAT, strBuilder.toString());
            } else {
                Lexeme lex = new Lexeme(line, column, Token.ERROR,strBuilder.toString());
                lex.setErrorMessage("Bad format float.");
                return lex;
            }
        }

        if(Character.isAlphabetic(lookAhead) || lookAhead=='_'){                  //strating with letter or underline
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            while(Character.isAlphabetic(lookAhead) || lookAhead=='_' || Character.isDigit(lookAhead)) {
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
            }
            return verifyWord(strBuilder.toString());
        }

        if(lookAhead=='\''){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(Character.isAlphabetic(lookAhead) || Character.isDigit(lookAhead)){
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                if(lookAhead=='\''){
                    strBuilder.append(lookAhead);
                    lookAhead = nextlookahead();
                    return new Lexeme(line, column, Token.CHAR, strBuilder.toString());
                } else{
                    Lexeme lex = new Lexeme(line, column, Token.ERROR, strBuilder.toString());
                    lex.setErrorMessage("Bad format char.");
                    return lex;
                }
            } else{
                Lexeme lex = new Lexeme(line, column, Token.ERROR, strBuilder.toString());
                lex.setErrorMessage("Bad format char.");
                return lex;
            }
        }

        if(lookAhead=='('){                                                      //especial characters
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.OPEN_PARENTHESES,strBuilder.toString());
        } else if (lookAhead==')'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.CLOSED_PARENTHESES,strBuilder.toString());
        } else if (lookAhead=='{') {
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.OPEN_BRACES,strBuilder.toString());
        } else if (lookAhead=='}'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.CLOSED_BRACES,strBuilder.toString());
        } else if(lookAhead==','){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.COMMA,strBuilder.toString());
        } else if(lookAhead==';'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.SEMICOLON,strBuilder.toString());
        }

        if(lookAhead=='<'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(lookAhead=='='){
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                return new Lexeme(line, column, Token.LESS_THAN_OR_EQUAL,strBuilder.toString());
            } else{
                return new Lexeme(line, column, Token.LESS_THAN,strBuilder.toString());
            }
        } else if(lookAhead=='>'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(lookAhead=='='){
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                return new Lexeme(line, column, Token.GREATER_THAN_OR_EQUAL,strBuilder.toString());
            } else{
                return new Lexeme(line, column, Token.GREATER_THAN,strBuilder.toString());
            }
        } else if(lookAhead=='='){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(lookAhead=='=') {
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                return new Lexeme(line, column, Token.EQUIVALENT, strBuilder.toString());               // two '==' ---> EQUIVALENT
            } else {
                return new Lexeme(line, column, Token.EQUAL, strBuilder.toString());                    // one '=' ----> EQUAL
            }
        } else if(lookAhead=='!'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(lookAhead=='=') {
                strBuilder.append(lookAhead);
                lookAhead = nextlookahead();
                return new Lexeme(line, column, Token.NOT_EQUAL, strBuilder.toString());
            } else{
                Lexeme lex = new Lexeme(line, column, Token.ERROR, strBuilder.toString());
                lex.setErrorMessage("Relacional Operator bad formed");
                return lex;
            }
        }

        if(lookAhead=='+'){                                                             //aritmethics operators '+' and '-'
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.PLUS, strBuilder.toString());
        } else if (lookAhead=='-'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return  new Lexeme(line, column, Token.MINUS, strBuilder.toString());
        }

        if(lookAhead=='/'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            if(lookAhead!='/' && lookAhead!='*'){
                return  new Lexeme(line, column, Token.DIVIDED, strBuilder.toString());             //operator '/'
            } else{
                if(lookAhead=='/'){                                                                 //single line comment
                    lookAhead = nextlookahead();
                    while(lookAhead!='\n'){
                        lookAhead = nextlookahead();
                    }
                    lookAhead = nextlookahead();
                    return new Lexeme(line, column, Token.COMMENT, strBuilder.toString());          //multu line comment
                } else if(lookAhead=='*'){
                    while(lookAhead != '/') {
                        lookAhead=nextlookahead();
                        while (lookAhead != '*') {
                            if (lookAhead == '\n') {
                                line = line + 1;
                                column = 0;
                            }
                            if(lookAhead=='$'){
                                Lexeme lex = new Lexeme(line, column, Token.EOF, strBuilder.toString());
                                lex.setErrorMessage("Unfinished comment.");
                                return lex;
                            }
                            lookAhead = nextlookahead();
                        }
                        lookAhead = nextlookahead();
                        if (lookAhead == '/') {
                            lookAhead = nextlookahead();
                            return new Lexeme(line, column, Token.COMMENT, strBuilder.toString());
                        }
                    }
                }
            }
        }

        if(lookAhead=='*'){
            strBuilder.append(lookAhead);
            lookAhead = nextlookahead();
            return new Lexeme(line, column, Token.MULTIPLIED, strBuilder.toString());
        }

        if(lookAhead=='$'){
            return new Lexeme(line, column, Token.EOF, strBuilder.toString());
        }

        Lexeme lex = new Lexeme(line, column, Token.ERROR, strBuilder.toString());
        lex.setErrorMessage("Invalid character.");
        return lex;
    }

    private Lexeme verifyWord(String strBuilder) {
        if(strBuilder.toString().equals("main")){                                   //verify reserved words
            return new Lexeme (line, column, Token.MAIN,strBuilder);
        }else if(strBuilder.toString().equals("if")){
            return new Lexeme (line, column, Token.IF,strBuilder);
        } else if(strBuilder.toString().equals("else")){
            return new Lexeme (line, column, Token.ELSE,strBuilder);
        }else if(strBuilder.toString().equals("while")){
            return new Lexeme (line, column, Token.WHILE,strBuilder);
        }else if(strBuilder.toString().equals("do")){
            return new Lexeme (line, column, Token.DO,strBuilder);
        }else if(strBuilder.toString().equals("for")){
            return new Lexeme (line, column, Token.FOR,strBuilder);
        }else if(strBuilder.toString().equals("int")){
            return new Lexeme (line, column, Token.TYPE_INT,strBuilder);
        }else if(strBuilder.toString().equals("float")){
            return new Lexeme (line, column, Token.TYPE_FLOAT,strBuilder);
        }else if(strBuilder.toString().equals("char")){
            return new Lexeme (line, column, Token.TYPE_CHAR,strBuilder);
        } else{
            return new Lexeme(line, column, Token.IDENTIFIER, strBuilder);
        }
    }

    private char nextlookahead(){
        try {
            if (column < list.get(line).length()) {
                return list.get(line).charAt(column++);
            } else if (column >= list.get(line).length()) {
                return '\n';
            }
        } catch(IndexOutOfBoundsException io){
            return '$';    //when the whole file was read
        }
        return  '$';
    }


}
