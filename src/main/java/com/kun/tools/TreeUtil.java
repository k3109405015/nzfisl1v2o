package com.kun.tools;

import com.kun.annotation.TreeId;
import com.kun.annotation.TreeParentId;
import com.kun.domain.TreeNode;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 树形结构构建工具类。
 *
 * <p>基于 {@link TreeId} 与 {@link TreeParentId} 注解，将扁平列表转换为树形结构。</p>
 *
 * @author GaoYu
 */
public class TreeUtil {

    /**
     * 树节点元数据，缓存 id 与 parentId 字段。
     */
    private static class Meta {
        /**
         * 标识节点 id 的字段。
         */
        Field idField;
        /**
         * 标识父节点 id 的字段。
         */
        Field parentField;
    }

    private static final Map<Class<?>, Meta> CACHE = new ConcurrentHashMap<>();

    /**
     * 构建树形结构，并支持按层级路径补全祖先节点。
     *
     * @param list      扁平数据列表
     * @param clazz     数据类型
     * @param hierarchy 层级 id 路径，第一个元素为顶级节点 id
     * @param nameMap   节点 id 与名称映射
     * @param <T>       数据类型泛型
     * @return 树形根节点列表
     */
    public static <T> List<TreeNode<T>> build(List<T> list, Class<T> clazz, List<Long> hierarchy,
                                              Map<Long, String> nameMap) {
        return build(list, clazz, null, nameMap, hierarchy);
    }

    /**
     * 构建树形结构。
     *
     * @param list    扁平数据列表
     * @param clazz   数据类型
     * @param nameMap 节点 id 与名称映射
     * @param <T>     数据类型泛型
     * @return 树形根节点列表
     */
    public static <T> List<TreeNode<T>> build(List<T> list, Class<T> clazz, Map<Long, String> nameMap) {
        return build(list, clazz, null, nameMap, null);
    }

    /**
     * 构建树形结构的核心实现。
     *
     * @param list         扁平数据列表
     * @param clazz        数据类型
     * @param rootParentId 根节点的父 id，可为 null
     * @param nameMap      节点 id 与名称映射
     * @param hierarchy    层级 id 路径，不为空时补全祖先节点
     * @param <T>          数据类型泛型
     * @return 树形根节点列表
     */
    private static <T> List<TreeNode<T>> build(List<T> list, Class<T> clazz, Long rootParentId,
                                               Map<Long, String> nameMap, List<Long> hierarchy) {

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

        if (!ObjectUtil.isEmpty(hierarchy)) {
            return completeHierarchy(roots, hierarchy, nameMap);
        }

        return roots;
    }

    /**
     * 扫描类中标注了 {@link TreeId} 与 {@link TreeParentId} 的字段。
     *
     * @param clazz 目标类型
     * @return 树节点元数据
     */
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

    /**
     * 补全层级结构，第一个元素是顶级元素。
     *
     * @param roots     当前根节点列表
     * @param hierarchy 层级 id 路径
     * @param nameMap   节点 id 与名称映射
     * @param <T>       数据类型泛型
     * @return 补全后的根节点列表
     */
    public static <T> List<TreeNode<T>> completeHierarchy(
            List<TreeNode<T>> roots, List<Long> hierarchy, Map<Long, String> nameMap) {

        TreeNode<T> currentRoot = roots.get(0);

        if (ObjectUtil.isEmpty(currentRoot.getParentId())) {
            return roots;
        }
        int index = hierarchy.indexOf(currentRoot.getParentId());

        if (index == -1) {
            return roots;
        }

        // 从父节点位置向上追溯，逐层构建完整的层级结构
        for (int i = index; i >= 0; i--) {
            Long id = hierarchy.get(i);
            TreeNode<T> parent = new TreeNode<>();
            parent.setId(id);
            parent.setName(nameMap.get(id));
            parent.getChildren().add(currentRoot);
            currentRoot.setParentId(id);
            currentRoot = parent;
        }

        return Collections.singletonList(currentRoot);
    }

}
