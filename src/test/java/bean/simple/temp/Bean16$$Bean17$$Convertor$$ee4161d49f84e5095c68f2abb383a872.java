package bean.simple.temp;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import bean.simple.Bean16;
import bean.simple.Bean17;
import bean.simple.Enum3;
import bean.simple.Enum4;
import com.elin4it.convert.Convertor;
import java.util.HashMap;
import java.util.Map;

public class Bean16$$Bean17$$Convertor$$ee4161d49f84e5095c68f2abb383a872 implements Convertor {
    private static final Map _beansimpleEnum4$$javalangCharacterMap = new HashMap();
    private static final Map _javalangCharacter$$beansimpleEnum4Map = new HashMap();
    private static final Map _beansimpleEnum3$$javalangByteMap = new HashMap();
    private static final Map _javalangByte$$beansimpleEnum3Map = new HashMap();

    static {
        _beansimpleEnum4$$javalangCharacterMap.put(Enum4.B, 'b');
        _javalangCharacter$$beansimpleEnum4Map.put('b', Enum4.B);
        _beansimpleEnum4$$javalangCharacterMap.put(Enum4.A, 'a');
        _javalangCharacter$$beansimpleEnum4Map.put('a', Enum4.A);
        _beansimpleEnum3$$javalangByteMap.put(Enum3.B, (byte)2);
        _javalangByte$$beansimpleEnum3Map.put((byte)2, Enum3.B);
        _beansimpleEnum3$$javalangByteMap.put(Enum3.A, (byte)1);
        _javalangByte$$beansimpleEnum3Map.put((byte)1, Enum3.A);
    }

    public Object toSource(Object var1) {
        Bean17 var2 = (Bean17)var1;
        Bean16 var3 = new Bean16();
        Byte var4 = var2.getE1();
        Enum3 var6 = (Enum3)_javalangByte$$beansimpleEnum3Map.get(var4);
        var3.setE1(var6);
        Enum3 var7 = var2.getE2();
        Byte var9 = (Byte)_beansimpleEnum3$$javalangByteMap.get(var7);
        var3.setE2(var9);
        Character var10 = var2.getE3();
        Enum4 var12 = (Enum4)_javalangCharacter$$beansimpleEnum4Map.get(var10);
        var3.setE3(var12);
        Enum4 var13 = var2.getE4();
        Character var15 = (Character)_beansimpleEnum4$$javalangCharacterMap.get(var13);
        var3.setE4(var15);
        return var3;
    }

    public Object toTarget(Object var1) {
        Bean16 var2 = (Bean16)var1;
        Bean17 var3 = new Bean17();
        Enum3 var4 = var2.getE1();
        Byte var6 = (Byte)_beansimpleEnum3$$javalangByteMap.get(var4);
        byte var5 = var6.byteValue();
        var3.setE1(var5);
        Byte var7 = var2.getE2();
        Enum3 var9 = (Enum3)_javalangByte$$beansimpleEnum3Map.get(var7);
        var3.setE2(var9);
        Enum4 var10 = var2.getE3();
        Character var12 = (Character)_beansimpleEnum4$$javalangCharacterMap.get(var10);
        char var11 = var12.charValue();
        var3.setE3(var11);
        Character var13 = var2.getE4();
        Enum4 var15 = (Enum4)_javalangCharacter$$beansimpleEnum4Map.get(var13);
        var3.setE4(var15);
        return var3;
    }

    public Bean16$$Bean17$$Convertor$$ee4161d49f84e5095c68f2abb383a872() {
    }
}
