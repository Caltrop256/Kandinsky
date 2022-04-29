package Kandinsky.Lexer.Tokens;

public enum Typedef {
    any, unit,
    f32, f64,
    i8, i16, i32,
    u1, u8, u16, u32,
    fn, typedef, array, string, struct;

    static public boolean isInteger(Typedef type) {
        switch(type) {
            default : return false;
            case u1 :
            case u8 :
            case u16 :
            case u32 :
            case i8 :
            case i16 :
            case i32 :
                return true;
        }
    }
    static public boolean isFloat(Typedef type) {
        switch(type) {
            default: return false;
            case f32 : 
            case f64 : 
                return true;
        }
    }
    static public boolean isSigned(Typedef type) {
        switch(type) {
            default : return false;
            case i8 :
            case i16 :
            case i32 :
                return true;
        }
    }
}
