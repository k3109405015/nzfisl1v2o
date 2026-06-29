import com.kun.tools.IDUtil;

/**
 * ID 生成测试入口类。
 */
public class a {

    /**
     * 程序入口，用于测试短 ID 生成。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String s = IDUtil.genShortId("prefix", 10);
        System.out.println(s);
    }

}
