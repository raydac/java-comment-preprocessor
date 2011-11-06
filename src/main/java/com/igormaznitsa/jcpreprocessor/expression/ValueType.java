package com.igormaznitsa.jcpreprocessor.expression;

public enum ValueType {
    ANY("Any"), STRING("Str"), BOOLEAN("Bool"), INT("Int"), FLOAT("Float"), UNKNOWN("Unknown");
    
    private final String signature;
    
    public String getSignature(){
        return this.signature;
    }
    
    private ValueType(final String signature) {
        this.signature = signature;
    }
    
    public boolean isCompatible(final ValueType type){
        if (this == type) {
            return true;
        }

        if (this == UNKNOWN || type == UNKNOWN) {
            return false;
        }
        
        if (this == ANY || type == ANY){
            return true;
        }
        
        return false;
    }
}
