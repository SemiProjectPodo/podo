package coupon.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import coupon.domain.CouponDTO;
import coupon.domain.MyCouponDTO;

public class CouponDAO_imple implements CouponDAO {

	private DataSource ds;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public CouponDAO_imple() {
		
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context)initContext.lookup("java:/comp/env");
			ds = (DataSource)envContext.lookup("jdbc/semioracle");
			
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


	// 할인 쿠폰 등록(admin 만 등록 가능)
	@Override
	public int register(CouponDTO codto) throws SQLException {
		
		int result = 0;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = "INSERT INTO COUPON (CODISCOUNT, CONAME, CODETAIL, COTYPE, CODATE, COCODE) "
					   + "VALUES (?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, codto.getCodiscount());
			pstmt.setString(2, codto.getConame());
			pstmt.setString(3, codto.getCodetail());
			pstmt.setInt(4, codto.getCotype());
			pstmt.setString(5, codto.getCodate());
			pstmt.setString(6, codto.getCocode());
			
			result = pstmt.executeUpdate();
			
		}finally {
			close();
		}
		
		return result;
	} // end of public int register(CouponDTO codto) throws SQLException -------


	// 쿠폰을 삽입하기 전에 쿠폰이 이미 존재하는지 중복 검사(coname이름PK)
	@Override
	public boolean isCouponExist(CouponDTO codto) throws SQLException {
		boolean isCouponExist = false;
		try {
			conn = ds.getConnection();

			String sql = " select CONAME " 
					   + " from COUPON " 
					   + " where CONAME = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, codto.getConame());

			rs = pstmt.executeQuery();

			isCouponExist = rs.next(); // 행이 있으면(중복된 userid) true,
									   // 행이 없으면(사용가능한 userid) false
		} finally {
			close();
		}
		return isCouponExist;
	} // end of public boolean isCouponExist(CouponDTO codto) throws SQLException ----


	@Override
	public List<MyCouponDTO> getMyList(String userid) throws SQLException {
		
		List<MyCouponDTO> mycodtoList = new ArrayList<>();
				
		try {
			
			conn = ds.getConnection();
			
			String sql = " select COUPON.CONAME as coname, COTYPE, CODISCOUNT, CODATE, COREGISTERDAY, COINDEX, costatus "
					+ " from COUPON join MYCOUPON on COUPON.CONAME = MYCOUPON.CONAME join MEMBER on MYCOUPON.USERID = MEMBER.USERID "
					+ " where MEMBER.USERID = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				if(rs.getInt("costatus")==1) {
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String codate = rs.getString("codate");
					
					Date now = new Date();
					Date finshLine = format.parse(codate);
				    now = format.parse(format.format(now));
				    
					if(now.compareTo(finshLine)>0) {
						
						int coindex = rs.getInt("COINDEX");
						
						sql = " UPDATE MYCOUPON SET COSTATUS = 3 WHERE COINDEX = ?";
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, coindex);
						
						if(1!=pstmt.executeUpdate()) {
							System.out.println("쿠폰기간 처리중 오류 발생");
						}
						
					}else {
						MyCouponDTO mycodto = new MyCouponDTO();
						
						CouponDTO codto = new CouponDTO();
						
						codto.setConame(rs.getString("coname"));
						codto.setCotype(rs.getInt("cotype"));
						codto.setCodiscount(rs.getInt("codiscount"));
						codto.setCodate(rs.getString("codate"));
						codto.setCoregisterday(rs.getString("coregisterday"));
						
						mycodto.setCodto(codto);
						mycodto.setCoindex(rs.getInt("COINDEX"));
						mycodto.setCostatus(rs.getInt("costatus"));
						
						mycodtoList.add(mycodto);
					}
				
				}
				
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}finally {
			close();
		}
		
		return mycodtoList;
	}


	@Override
	public CouponDTO getcouponinfo(String coname) throws SQLException {
		
		CouponDTO codto = null;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " select COTYPE,CODISCOUNT from COUPON where coname = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, coname);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				codto = new CouponDTO();
				
				codto.setCotype(rs.getInt("COTYPE"));
				codto.setCodiscount(rs.getInt("CODISCOUNT"));
				
			}
			
			
		}finally {
			close();
		}
		
		return codto;
	}

	@Override
	public int useCoupon(MyCouponDTO mycodto) throws SQLException {
		
		int result = 0;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = "UPDATE MYCOUPON "
					+ " SET COSTATUS = 2 "
					+ " WHERE CONAME = ? and userid = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, mycodto.getCodto().getConame());
			pstmt.setString(2, mycodto.getMdto().getUserid());
			
			result = pstmt.executeUpdate();
			
		}finally {
			close();
		}
		
		return result;
	}


	@Override
	public boolean isCoupon(Map<String, String> paraMap) throws SQLException {
		
		boolean result = false;
		
		try {
		
			conn = ds.getConnection();
			
			String sql = "select * from MYCOUPON where USERID = ? and CONAME = ? and COSTATUS = 1";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("coname"));
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
			
		}finally {
			close();
		}
		
		return result;
	
	}


	@Override
	public boolean delCoupon(Map<String, String> paraMap) throws SQLException {
		
		boolean result = false;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " UPDATE MYCOUPON SET COSTATUS = 2 WHERE CONAME = ? and USERID = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("coname"));
			pstmt.setString(2, paraMap.get("userid"));
			
			if(1==pstmt.executeUpdate()) {
				result = true;
			}
			
		}finally {
			close();
		}
		
		return result;
	}


	// 쿠폰등록 (쿠폰번호가 있으면 true 없으면 false 로 해서 값 반환한다.)
	@Override
	public boolean CouponRegistration(Map<String, String> paraMap) throws SQLException {
		
		boolean isExists = false;
		
		try {
			
			conn = ds.getConnection();
			
			String sql = " INSERT INTO mycoupon (COINDEX, USERID, CONAME, COSTATUS)  "
					   + " SELECT SEQ_COINDEX.nextval, ?, C.CONAME, 1  "
					   + " FROM coupon C  "
					   + " WHERE C.COCODE = ? "
					   + " AND TO_DATE(C.CODATE, 'YYYY-MM-DD') >= TO_DATE(TO_CHAR(SYSDATE, 'YYYY-MM-DD'), 'YYYY-MM-DD') "
					   + " AND NOT EXISTS (  "
					   + "             SELECT 1  "
					   + "             FROM mycoupon MC  "
					   + "             WHERE MC.CONAME = C.CONAME AND MC.USERID = ? "
					   + " ) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("cocode"));
			pstmt.setString(3, paraMap.get("userid"));
			
			int n = pstmt.executeUpdate();
			if(n == 1) {
				isExists = true;
			}
			
		}finally {
			close();
		}
		
		return isExists;
	} // end of public boolean CouponRegistration(String cocode) --------


	
	
	@Override
	public List<MyCouponDTO> getMyCouponList(String userid) throws SQLException {
			
		List<MyCouponDTO> mycodtoList = new ArrayList<>();
				
			try {
				
				conn = ds.getConnection();
				
				String sql = " select COUPON.CONAME as coname, COTYPE, CODISCOUNT, CODATE, COREGISTERDAY, COINDEX, costatus "
						+ " from COUPON join MYCOUPON on COUPON.CONAME = MYCOUPON.CONAME join MEMBER on MYCOUPON.USERID = MEMBER.USERID "
						+ " where MEMBER.USERID = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userid);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
	
					MyCouponDTO mycodto = new MyCouponDTO();
					
					CouponDTO codto = new CouponDTO();
					
					codto.setConame(rs.getString("coname"));
					codto.setCotype(rs.getInt("cotype"));
					codto.setCodiscount(rs.getInt("codiscount"));
					codto.setCodate(rs.getString("codate"));
					codto.setCoregisterday(rs.getString("coregisterday"));
					
					mycodto.setCodto(codto);
					mycodto.setCoindex(rs.getInt("COINDEX"));
					mycodto.setCostatus(rs.getInt("costatus"));
					
					mycodtoList.add(mycodto);
			
				}
				
			}
			finally {
				close();
			}
		return mycodtoList;
	} // end of public List<MyCouponDTO> getMyCouponList(String userid) throws SQLException -----


	
	// 내 쿠폰의 총개수 알아오기
	@Override
	public int getTotalMyCouponCount(String userid) throws SQLException {
		int totalMyCouponCount = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select count(*) "
						+ " from mycoupon "
						+ " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userid);
						
			rs = pstmt.executeQuery();
			
			rs.next();
			
			totalMyCouponCount = rs.getInt(1);
			
		} finally {
			close();
		}
		
		return totalMyCouponCount;
	}// end of public int getTotalMyCouponCount(String userid) throws SQLException ---


	// 내 쿠폰의 총 페이지 수 알아오기
	@Override
	public int getTotalPage(String userid) throws SQLException {
		int totalPage = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select ceil(count(*)/5) "
						+ " from mycoupon "
						+ " where userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			rs.next();
			
			totalPage = rs.getInt(1);
			
		} finally {
			close();
		}
		
		return totalPage;	
	}


	// **** 페이징 처리를 한 모든 쿠폰 목록 보여주기 **** //
	@Override
	public List<MyCouponDTO> selectMyCouponpaging(Map<String, String> paraMap) throws SQLException {
		List<MyCouponDTO> MyCouponpagingList = new ArrayList<>();
		
		try {
			conn = ds.getConnection();
			
			String sql =  "  SELECT  "
					    + "    T.rno,  "
						+ "    T.COINDEX, "
						+ "    T.USERID, "
						+ "    T.CONAME, "
						+ "    T.COSTATUS, "
						+ "    C.CODISCOUNT, "
						+ "    C.CODETAIL, "
						+ "    C.COTYPE, "
						+ "    C.CODATE, "
						+ "    C.COREGISTERDAY, "
						+ "    C.COCODE "
						+ " FROM ( "
						+ "    SELECT  "
						+ "        rownum AS rno, "
						+ "        COINDEX, "
						+ "        USERID, "
						+ "        CONAME, "
						+ "        COSTATUS "
						+ "    FROM ( "
						+ "        SELECT  "
						+ "            COINDEX, "
						+ "            USERID, "
						+ "            CONAME, "
						+ "            COSTATUS "
						+ "        FROM "
						+ "            mycoupon "
						+ "        WHERE "
						+ "            userid = ? "
						+ "    ) V "
						+ " ) T "
						+ " JOIN coupon C ON T.CONAME = C.CONAME "
						+ " WHERE T.rno BETWEEN ? AND ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			int currentShowPageNo = Integer.parseInt( paraMap.get("currentShowPageNo") ); 
			int sizePerPage = Integer.parseInt( paraMap.get("sizePerPage") );
			
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setInt(2, (currentShowPageNo * sizePerPage) - (sizePerPage - 1) ); // 공식
			pstmt.setInt(3, (currentShowPageNo * sizePerPage) ); // 공식
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				MyCouponDTO mcdto = new MyCouponDTO();
				mcdto.setCoindex(rs.getInt("COINDEX"));
				mcdto.setCostatus(rs.getInt("COSTATUS"));
				
				CouponDTO cdto = new CouponDTO();
				cdto.setConame(rs.getString("CONAME"));
				cdto.setCodetail(rs.getString("CODETAIL"));
				cdto.setCodate(rs.getString("CODATE"));
				cdto.setCoregisterday(rs.getString("COREGISTERDAY"));
				cdto.setCocode(rs.getString("COCODE"));
				cdto.setCotype(rs.getInt("COTYPE"));
				cdto.setCodiscount(rs.getInt("CODISCOUNT"));
				
				
				// 쿠폰의 유효기간 확인
            	SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
            	Date codate = sdformat.parse(rs.getString("CODATE"));
            	
            	Date now = new Date();	// 현재시각
            	now = sdformat.parse(sdformat.format(now));
            	
            	if (now.compareTo(codate) > 0) { // 오늘이 쿠폰 유효기간보다 이후일 경우
            		mcdto.setCostatus(3);
    			}
				
				mcdto.setCodto(cdto);
				
				MyCouponpagingList.add(mcdto);
			}// end of while(rs.next())---------------------
		}
    	catch (ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			close();
		}
		return MyCouponpagingList;
	} // end of public List<MyCouponDTO> selectMyCouponpaging(Map<String, String> paraMap) -----


	// 사용가능한 쿠폰 개수 가져오기
	@Override
	public int getAvailableCoupons(String userid) throws SQLException {
		int availableCoupons = 0;
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select count(*) "
						+ " from mycoupon "
						+ " where COSTATUS = 1 and userid = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userid);
			rs = pstmt.executeQuery();
			rs.next();
			
			availableCoupons = rs.getInt(1);
			
		} finally {
			close();
		}
		
		return availableCoupons;	
	} // end of public int getAvailableCoupons(String userid) throws SQLException ----
	
	// 관리자 회원관리 - 쿠폰선택
	   @Override
	   public List<CouponDTO> adminCoupon() throws SQLException {
	      
	      List<CouponDTO> codtoList = new ArrayList<>();
	      
	      try {
	         
	         conn = ds.getConnection();
	         
	         String sql = " select codiscount, coname, codetail, cotype, codate, coregisterday, cocode "
	                  + " from coupon ";
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            
	            CouponDTO codto = new CouponDTO();
	            
	            codto.setConame(rs.getString("coname"));
	            
	            codtoList.add(codto);
	            
	         } // end of while(rs.next())
	         
	         
	      } catch(Exception e) {
	         e.printStackTrace();
	      } finally {
	         close();
	      }
	      
	      return codtoList;   
	      
	   } // end of public List<CouponDTO> adminCouponIn() throws SQLException 


	   // 관리자 회원관리 - 쿠폰넣기
	   @Override
	   public int adminCouponIn(Map<String, String> paraMap) throws SQLException {
	      
	      int result = 0;
	      
	      try {
	         
	         conn = ds.getConnection();
	      
	         String sql = " insert into mycoupon(coindex, userid, coname, costatus) VALUES (seq_coindex.nextval, ?, ?, 1) ";
	         
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setString(1, paraMap.get("userid"));
	         pstmt.setString(2, paraMap.get("coname"));
	         
	         result = pstmt.executeUpdate();
	         
	         
	      } catch(Exception e) {
	         
	      } finally {
	         close();
	      }
	      
	      return result;
	      
	   } // end of public List<MyCouponDTO> adminCouponIn(String userid) throws SQLException


	// 쿠폰 유효기간 확인
	@Override
	public String getCouponCodate(String coname) throws SQLException {
		String CouponCodate = "";
		
		try {
			conn = ds.getConnection();
			
			String sql =  " select CODATE "
						+ " from coupon "
						+ " where CONAME = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, coname);
			rs = pstmt.executeQuery();
			rs.next();
			CouponCodate = rs.getString(1);	
			
			
		} finally {
			close();
		}
		
		return CouponCodate;
	} // public int getCouponCodate(String coname) throws SQLException --------------
	
	
}
