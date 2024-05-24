package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import member.domain.MemberDTO;
import member.domain.PointDTO;
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
            mdto.setEmail(aes.decrypt(rs.getString(3)));
            mdto.setPhone(aes.decrypt(rs.getString(4)));
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
      
   } // end of public MemberDTO signin(Map<String, String> paraMap)
   
   
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
         
         isExist = rs.next();
         
      } finally {
         close();
      }
      
      return isExist;
      
   } // end of public boolean idDuplicateCheck(String userid)



   // 이메일 중복 확인
   @Override
   public boolean emailDuplicateCheck(String email) throws SQLException {

      boolean isExist = false;
      
      try {
         conn = ds.getConnection();
         
         String sql = " select email "
                  + " from member "
                  + " where email = ? ";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, aes.encrypt(email));
         rs = pstmt.executeQuery();
         
         isExist = rs.next(); 
         
      } catch (UnsupportedEncodingException | GeneralSecurityException e) {
         e.printStackTrace();
         
      } finally {
         close();
      }
      
      return isExist;
      
   } // end of public boolean emailDuplicateCheck(String email)



   // 아이디 찾기
   @Override
   public String finduserid(Map<String, String> paraMap) throws SQLException {
      
      String userid = null;
      
      try {
         
         conn = ds.getConnection();
         
         String sql = " select userid from member where name = ? and email = ?";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, paraMap.get("name"));
         pstmt.setString(2, aes.encrypt(paraMap.get("email")));
         
         rs = pstmt.executeQuery();
         
         if(rs.next()) {
            userid = rs.getString("userid");
         }
         
      } catch (UnsupportedEncodingException | GeneralSecurityException e) {
         e.printStackTrace();
      }finally {
         close();
      }
      
      return userid;
   } // end of public String finduserid(Map<String, String> paraMap) throws SQLException



   // 비밀번호 찾기(성명, 아이디, (휴대폰 or 이메일)을 입력받아 해당 사용자가 존재하는지 여부 알아오기)
   @Override
   public boolean isUserExist(Map<String, String> paraMap) throws SQLException {

      boolean isUserExist = false;
      
      try {
         conn = ds.getConnection();

         String sql = " select userid "
                  + " from member "
                  + " where memberidx = 1 and name = ? and userid = ? ";

         if(paraMap.containsKey("email")) { // '이메일 인증'일 때
            sql += " and email = ? ";
            
         } else { // '휴대폰 인증'일 때
            sql += " and phone = ? ";
         }
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, paraMap.get("name"));
         pstmt.setString(2, paraMap.get("userid"));
         
         if(paraMap.containsKey("email")) { // '이메일 인증'일 때
            pstmt.setString(3, aes.encrypt(paraMap.get("email")));
            
         } else { // '휴대폰 인증'일 때
            pstmt.setString(3, aes.encrypt(paraMap.get("phone")));
         }
         
         rs = pstmt.executeQuery();

         isUserExist = rs.next(); // 행이 있으면(사용자가 존재하면) true,
                             // 행이 없으면(사용자가 존재하지 않으면) false

      } catch (GeneralSecurityException | UnsupportedEncodingException e) {
         e.printStackTrace();
         
      } finally {
         close();
      }

      return isUserExist;
      
   } // end of public boolean isUserExist(Map<String, String> paraMap) throws SQLException { -----------



   // 비밀번호 변경
   @Override
   public int pwdUpdate(Map<String, String> paraMap) throws SQLException {

      int result = 0;
      
      try {
         conn = ds.getConnection();
         
         String sql = " update member set pwd = ?, pwdupdateday = to_char(sysdate, 'yyyy-mm-dd') "
                  + " where userid = ? ";
         
         pstmt = conn.prepareStatement(sql);
         
         pstmt.setString(1, Sha256.encrypt(paraMap.get("new_pwd"))); // 암호를 SHA256 알고리즘으로 단방향 암호화 시킨다.
         pstmt.setString(2, paraMap.get("userid"));
           
           result = pstmt.executeUpdate();
         
      } finally {
         close();
      }
      
      return result;
      
   } // end of public int pwdUpdate(Map<String, String> paraMap) throws SQLException ------------



   // 로그 기록
   @Override
   public int logwrite(Map<String, String> paraMap) throws SQLException {
      
      int result = 0;
      
      try {
      
         conn = ds.getConnection();
         
         String sql = "insert into LOG(LOGINDEX, userid, ipaddress) VALUES (SEQ_LOGIDX.nextval, ?, ?)";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, paraMap.get("userid"));
         pstmt.setString(2, paraMap.get("clientip"));
         
         result = pstmt.executeUpdate();
         
      }finally {
         
         close();
      
      }
      
      return result;
   }

   
   
   
   
   
   
   
   // 관리자 회원관리 - 페이징 처리를 한 모든 회원 또는 검색한 회원 목록 보여주기
   @Override
   public List<MemberDTO> select_Member_paging(Map<String, String> paraMap) throws SQLException {
      
      List<MemberDTO> memberList = new ArrayList<>();
      
      try {
         
         conn = ds.getConnection();
         
         String sql = " SELECT rno, userid, name, email, phone, gender, status "
                  + " FROM "
                  + " ( "
                  + "    select rownum AS RNO, userid, name, email, phone, gender, status "
                  + "    from "
                  + "    ( "
                  + "        select userid, name, email, phone, gender, status "
                  + "        from member join memberidx on member.memberidx = memberidx.memberidx "
                  + "        where memberidx.memberidx != 9";
         
         
         String colname = paraMap.get("searchType");
         String searchWord = paraMap.get("searchWord");
         
         if("email".equals(colname)) {
            // 검색 대상이 email인 경우
            searchWord = aes.encrypt(searchWord);
         }
         
         if((colname != null && !colname.trim().isEmpty()) && 
            (searchWord != null && !searchWord.trim().isEmpty())) {
            
            sql += " and " + colname + " like '%'|| ? ||'%' ";
         }
         
         
         sql += " order by registerday desc "
             + "    ) "
             + " ) T "
             + " WHERE T.rno BETWEEN ? AND ? ";
         
         pstmt = conn.prepareStatement(sql);
         
         
         int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
         int sizePerPage =Integer.parseInt(paraMap.get("sizePerPage"));
         
         
         if((colname != null && !colname.trim().isEmpty()) && 
            (searchWord != null && !searchWord.trim().isEmpty())) {
            // 검색이 있는 경우
            
            pstmt.setString(1, searchWord);
            pstmt.setInt(2, (currentShowPageNo * sizePerPage - (sizePerPage - 1)));
            pstmt.setInt(3, (currentShowPageNo * sizePerPage));
            
         } else {
            // 검색이 없는 경우
            
            pstmt.setInt(1, (currentShowPageNo * sizePerPage - (sizePerPage - 1)));
            pstmt.setInt(2, (currentShowPageNo * sizePerPage));
         }
         
         rs = pstmt.executeQuery();
         
         while(rs.next()) {
            MemberDTO mdto = new MemberDTO();
            
            mdto.setUserid(rs.getString("userid"));
            mdto.setName(rs.getString("name"));
            mdto.setEmail(aes.decrypt(rs.getString("email")));
            mdto.setPhone(aes.decrypt(rs.getString("phone")));
            mdto.setGender(rs.getString("gender"));
            mdto.setStatus(rs.getString("status"));
            
            memberList.add(mdto);
            
         } // end of while(rs.next())
         
      } catch (GeneralSecurityException | UnsupportedEncodingException e) {
         e.printStackTrace();
         
      } finally {
         close();
      }
      
      return memberList;
      
   } // end of public List<MemberDTO> select_Member_paging(Map<String, String> paraMap)

   
   
   

   /* >>> 뷰단(memberList.jsp)에서 "페이징 처리 시 보여주는 순번 공식" 에서 사용하기 위해 
      검색이 있는 또는 검색이 없는 회원의 총 개수 알아오기 <<< */
   @Override
   public int getTotalMemberCount(Map<String, String> paraMap) throws SQLException {
      int totalMemberCount = 0;
      
      try {
         
         conn = ds.getConnection();
         
         String sql = " select count(*) "
                  + " from member join memberidx on member.memberidx = memberidx.memberidx "
                  + " where memberidx.memberidx != 9 ";

         String colname = paraMap.get("searchType");
         String searchWord = paraMap.get("searchWord");
         
         if("email".equals(colname)) {
            // 검색 대상이 email인 경우
            searchWord = aes.encrypt(searchWord);
         }
         
         if((colname != null && !colname.trim().isEmpty()) && 
            (searchWord != null && !searchWord.trim().isEmpty())) {
            
            sql += " and " + colname + " like '%'|| ? ||'%' ";
         }
         
         pstmt = conn.prepareStatement(sql);
         
         if((colname != null && !colname.trim().isEmpty()) && 
            (searchWord != null && !searchWord.trim().isEmpty())) {
            // 검색이 있는 경우
            
            pstmt.setString(1, searchWord);
         }
         
         rs = pstmt.executeQuery();
         rs.next();
         
         totalMemberCount = rs.getInt(1);
         
      } catch (GeneralSecurityException | UnsupportedEncodingException e) {
         e.printStackTrace();
         
      } finally {
         close();
      }
      
      return totalMemberCount;
      
   } // end of public int getTotalMemberCount(Map<String, String> paraMap)



   
   // 관리자 회원관리 - 페이징 처리를 위한 검색이 있는 또는 검색이 없는 회원에 대한 총 페이지 수 알아오기
      @Override
      public int getTotalPage(Map<String, String> paraMap) throws SQLException {
         
         int totalPage = 0;
         
         try {
            
            conn = ds.getConnection();
            
            String sql = " select ceil(count(*)/?) "
                     + " from member "
                     + " join memberidx "
                     + " on member.memberidx = memberidx.memberidx "
                     + " where memberidx.memberidx != 9 ";

            String colname = paraMap.get("searchType");
            String searchWord = paraMap.get("searchWord");
            
            if("email".equals(colname)) {
               // 검색 대상이 email인 경우
               searchWord = aes.encrypt(searchWord);
            }
            
            if((colname != null && !colname.trim().isEmpty()) && 
               (searchWord != null && !searchWord.trim().isEmpty())) {
               
               sql += " and " + colname + " like '%'|| ? ||'%' ";
            }
            
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, Integer.parseInt(paraMap.get("sizePerPage")) );
            
            if((colname != null && !colname.trim().isEmpty()) && 
               (searchWord != null && !searchWord.trim().isEmpty())) {
               // 검색이 있는 경우
               
               pstmt.setString(2, searchWord);
            }
            
            rs = pstmt.executeQuery();
            rs.next();
            
            totalPage = rs.getInt(1);
            
         } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
            
         } finally {
            close();
         }
         
         return totalPage;
         
      } // end of public int getTotalPage(Map<String, String> paraMap) throws SQLException
         
   

   
   
   
   
   
   
   // 관리자 회원관리 - 한명 조회
   @Override
   public MemberDTO selectOneMember(String userid) throws SQLException {
      return null;
      
   } // end of public MemberDTO selectOneMember(String userid) throws SQLException


   // 내 정보 가져오기
   @Override
   public MemberDTO getMyInfo(String userid) throws SQLException {
      MemberDTO myInfo = new MemberDTO();      
      
      try {
         
         conn = ds.getConnection();
         
         String sql = " select name, point, birthday"
                  + " from member"
                  + " where userid = ? ";
         
         pstmt = conn.prepareStatement(sql);
         
         pstmt.setString(1, userid);
         
         rs = pstmt.executeQuery();
         
         rs.next();
         
         myInfo.setName(rs.getString("name"));
         myInfo.setPoint(rs.getString("point"));
         myInfo.setBirthday(rs.getString("birthday"));
         
         
      } finally {
         close();
      }
      
      return myInfo;
   }


   // 적립금 정보 
   @Override
   public Map<String, String> getMyPointInfo(String userid) throws SQLException {
      
      Map<String, String> paraMap = new HashMap<>();
      
      try {
         
         conn = ds.getConnection();
         
         String sql = " select poincome, podetail, podate"
                  + " from Point"
                  + " where userid = ? ";
         
         pstmt = conn.prepareStatement(sql);
         
         pstmt.setString(1, userid);
         
         rs = pstmt.executeQuery();
         
         while(rs.next()) {
            String poincome = rs.getString("poincome");
            String podetail = rs.getString("podetail");
            String podate = rs.getString("podate");
            
            paraMap.put("poincome", poincome);
            paraMap.put("podetail", podetail);
            paraMap.put("podate", podate);
         }
         
      } finally {
         close();
      }
      return paraMap;
   }


   // 포인트 정보와 회원 정보 가져오기
   @Override
   public Map<String, String> getMemberWithPointInfo(String userid) throws SQLException {
      Map<String, String> memberPointInfo = new HashMap<>();
      try {
         conn = ds.getConnection();
         String sql = " SELECT M.USERID, M.NAME, M.POINT, P.POINCOME, P.PODATE " 
			        + " FROM MEMBER M LEFT JOIN POINT P ON M.USERID = P.USERID " 
			        + " WHERE M.USERID = ? ";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, userid);
         rs = pstmt.executeQuery();
         
         if(rs.next()) {
            memberPointInfo.put("userid", rs.getString("USERID"));
            memberPointInfo.put("name", rs.getString("NAME"));
            memberPointInfo.put("point", rs.getString("POINT"));
            memberPointInfo.put("poincome", rs.getString("POINCOME"));
            memberPointInfo.put("podate", rs.getString("PODATE"));
         }
         
      } finally {
         close();
      }
      return memberPointInfo;
   }


   // 회원 정보 및 포인트 정보 가져오기
	@Override
	public List<PointDTO> selectMemberPoint(String userid) throws SQLException {
	
		// 포인트를 얻은 적 없을 수도 있기 때문에
		List<PointDTO> pointList = null;

		try {
			conn = ds.getConnection();
			
			String sql = " select M.name, M.point, P.poincome, P.podetail, P.podate "
					   + " From "
					   + " ( "
					   + "    select userid, poincome, podetail, podate "
					   + "    from point "
					   + "    where userid = ? "
					   + " ) P "
					   + " JOIN member M "
					   + " ON P.userid = M.userid "
					   + " ORDER BY P.podate desc ";
			
			pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	         
	        rs = pstmt.executeQuery();
	        
	        // 한번만 cartList = new ArrayList<>(); 만들기 위해;
	        int cnt = 0;
	        while(rs.next()) {
	        	cnt++;
	        	if(cnt == 1) {
	        		pointList = new ArrayList<>();
	        	}
	                String poincome = rs.getString("poincome");
	                String podetail = rs.getString("podetail");
	                String podate = rs.getString("podate");
	                
	                MemberDTO member = new MemberDTO();
	                member.setName(rs.getString("name"));
	                member.setPoint(rs.getString("point"));
	                
	                
	                PointDTO point = new PointDTO();
	                point.setPoincome(poincome);
	                point.setPodetail(podetail);
	                point.setPodate(podate);
	                point.setMember(member);
	                
	                pointList.add(point);
	        	
	        } // end of while(rs.next()) ------------------
			
		} finally {
			close();
		}

		return pointList;
	} // end of public List<PointDTO> selectMemberPoint(String userid) --------


	// 만약 시간이 유효기간이 지난 포인트가 있을 경우 poStatus 값을 0 으로 변경시킨다.
	@Override
	public void updateMemberPointTimeOver(String userid) throws SQLException {
		
		// 포인트 얻은지 3개월이 지나면 포인트 소멸
		try {
			conn = ds.getConnection();
			
			String sql = " UPDATE point SET postatus = '0' "
	        		   + " WHERE poStatus = 1 and userid = ?"
	        		   + " 	  and to_char(to_date(PODATE, 'YYYY-MM-DD HH24:MI:SS') + to_yminterval('00-03') + to_dsinterval('003 00:00:00')  , 'yyyy-mm-dd hh24:mi:ss') <= to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') ";
	        	
	        	pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, userid);
	        	
	        	int n = pstmt.executeUpdate();
	        	
	        	if(n == 1) {
	        		System.out.println("뭔가 됌");
	        	}
	        
		} finally {
			close();
		}
	} // end of public void updateMemberPointTimeOver(String userid) throws SQLException ----


	// 소멸예정 적립금(한달남음) 가져오기
	@Override
	public List<PointDTO> getDeleteSoonPointList(String userid) throws SQLException {
		List<PointDTO> deleteSoonPointList = null;
		
		try {
			conn = ds.getConnection();
			
			String sql = " select POINCOME "
					   + " from point "
					   + " where poStatus = 1 and userid = ? "
					   + "       and to_char(to_date(PODATE, 'YYYY-MM-DD HH24:MI:SS') + to_yminterval('00-02') + to_dsinterval('000 00:00:00')  , 'yyyy-mm-dd hh24:mi:ss') <= to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') ";
			
			pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userid);
	         
	        rs = pstmt.executeQuery();
	        
	        // 한번만 cartList = new ArrayList<>(); 만들기 위해;
	        int cnt = 0;
	        while(rs.next()) {
	        	cnt++;
	        	if(cnt == 1) {
	        		deleteSoonPointList = new ArrayList<>();
	        	}
	                String poincome = rs.getString("POINCOME");
	                
	                PointDTO point = new PointDTO();
	                point.setPoincome(poincome);

	                
	                deleteSoonPointList.add(point);
	        	
	        } // end of while(rs.next()) ------------------
			
		} finally {
			close();
		}

		return deleteSoonPointList;	
	} // end of public List<PointDTO> getDeleteSoonPointList(String userid) ------

	
	

   
   
}