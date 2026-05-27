package com.kun.expression;

import com.kun.tools.AssertUtil;
import com.kun.tools.ObjectUtil;
import org.apache.commons.jexl3.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ComplexExpressionEvaluator {

    // JEXL
    private final static JexlEngine jexl;

    static {
        jexl = new JexlBuilder().strict(true) // 严格报错
                .silent(false) // 不静默异常
                .create();
    }

    /**
     * JEXL 编译缓存
     */
    private static final ConcurrentMap<String, JexlExpression> CACHE =
            new ConcurrentHashMap<>();

    /**
     * 表达式评估
     * <pre>
     * Complex KRAS WildType && BRAF WildType
     * </pre>
     *
     * @param hitSet       命中的集合
     * @param expr         原始表达式（可能包含前缀，如 Complex）
     * @param removePrefix 需要移除的前缀（如 Complex）
     * @return true=表达式命中，false=未命中
     */
    public static boolean evaluate(Set<String> hitSet, String expr, String removePrefix) {
        if (ObjectUtil.isEmpty(hitSet)) {
            return false;
        }
        // 去除表达式前缀
        String input = expr.startsWith(removePrefix + " ") ? expr.substring((removePrefix + " ").length()) : expr;
        // 表达式
        String parse = parseContains(input);
        // context
        JexlContext context = new MapContext();
        context.set("hitSet", hitSet);
        // 执行
        JexlExpression expression = CACHE.computeIfAbsent(parse, jexl::createExpression);
        return (boolean) expression.evaluate(context);
    }

    public static boolean evaluate(Set<String> hitSet, String expr) {
        return evaluate(hitSet, expr, "Complex");
    }

    /**
     * <p>
     * 将表达式转换为 {@code hitSet.contains("xxx")} 形式
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     *
     * <pre>
     * EGFR && ALK
     * =>
     * hitSet.contains("EGFR") && hitSet.contains("ALK")
     * </pre>
     */
    private static String parseContains(String str) {
        AssertUtil.notNull(str, "str must not be null");
        StringBuilder o = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '|' || c == '&' || c == '(' || c == ')' || c == '!') {
                if (!sb.isEmpty()) {
                    o.append("hitSet.contains('").append(sb.toString().trim()).append("')");
                    if (c == '|' || c == '&') {
                        o.append(" ");
                    }
                    o.append(c);
                } else {
                    o.append(c);
                }
                sb.setLength(0);
                continue;
            }

            if (sb.isEmpty() && c == ' ') {
                o.append(c);
                continue;
            }
            sb.append(c);
        }
        if (!sb.isEmpty()) {
            o.append("hitSet.contains('").append(sb.toString().trim()).append("')");
            sb.setLength(0);
        }
        return o.toString();
    }

}
