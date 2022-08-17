package group5.freelancejob.mybatis.handler;

import group5.freelancejob.utils.JobStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobStatusHandler implements TypeHandler<JobStatus> {
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, JobStatus jobStatus, JdbcType jdbcType) throws SQLException {
        if (jobStatus == null) {
            if (jdbcType == null) {
                throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
            }

            try {
                preparedStatement.setNull(i, jdbcType.TYPE_CODE);
            } catch (SQLException var7) {
                throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. Cause: " + var7, var7);
            }
        } else {
            try {
                preparedStatement.setString(i, jobStatus.name());
            } catch (Exception var6) {
                throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . Try setting a different JdbcType for this parameter or a different configuration property. Cause: " + var6, var6);
            }
        }
    }

    @Override
    public JobStatus getResult(ResultSet resultSet, String s) throws SQLException {
        return JobStatus.valueOf(resultSet.getString(s));
    }

    @Override
    public JobStatus getResult(ResultSet resultSet, int i) throws SQLException {
        return JobStatus.valueOf(resultSet.getString(i));
    }

    @Override
    public JobStatus getResult(CallableStatement callableStatement, int i) throws SQLException {
        return JobStatus.valueOf(callableStatement.getString(i));
    }
}
