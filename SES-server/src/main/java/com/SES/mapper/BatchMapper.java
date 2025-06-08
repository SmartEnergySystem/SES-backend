package com.SES.mapper;

import com.SES.dto.batch.BatchPageQueryDTO;
import com.SES.entity.Batch;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchMapper {

    @Insert("INSERT INTO batch (user_id, name, createtime, updatetime) " +
            "VALUES (#{userId}, #{name}, #{createtime}, #{updatetime})")
    void insert(Batch batch);

    @Delete("DELETE FROM batch WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM batch WHERE id = #{id}")
    Batch getById(Long id);

    @Update("UPDATE batch SET name = #{name}, updatetime = #{updatetime} WHERE id = #{id}")
    void updateName(@Param("id") Long id,
                    @Param("name") String name,
                    @Param("updatetime") java.time.LocalDateTime updatetime);

    List<Batch> pageQuery(BatchPageQueryDTO queryDTO);
}