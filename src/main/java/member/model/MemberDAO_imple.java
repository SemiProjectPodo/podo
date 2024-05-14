package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import member.domain.MemberDTO;
import util.security.AES256;
import util.security.SecretMykey;
import util.security.Sha256;

public class MemberDAO_imple implements MemberDAO {

	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private AES256 aes;
	
	public MemberDAO_imple() {
		
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup("jdbc/semioracle");
			
			aes = new AES256(SecretMykey.KEY);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}// end of public MemberDAO_imple()
	
	
	
	// 자원 반납
	private void close() {
		
		try {
			if(rs != null)    {rs.close();    rs=null;}
			if(pstmt != null) {pstmt.close(); pstmt=null;}
			if(conn != null)  {conn.close();  conn=null;}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}// end of private void close()

	
	
	// 회원가입
	@Override
	public int doRegister(MemberDTO mdto) throws SQLException {
		
		int result = 0;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = "INSERT INTO MEMBER "
					+ " (USERID, PWD, NAME, EMAIL, PHONE, ADDRESS, ADDRESSDETAIL, GENDER, BIRTHDAY) "
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, mdto.getUserid());
			pstmt.setString(2, Sha256.encrypt(mdto.getPwd()));
			pstmt.setString(3, mdto.getName());
			pstmt.setString(4, aes.encrypt(mdto.getEmail()) );
			pstmt.setString(5, aes.encrypt(mdto.getPhone()));
			pstmt.setString(6, mdto.getAddress());
			pstmt.setString(7, mdto.getAddressDetail());
			pstmt.setString(8, mdto.getGender());
			pstmt.setString(9, mdto.getBirthday());

			result = pstmt.executeUpdate();
			
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return result;
		
	}// end of public int doRegister(MemberDTO mdto)

	@Override
	public MemberDTO signin(Map<String, String> paraMap) throws SQLException {
		
		MemberDTO mdto = null;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " select userid, name, email, phone, address, addressdetail, gender, birthday, point, registerday, pwdupdateday, memberidx "
					+ " from MEMBER where userid = ? and pwd = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, Sha256.encrypt(paraMap.get("pwd")));
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				mdto = new MemberDTO();
				
				mdto.setUserid(rs.getString(1));
				mdto.setName(rs.getString(2));
				mdto.setEmail(aes.encrypt(rs.getString(3)));
				mdto.setPhone(aes.encrypt(rs.getString(4)));
				mdto.setAddress(rs.getString(5));
				mdto.setAddressDetail(rs.getString(6));
				mdto.setGender(rs.getString(7));
				mdto.setBirthday(rs.getString(8));
				mdto.setPoint(rs.getString(9));
				mdto.setRegisterDay(rs.getString(10));
				mdto.setPwdUpdateDay(rs.getString(11));
				mdto.setMemberIdx(rs.getString(12));
				
			}
			
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return mdto;
		
	}// end of public MemberDTO signin(Map<String, String> paraMap)
	
	
	// 아이디 중복 확인
	@Override
	public boolean idDuplicateCheck(String userid) throws SQLException {
		
		boolean isExist = false;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select userid "
					   + " from member "
					   + " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
			isExist = rs.next(); // 행이 있으면(중복된 userid) true,
                				 // 행이 없으면(사용가능한 userid) false
			
		} finally {
			close();
		}
		
		return isExist;
		
	} // end of public boolean idDuplicateCheck(String userid) throws SQLException -----------



	// 이메일 중복 확인
	@Override
	public boolean emailDuplicateCheck(String email) throws SQLException {

		boolean isExist = false;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select email "
					   + " from member "
					   + " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aes.encrypt(email));
			rs = pstmt.executeQuery();
			
			isExist = rs.next(); // 행이 있으면(중복된 userid) true,
                				 // 행이 없으면(사용가능한 userid) false
			
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
			
		} finally {
			close();
		}
		
		return isExist;
		
	} // end of public boolean emailDuplicateCheck(String email) throws SQLException ------------
	
	
	
}