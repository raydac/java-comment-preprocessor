package com.igormaznitsa.jcpreprocessor.directives;

public enum DirectiveArgumentType {
    NONE(""),
    STRING("STRING"),
    TAIL("TAIL"),
    BOOLEAN("BOOLEAN"),
    VARNAME("VAR"),
    EXPRESSTION("EXPR"),
    MULTIEXPRESSION("EXPR1,EXPR2...EXPRn"),
    SET("VAR=EXPR"), 
    ONOFF("[+|-]");
    
    private final String str;
    
    private DirectiveArgumentType(final String str){
        this.str = str;
    }
    
    public String getAsText() {
        return this.str;
    }
}
