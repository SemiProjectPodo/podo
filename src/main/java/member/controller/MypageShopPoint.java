package member.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.domain.MemberDTO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class MypageShopPoint extends AbstractController {

   MemberDAO mdao = null;
   
   public MypageShopPoint() {
      mdao = new MemberDAO_imple();
   }
   
   @Override
   public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
      
      String method = request.getMethod();
      
      HttpSession session = request.getSession();
      MemberDTO mdto = (MemberDTO)session.getAttribute("loginUser");
      
      if(mdto != null) {
         // 로그인을 했을 경우
         
         String userid = mdto.getUserid();
         request.setAttribute("userid", userid);
         
         // 내 정보 가져오기
         MemberDTO myInfo = mdao.getMyInfo(userid);
         String name = myInfo.getName();
         String point = myInfo.getPoint();
         String birthday = myInfo.getBirthday();
         
         request.setAttribute("name", name);
         request.setAttribute("point", point);
         request.setAttribute("birthday", birthday);
         
         // 적립금 정보 
         Map<String, String> paraMap = mdao.getMyPointInfo(userid);
         
         
         
         super.setRedirect(false);
         super.setViewPage("/WEB-INF/member/mypageShopPoint.jsp");
      }
      else {
         // 마이페이지에서 로그아웃했을 경우 아닐경우 홈페이지로 이동
         super.setRedirect(false);
         super.setViewPage("/WEB-INF/member/mypageShopPoint.jsp");
         
         
      }
      


   }

}