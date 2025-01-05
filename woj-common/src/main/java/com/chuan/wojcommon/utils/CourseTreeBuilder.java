package com.chuan.wojcommon.utils;

import lombok.Data;

import java.util.*;

/**
 * 课程树构造器
 *
 * @Author: chuan-wxy
 * @Date: 2024/10/1 14:25
 * @Description: 将数据库中的课程类，构造成json树返回给前端
 */
public class CourseTreeBuilder {

    @Data
    public static class Category {
        private long id;
        private long pid;
        private String avatar;
        private String description;
        private String name;
        private List<Category> children = new ArrayList<>();
        public Category(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addChild(Category child) {
            this.children.add(child);
        }

    }

    public static Map<Integer, Category> buildTree(List<Category> categories,int root) {
        System.out.println(categories);
        Map<Long, Category> categoryMap = new HashMap<>();

        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
        }
        // 插入根节点
        Category rootCategory = new Category(root,"root");
        rootCategory.setPid(-1);
        categoryMap.put(Long.valueOf(root), rootCategory);

        for (Category category : categories) {
            System.out.println(category);
            Long parentId = category.getPid();
            if (parentId != -1) {
                Category parent = categoryMap.get(parentId);
                if (parent != null) {
                    parent.addChild(category);
                    System.out.println(parent);
                }
            }
        }

        return Collections.singletonMap(root, categoryMap.get(Long.valueOf(root)));
    }
}
