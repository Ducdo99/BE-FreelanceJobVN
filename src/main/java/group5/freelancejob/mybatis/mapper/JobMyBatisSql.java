package group5.freelancejob.mybatis.mapper;

import org.apache.ibatis.jdbc.SQL;

public class JobMyBatisSql {
    public String getJobByName(String name) {
        return new SQL(){{
            SELECT("*");
            FROM("Job");
            WHERE("title like concat('%', #{name}, '%')");
        }}.toString();
    }
}
