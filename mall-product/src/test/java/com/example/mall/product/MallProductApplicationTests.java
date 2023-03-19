package com.example.mall.product;



import com.example.mall.product.entity.BrandEntity;
import com.example.mall.product.service.BrandService;
import com.example.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {
    @Resource
    private BrandService pmsBrandService;

    @Autowired
    private CategoryService categoryService;
    @Test
    public  void contextLoads() {
        BrandEntity brandEntity = new   BrandEntity();
        brandEntity.setDescript("hello");
        brandEntity.setName("华为");
        brandEntity.setBrandId(1L);
        pmsBrandService.save(brandEntity);
        System.out.println("保存成功");
    }
    @Test
    public void  findPaths(){
        Long[] catelogPath = categoryService.findCatelogPath(255L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }
}
