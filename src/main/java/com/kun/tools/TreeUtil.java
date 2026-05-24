package com.kun.tools;

import com.kun.annotation.TreeId;
import com.kun.annotation.TreeParentId;
import com.kun.domain.TreeNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreeUtil {

    private static class Meta {
        Field idField;
        Field parentField;
    }

    private static final Map<Class<?>, Meta> CACHE = new ConcurrentHashMap<>();

    public static <T> List<TreeNode<T>> build(List<T> list, Class<T> clazz, Map<Long, String> nameMap) {
        return build(list, clazz, null, nameMap);
    }

    private static <T> List<TreeNode<T>> build(List<T> list, Class<T> clazz, Long rootParentId,
                                               Map<Long, String> nameMap) {

        AssertUtil.notNull(list, "nameMap must not be null");
        AssertUtil.notNull(clazz, "clazz must not be null");
        AssertUtil.notNull(nameMap, "nameMap must not be null");

        Meta meta = CACHE.computeIfAbsent(clazz, TreeUtil::scanMeta);

        Map<Long, TreeNode<T>> map = new HashMap<>();

        // 1. build nodes O(n)
        for (T item : list) {

            Long id = BeanUtil.getFieldValue(item, meta.idField, Long.class);
            Long pid = BeanUtil.getFieldValue(item, meta.parentField, Long.class);

            TreeNode<T> node = map.computeIfAbsent(id, k -> {
                TreeNode<T> n = new TreeNode<>();
                n.setId(id);
                n.setParentId(pid);
                n.setName(nameMap.get(id));
                return n;
            });

            node.getData().add(item);
        }

        List<TreeNode<T>> roots = new ArrayList<>();

        // 2. build tree O(n)
        for (TreeNode<T> node : map.values()) {

            Long pid = node.getParentId();

            if (pid == null || !map.containsKey(pid)) {
                roots.add(node);
            } else {
                map.get(pid).getChildren().add(node);
            }
        }

        return roots;
    }

    private static Meta scanMeta(Class<?> clazz) {
        Meta meta = new Meta();

        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);

            if (f.isAnnotationPresent(TreeId.class)) {
                meta.idField = f;
            }

            if (f.isAnnotationPresent(TreeParentId.class)) {
                meta.parentField = f;
            }
        }

        if (meta.idField == null || meta.parentField == null) {
            throw new IllegalArgumentException("Missing annotations in " + clazz.getName());
        }

        return meta;
    }

}
