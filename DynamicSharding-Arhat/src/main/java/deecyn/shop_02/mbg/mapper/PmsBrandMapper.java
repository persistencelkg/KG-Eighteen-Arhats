package deecyn.shop_02.mbg.mapper;

import org.lkg.model.PmsBrand;

public interface PmsBrandMapper {
    int deleteByPrimaryKey(String variable);

    int insert(PmsBrand record);

    int insertSelective(PmsBrand record);

    PmsBrand selectByPrimaryKey(String variable);

    int updateByPrimaryKeySelective(PmsBrand record);

    int updateByPrimaryKey(PmsBrand record);
}