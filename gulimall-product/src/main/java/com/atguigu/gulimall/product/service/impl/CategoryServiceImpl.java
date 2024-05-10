package com.atguigu.gulimall.product.service.impl;

import cn.hutool.Hutool;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity -> {
            return categoryEntity.getParentCid().equals(0L);
        })).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, entities));
            return categoryEntity;
        }).sorted((category1, category2) -> {
            return Optional.ofNullable(category1.getSort()).orElse(0) - Optional.ofNullable(category2.getSort()).orElse(0);
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenusByIds(Long[] catIds) {
        ArrayList<Long> catIdsList=new ArrayList<>();
        for (Long catId : catIds) {
            catIdsList.add(catId);
        }
        baseMapper.deleteBatchIds(catIdsList);
    }

    private List<CategoryEntity> getChildren(CategoryEntity nowCategory, List<CategoryEntity> allCategories) {
        return allCategories.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(nowCategory.getCatId());
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity,allCategories));
            return categoryEntity;
        }).sorted((category1, category2) -> {
            return Optional.ofNullable(category1.getSort()).orElse(0) - Optional.ofNullable(category2.getSort()).orElse(0);
        }).collect(Collectors.toList());
    }

}