package org.lkg.decorate;

/**
 * @date: 2025/5/24 21:51
 * @author: li kaiguang
 */
public class EncryptDataLoader extends DataLoaderDecorator{

    public EncryptDataLoader(DataLoader dataLoader) {
        super(dataLoader);
    }

    @Override
    public String read() {
        String read = super.read();
        System.out.println("开始解密----");
        read = read.substring(0, read.indexOf("LKG"));
        System.out.println("解密成功---");
        return read;
    }


    @Override
    public void write(String str) {
        System.out.println("开始加密：" + str);
        str = str+"LKG";
        super.write(str);
        System.out.println("加密完成:" + str);
    }
}
