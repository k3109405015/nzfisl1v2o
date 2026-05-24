package com.kun.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TreeNode<T> {

    private Long id;

    private Long parentId;

    private String name;

    private List<TreeNode<T>> children = new ArrayList<>();

    private List<T> data = new ArrayList<>();

    public TreeNode(Long id, Long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

}
