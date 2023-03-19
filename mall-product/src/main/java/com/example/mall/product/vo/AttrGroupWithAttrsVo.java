package com.example.mall.product.vo;

import com.example.mall.product.entity.AttrEntity;
import com.example.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import  java.util.List;
@Data
public class AttrGroupWithAttrsVo  extends   AttrGroupEntity{
    private List<AttrEntity>  attrs;
}
