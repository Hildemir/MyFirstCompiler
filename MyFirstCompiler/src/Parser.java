public class Parser {

    private Lexer lexer;
    private Lexeme lookAhead;

    public Parser(Lexer lexer) throws MyException {
        this.lexer = lexer;
        this.lookAhead = lexer.scanner();
    }

    public void  nextLookAhead(){
         lookAhead = lexer.scanner();
    }
    public Lexeme program() throws MyException{

        if(lookAhead.getValue().equals("int")){
            nextLookAhead();
            if(lookAhead.getValue().equals("main")){
                nextLookAhead();
                if(lookAhead.getValue().equals("(")){
                    nextLookAhead();
                    if(lookAhead.getValue().equals(")")){
                        nextLookAhead();
                        block();
                        if(lookAhead.getValue().equals("}")){
                            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"The close braces doesn't finish any block");
                        }
                    }
                } else{
                    throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"Program must begin with 'int main ()'");
                }
            } else{
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"Program must begin with 'int main'");
            }
        } else {
            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"Program must begin with 'int'");
        }
        return lookAhead;

    }

    private void block() throws MyException{
        if(lookAhead.getValue().equals("{")){
            nextLookAhead();
            if(lookAhead.getValue().equals("int") || lookAhead.getValue().equals("float") || lookAhead.getValue().equals("char")){
                while (lookAhead.getValue().equals("int") || lookAhead.getValue().equals("float") || lookAhead.getValue().equals("char")){
                    nextLookAhead();
                    declaration();
                }
            }
            if(!lookAhead.getValue().equals("}")){
                while(!lookAhead.getValue().equals("}")){
                    if(lookAhead.getValue().equals("int") || lookAhead.getValue().equals("float") || lookAhead.getValue().equals("char")){
                        throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"Declarations must be in the beggiing of a block");
                    }
                    comand();
                }
            }
        } else{
            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(),"Block not opened");
        }
    }

    private void comand() throws MyException {
        if(lookAhead.getValue().equals("while") || lookAhead.getValue().equals("do")){
            interaction();
        }
        if(lookAhead.getValue().equals("if")){
            nextLookAhead();
            if(lookAhead.getValue().equals("(")){
                nextLookAhead();
                Lexeme lexeme = relationalExpression();
                if(lookAhead.getValue().equals(")")){
                    nextLookAhead();
                    comand();
                    if(lookAhead.getValue().equals("else")){
                        nextLookAhead();
                        comand();
                    }
                } else{
                    throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing '(' from the last 'if'");
                }
            } else{
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing '(' from last 'if'");
            }
        }
        basicComand();
    }


    private void interaction() throws MyException{
        if(lookAhead.getValue().equals("do")){
            nextLookAhead();
            comand();
            if(lookAhead.getValue().equals("while")){
                nextLookAhead();
                if(lookAhead.getValue().equals("(")){
                    nextLookAhead();
                    Lexeme expression = relationalExpression();
                    if(lookAhead.getValue().equals(")")){
                        nextLookAhead();
                        if(lookAhead.getValue().equals(";")){
                            nextLookAhead();
                        } else {
                            throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing ';'");
                        }
                    } else {
                        throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing ')'");
                    }
                } else{
                    throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing '(' from the last 'while'");
                }
            } else{
                throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing 'while' from the last 'do'");
            }
        } else if(lookAhead.getValue().equals("while")){
            nextLookAhead();
            if(lookAhead.getValue().equals("(")){
                nextLookAhead();
                Lexeme expression = relationalExpression();
                if(lookAhead.getValue().equals(")")) {
                    nextLookAhead();
                    comand();
                }else {
                    throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing ')'");
                }
            }throw  new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing '(' from the last 'while'");
        }
    }

    private Lexeme basicComand() throws MyException {
        if(lookAhead.getValue().equals("{")){
            block();
        }else {
            if (lookAhead.getToken()==Token.IDENTIFIER) {
                Lexeme lexeme = lookAhead;
                nextLookAhead();
                Lexeme lexeme2 = equal();
                return new Lexeme(lexeme.getLine(), lexeme.getColumn(), lexeme.getToken(), lexeme.getValue());
            } else {
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Attribution must start with an identifier");
            }
        }
        return lookAhead;
    }

    private Lexeme equal() throws MyException {
        Lexeme lexeme;
        if(lookAhead.getToken()==Token.EQUAL){
            nextLookAhead();
            lexeme = aritmethicExpression();
            if(lookAhead.getValue().equals(";")){
                nextLookAhead();
            } else {
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing ';'");
            }
        } else {
            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing attribution symbol");
        }
        return new Lexeme(lexeme.getLine(), lexeme.getColumn(), lexeme.getToken(), lexeme.getValue());
    }

    private void declaration() throws  MyException {
        if(lookAhead.getToken()==Token.IDENTIFIER){
            nextLookAhead();
            if (lookAhead.getValue().equals(",")){
                nextLookAhead();
                declaration();
            } else if(lookAhead.getValue().equals(";")){
                nextLookAhead();
            } else {
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Declaration wasn't finished with ';'");
            }

        } else{
            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Must be an identifier");
        }
    }

    private Lexeme factor() throws  MyException{
        if (lookAhead.getValue().equals("(")) {
            nextLookAhead();
            aritmethicExpression();
            if (lookAhead.getValue().equals(")")) {
                nextLookAhead();
            } else {
                throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing the ')'");
            }
        } else if(lookAhead.getToken().equals(Token.INT) || lookAhead.getToken().equals(Token.FLOAT) || lookAhead.getToken().equals(Token.IDENTIFIER)){
            nextLookAhead();
        }

        throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Waiting for a value that belongs to the language");
    }

    private Lexeme term() throws MyException{
        Lexeme lexeme = factor();
        while(lookAhead.getValue().equals("/") || lookAhead.getValue().equals("*")){
            nextLookAhead();
            Lexeme lexeme2 = factor();
        }
        return new Lexeme(lookAhead.getLine(), lookAhead.getColumn(),lexeme.getToken(),lexeme.getValue());
    }

    private Lexeme aritmethicExpression() throws MyException{
        Lexeme lexeme = term();
        if (lookAhead.getValue().equals("-") || (lookAhead.getValue().equals("+"))){
            nextLookAhead();
            Lexeme lexeme2 = aritmethicExpression();
        }
        return new Lexeme(lexeme.getLine(), lexeme.getColumn(),lexeme.getToken(),lexeme.getValue());
    }

    private Lexeme relationalExpression() throws MyException{
        Lexeme lexeme = aritmethicExpression();
        if (lookAhead.getToken()==Token.GREATER_THAN ||
                lookAhead.getToken() == Token.LESS_THAN ||
                lookAhead.getToken() == Token.GREATER_THAN_OR_EQUAL || lookAhead.getToken() == Token.LESS_THAN || lookAhead.getToken() == Token.LESS_THAN_OR_EQUAL ||
                lookAhead.getToken() == Token.EQUIVALENT || lookAhead.getToken() == Token.NOT_EQUAL ){
            nextLookAhead();

        } else {
            throw new MyException(lookAhead, lookAhead.getLine(), lookAhead.getColumn(), "Missing the operator");
        }
        Lexeme lexeme2 = aritmethicExpression();
        return new Lexeme(lexeme.getLine(), lexeme.getColumn(), lexeme.getToken(), lexeme.getValue());
    }
}
