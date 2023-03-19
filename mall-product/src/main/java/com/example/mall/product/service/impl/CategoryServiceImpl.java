package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;
import com.example.mall.product.dao.CategoryDao;
import com.example.mall.product.entity.CategoryBrandRelationEntity;
import com.example.mall.product.entity.CategoryEntity;
import com.example.mall.product.service.CategoryBrandRelationService;
import com.example.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
      @Autowired
      private CategoryBrandRelationService categoryBrandRelationService;
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
        //查出分类表所有数据
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        //查询出所有的一级分类数据.  使用stream的api
        List<CategoryEntity> firstEntityList = entityList.stream().
                filter(categoryEntity -> categoryEntity.getParentCid().equals(0L)).map((menu) -> {
                    //传递当前的商品分类, 和所有的分类,递归查询出子分类
                    menu.setChildren( getChildrens(menu,entityList));
                    return menu;
                } ).sorted((menu1,menu2)->{
                    //进行排序
                    return (menu1.getSort() ==null?0:menu1.getSort())- (menu2.getSort()==null?0 : menu2.getSort());
                }).collect(Collectors.toList());
        return firstEntityList;
    }

    @Override
    public void removeMenusByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }
    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){

         List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
             assert categoryEntity != null;
             assert categoryEntity.getParentCid() != null;
             assert root != null;
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //2、菜单的排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * * 根据categoryId找到CatelogPath的完整路径
     *  [父路径/子路径/孙子路径]*  [2,22,267]
     * @param categoryId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long categoryId) {
        //用于收集信息的容器
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(categoryId, paths);
        Collections.reverse(parentPath);
        return   parentPath.toArray(new Long[parentPath.size()]);
    }
    //[255,50,2]
    private  List<Long>  findParentPath(Long categoryId, List<Long> paths){
        //收集当前节点和递归查询的父节点
        paths.add(categoryId);
        //查询是否有父节点
        CategoryEntity categoryEntity = baseMapper.selectById(categoryId);
        if(categoryEntity.getParentCid()!=0){
           findParentPath(categoryEntity.getParentCid(),paths);
        }
        return   paths;
    }

    /**
     * * 进行分类的级联更新,当更新分类名称时,
     * 将也会更新其他的表的分类名称的字段*
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
         this.updateById(category);
         if(!StringUtils.isEmpty(category.getName())){
             UpdateWrapper<CategoryBrandRelationEntity> wrapper = new UpdateWrapper<>();
             wrapper.eq("catelog_id",category.getCatId()).set("catelog_name",category.getName());
             categoryBrandRelationService.update(wrapper);
         }
    }
}