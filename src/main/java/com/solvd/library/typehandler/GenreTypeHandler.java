package com.solvd.library.typehandler;

import com.solvd.library.domain.Genre;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Genre.class)
@MappedJdbcTypes(JdbcType.BIGINT)
public class GenreTypeHandler extends BaseTypeHandler<Genre> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Genre parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getId());
    }

    @Override
    public Genre getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Long id = rs.getLong(columnName);
        if (rs.wasNull() || id == null) {
            return null;
        }
        return Genre.fromId(id);
    }

    @Override
    public Genre getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Long id = rs.getLong(columnIndex);
        if (rs.wasNull() || id == null) {
            return null;
        }
        return Genre.fromId(id);
    }

    @Override
    public Genre getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Long id = cs.getLong(columnIndex);
        if (cs.wasNull() || id == null) {
            return null;
        }
        return Genre.fromId(id);
    }
}