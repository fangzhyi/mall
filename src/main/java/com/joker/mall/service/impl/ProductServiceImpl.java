package com.joker.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.joker.mall.dao.ProductMapper;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.pojo.Product;
import com.joker.mall.service.ICategoryService;
import com.joker.mall.service.IProductService;
import com.joker.mall.vo.ProductDetailVo;
import com.joker.mall.vo.ProductVo;
import com.joker.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.joker.mall.enums.ProductStatusEnum.DELETE;
import static com.joker.mall.enums.ProductStatusEnum.OFF_SALE;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ICategoryService categoryService;

    @Override
    public ResponseVo<PageInfo> list(Integer categroyId, Integer pageNum, Integer pageSize) {

        Set<Integer> categoryIdSet = new HashSet<>();
        if(categroyId != null){
            categoryService.findSubCategoryId(categroyId,categoryIdSet);
            categoryIdSet.add(categroyId);
        }

        PageHelper.startPage(pageNum,pageSize);
        //categoryIdSet.size() == 0?null:categoryIdSet
        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productsVoList = productList
                .stream().map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e,productVo);
                    return productVo;
                })
                .collect(Collectors.toList());


        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productsVoList);

        return ResponseVo.successByData(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {

        Product product = productMapper.selectByPrimaryKey(productId);

        if(product.getStatus().equals(OFF_SALE.getCode())||product.getStatus().equals(DELETE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        //敏感数据处理
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return ResponseVo.successByData(productDetailVo);
    }
}
