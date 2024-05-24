package member.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import member.domain.MemberDTO;
import member.domain.PointDTO;

public interface MemberDAO {

   // 회원가입
   int doRegister(MemberDTO mdto) throws SQLException;

   // 로그인
   MemberDTO signin(Map<String, String> paraMap) throws SQLException;
   
   // 아이디 중복 확인
   boolean idDuplicateCheck(String userid) throws SQLException;

   // 이메일 중복 확인
   boolean emailDuplicateCheck(String email) throws SQLException;
   
   // 아이디 찾기
   String finduserid(Map<String, String> paraMap) throws SQLException;

   // 비밀번호 찾기(성명, 아이디, (휴대폰 or 이메일)을 입력받아 해당 사용자가 존재하는지 여부 알아오기)
   boolean isUserExist(Map<String, String> paraMap) throws SQLException;

   // 비밀번호 변경
   int pwdUpdate(Map<String, String> paraMap) throws SQLException;

   // 로그기록
   int logwrite(Map<String, String> paraMap) throws SQLException;
   
   
   
   // 관리자 회원관리 - 페이징 처리를 위한 검색이 있는 또는 검색이 없는 회원에 대한 총 페이지 수 알아오기
   int getTotalPage(Map<String, String> paraMap) throws SQLException;
   
   
   // 관리자 회원관리 - 페이징 처리를 한 모든 회원 또는 검색한 회원 목록 보여주기
   List<MemberDTO> select_Member_paging(Map<String, String> paraMap) throws SQLException;
   
   
   /* >>> 뷰단(memberList.jsp)에서 "페이징 처리 시 보여주는 순번 공식" 에서 사용하기 위해 
       검색이 있는 또는 검색이 없는 회원의 총 개수 알아오기 시작 <<< */
   int getTotalMemberCount(Map<String, String> paraMap) throws SQLException;

   // 내 정보 가져오기
   MemberDTO getMyInfo(String userid) throws SQLException;
   

   // 관리자 회원관리 - 한명 조회
   MemberDTO selectOneMember(String userid) throws SQLException;

   // 적립금 정보 
   Map<String, String> getMyPointInfo(String userid) throws SQLException;

   // 회원 정보 및 포인트 정보 가져오기
   Map<String, String> getMemberWithPointInfo(String userid) throws SQLException;

   // 회원 정보 및 포인트 정보 가져오기
   List<PointDTO> selectMemberPoint(String userid) throws SQLException;

   // 만약 시간이 유효기간이 지난 포인트가 있을 경우 poStatus 값을 0 으로 변경시킨다.
   void updateMemberPointTimeOver(String userid) throws SQLException;

   // 소멸예정 적립금 가져오기
   List<PointDTO> getDeleteSoonPointList(String userid) throws SQLException;

   


   
}