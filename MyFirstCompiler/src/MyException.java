public class MyException extends Exception {
    private Lexeme lexeme;
    private int line;
    private int column;
    private  String message;

    public MyException(Lexeme lexeme, int line, int column, String message) {
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public Lexeme getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }
}
