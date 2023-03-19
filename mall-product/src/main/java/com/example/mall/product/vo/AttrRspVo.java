package com.example.mall.product.vo;

import lombok.Data;

@Data
public class AttrRspVo  extends  AttrVo{
    //所属分类的名字
    private   String  catelogName;

    //所属分组的名字
    private   String groupName;

    private   Long[]  catelogPath;
}
