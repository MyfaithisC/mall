package com.example.mall.ware.vo;
import lombok.Data;

import  java.util.List;
@Data
public class MergeVo {
    //整单ID
    private   Long purchaseId;
    //合并项集合
    private   List<Long>  items;
}
