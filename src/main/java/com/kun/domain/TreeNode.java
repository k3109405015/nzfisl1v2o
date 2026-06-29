package com.kun.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用树节点，支持挂载子节点与业务数据。
 *
 * @param <T> 节点关联的业务数据类型
 */
@Data
@NoArgsConstructor
public class TreeNode<T> {

    private Long id;

    private Long parentId;

    private String name;

    private List<TreeNode<T>> children = new ArrayList<>();

    private List<T> data = new ArrayList<>();

    /**
     * 构造树节点。
     *
     * @param id       节点 id
     * @param parentId 父节点 id
     * @param name     节点名称
     */
    public TreeNode(Long id, Long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

}
