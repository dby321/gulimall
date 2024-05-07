package com.atguigu.gulimall.product;



import cn.hutool.core.util.RandomUtil;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class GulimallProductApplicationTest {
    @Autowired
    BrandService brandService;
    @Test
    public void testHelloworld(){

    }
    @Test
    public void testBrandService(){
        BrandEntity brandEntity=new BrandEntity();
        brandEntity.setBrandId(RandomUtil.randomLong());
        brandEntity.setDescript(RandomUtil.randomString(10));
        brandEntity.setName(RandomUtil.randomString(10));
        brandService.save(brandEntity);
    }

}
