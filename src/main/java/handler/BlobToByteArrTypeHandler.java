package handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;

public class BlobToByteArrTypeHandler implements TypeHandler<byte[]> {
    @Override
    public void setParameter(PreparedStatement ps, int i, byte[] parameter, JdbcType jdbcType) throws SQLException {
        //ps.setString(i,parameter);
        ps.setBytes(i,parameter);
    }

    @Override
    public byte[] getResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        return blob.getBytes(1,(int)blob.length());
    }

    @Override
    public byte[] getResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        return blob.getBytes(1,(int)blob.length());
    }

    @Override
    public byte[] getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        return blob.getBytes(1,(int)blob.length());
    }
}
