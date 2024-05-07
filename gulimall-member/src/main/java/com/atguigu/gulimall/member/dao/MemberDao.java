package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author waterplants
 * @email 1017834057@qq.com
 * @date 2024-05-07 13:23:58
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
