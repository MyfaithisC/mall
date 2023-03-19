package com.example.mall.ware.vo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import  java.util.List;
@Data
public class PushaseDoneVo {
    @NotNull
    private  Long id;
    private  List<PurchaseItemDoneVo> item;
}
