package member.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import member.domain.MemberDTO;
import member.domain.PointDTO;
import shop.domain.ProductDTO;
import util.security.AES256;
import util.security.SecretMykey;

public class PointDAO_imple implements PointDAO {

	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private AES256 aes;
	
	public PointDAO_imple() {
		
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


	/*
	// 아이디, 이름, 사용가능 적립금, 누적 적립금, 사용한 적립금(합쳐서 한개)
	@Override
	public PointDTO getUserPointDetails(String userid) throws SQLException {
		
		PointDTO podto = null;
		
		try {
			conn = ds.getConnection();
			
			String sql = " SELECT M.UserID "
					   + "      , M.Name "
					   + "      , TO_CHAR(NVL(A.AvailablePoints, 0)) AS AvailablePoints "
				       + "      , TO_CHAR(NVL(B.TotalPoints, 0)) AS TotalPoints "
					   + "      , TO_CHAR(NVL(C.UsedPoints, 0)) AS UsedPoints "
					   + " FROM ( "
					   + "    SELECT UserID, SUM(TO_NUMBER(Poincome)) AS AvailablePoints "
					   + "    FROM POINT "
					   + "    WHERE PoStatus = '1' "
					   + "    GROUP BY UserID "
					   + " ) A "
					   + " JOIN ( "
					   + "    SELECT UserID, SUM(TO_NUMBER(Poincome)) AS TotalPoints "
					   + "    FROM POINT "
					   + "    GROUP BY UserID "
					   + " ) B "
					   + " ON A.UserID = B.UserID "
					   + " LEFT JOIN ( "
					   + "    SELECT UserID, SUM(TO_NUMBER(Poincome)) AS UsedPoints "
					   + "    FROM POINT "
					   + "    WHERE PoStatus = '2' "
					   + "    GROUP BY UserID "
					   + " ) C "
					   + " ON A.UserID = C.UserID "
					   + " INNER JOIN member M "
					   + " ON A.UserID = M.UserID "
					   + " WHERE M.UserID = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
            if (rs.next()) {
                podto = new PointDTO();
                podto.setUserID(rs.getString("UserID"));
                
                MemberDTO mdto = new MemberDTO();
                mdto.setName(rs.getString("Name"));
                podto.setMdto(mdto);
                
                podto.setAvailablePoints(rs.getString("AvailablePoints"));
                podto.setTotalPoints(rs.getString("TotalPoints"));
                podto.setUsedPoints(rs.getString("UsedPoints"));
            }
			
		} finally {
			close();
		}
		
		return podto;
	} // end of public PointDTO getUserPointDetails(String userid) throws SQLException
	*/

	/*
	// 소멸예정 적립금 리스트 가져오기 (소멸예정 적립금, 날짜, 남은일자)
	@Override
	public List<PointDTO> getExpiryDetailsList(String userid) throws SQLException {
		
		List<PointDTO> expiryList = null;
		boolean isSearch = true;
		
		try {
			conn = ds.getConnection();
			
			String sql = " SELECT NVL(POINCOME, '0') AS ExpiringPoints "
					   + "      , poExpiryDate "
					   + "      , TO_CHAR(TRUNC(TO_DATE(poExpiryDate, 'YYYY-MM-DD HH24:MI:SS') - SYSDATE)) AS DaysUntilExpiry "
					   + " FROM POINT "
					   + " WHERE PoStatus = '1' AND TRUNC(TO_DATE(poExpiryDate, 'YYYY-MM-DD HH24:MI:SS') - SYSDATE) <= 7 "
					   + " AND UserID = ? "
				   	   + " ORDER BY DaysUntilExpiry ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				if(isSearch) {
					expiryList = new ArrayList<>();
					isSearch = false;
				}
				
				PointDTO podto = new PointDTO();
				
				podto.setExpiringPoints(rs.getString("ExpiringPoints"));   // 소멸예정포인트
				podto.setPoExpiryDate(rs.getString("poExpiryDate")); 	   // 만료일
				podto.setDaysUntilExpiry(rs.getString("DaysUntilExpiry")); // 만료까지 남을일수
				
				expiryList.add(podto);
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return expiryList;
	} // end of public List<PointDTO> getExpiryDetailsList(String userid) throws SQLException ------
	*/

	
	// 아이디, 이름, 사용가능 적립금, 누적 적립금, 사용한 적립금(합쳐서 한개)
	@Override
	public PointDTO getUserPointDetails(String userid) throws SQLException {
		
		PointDTO podto = null;
		
		try {
			conn = ds.getConnection();
			
			String sql = " SELECT m.NAME "
					   + "      , p.UserID "
					   + "      , SUM(CASE WHEN p.PODETAIL LIKE '%주문' THEN TO_NUMBER(p.POINCOME) ELSE 0 END) AS AvailablePoints "
					   + "      , SUM(CASE WHEN p.PODETAIL LIKE '%상품구입' THEN TO_NUMBER(p.POINCOME) ELSE 0 END) AS UsedPoints "
					   + "      , SUM(CASE WHEN p.PODETAIL LIKE '%주문' THEN TO_NUMBER(p.POINCOME) ELSE 0 END) + "
					   + "        SUM(CASE WHEN p.PODETAIL LIKE '%상품구입' THEN TO_NUMBER(p.POINCOME) ELSE 0 END) AS TotalPoints "
					   + " FROM point p "
					   + " JOIN member m ON p.USERID = m.USERID "
					   + " WHERE (p.PODETAIL LIKE '%주문' OR p.PODETAIL LIKE '%상품구입') AND p.USERID = ? "
					   + " GROUP BY m.NAME, p.USERID ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
            if (rs.next()) {
                podto = new PointDTO();
                podto.setUserID(rs.getString("UserID"));
                
                MemberDTO mdto = new MemberDTO();
                mdto.setName(rs.getString("Name"));
                podto.setMdto(mdto);
                
                podto.setAvailablePoints(rs.getString("AvailablePoints")); // 사용가능포인트
                podto.setUsedPoints(rs.getString("UsedPoints"));           // 사용한포인트
                podto.setTotalPoints(rs.getString("TotalPoints"));         // 누적포인트
            }
			
		} finally {
			close();
		}
		
		return podto;
	} // end of public PointDTO getUserPointDetails(String userid) throws SQLException
	

	// 유저가 적립한 포인트 로그 가져오기
	@Override
	public List<PointDTO> getUserPointHistoryList(String userid) throws SQLException {
		List<PointDTO> pointHistoryList = null;
		boolean isSearch = true;
		
		try {
			conn = ds.getConnection();
			
			String sql = " SELECT POINCOME, PODETAIL, PODATE "
					   + " FROM POINT "
					   + " WHERE PODETAIL LIKE '%주문' and USERID = ? "
				   	   + " ORDER BY PODATE DESC ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				if(isSearch) {
					pointHistoryList = new ArrayList<>();
					isSearch = false;
				}
				
				PointDTO podto = new PointDTO();
				
				podto.setPoIncome(rs.getString("POINCOME"));   	// 변동 포인트
				podto.setPoDetail(rs.getString("PODETAIL")); 	// 적립내용
				podto.setPoDate(rs.getString("PODATE"));		// 적립날짜
				
				pointHistoryList.add(podto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return pointHistoryList;
	} // end of public List<PointDTO> getUserPointDetailsList(String userid) throws SQLException ---


	// 유저가 사용한 포인트 로그 가져오기
	@Override
	public List<PointDTO> getUserPointUsedHistoryList(String userid) {
		List<PointDTO> pointUsedHistoryList = null;
		boolean isSearch = true;
		
		try {
			conn = ds.getConnection();
			
			String sql = " SELECT POINCOME, PODETAIL, PODATE "
					   + " FROM POINT "
					   + " WHERE PODETAIL LIKE '%상품구입' and USERID = ? "
				   	   + " ORDER BY PODATE DESC ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				if(isSearch) {
					pointUsedHistoryList = new ArrayList<>();
					isSearch = false;
				}
				
				PointDTO podto = new PointDTO();
				
				podto.setPoIncome(rs.getString("POINCOME"));   	// 변동 포인트
				podto.setPoDetail(rs.getString("PODETAIL")); 	// 적립내용
				podto.setPoDate(rs.getString("PODATE"));		// 적립날짜
				
				pointUsedHistoryList.add(podto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return pointUsedHistoryList;
	} // end of public List<PointDTO> getUserPointUsedHistoryList(String userid) ------
	
}