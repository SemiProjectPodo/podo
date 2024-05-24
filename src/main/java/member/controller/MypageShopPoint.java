package member.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.domain.MemberDTO;
import member.domain.PointDTO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;

public class MypageShopPoint extends AbstractController {

	MemberDAO mdao = null;

	public MypageShopPoint() {
		mdao = new MemberDAO_imple();
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String referer = request.getHeader("referer");
		// request.getHeader("referer"); 은 이전 페이지의 URL을 가져오는 것이다.

		if (referer == null) {
			// referer == null 은 웹브라우저 주소창에 URL 을 직접 입력하고 들어온 경우이다.
			super.setRedirect(true);
			super.setViewPage(request.getContextPath() + "/index.wine");
			return;
		}

		String method = request.getMethod();

		HttpSession session = request.getSession();
		MemberDTO mdto = (MemberDTO) session.getAttribute("loginUser");

		if (mdto != null) {
			// 로그인을 했을 경우

			String userid = mdto.getUserid();
			request.setAttribute("userid", userid);

			/*
			 * // 내 정보 가져오기 MemberDTO myInfo = mdao.getMyInfo(userid); String name =
			 * myInfo.getName(); String point = myInfo.getPoint(); String birthday =
			 * myInfo.getBirthday();
			 * 
			 * request.setAttribute("name", name); request.setAttribute("point", point);
			 * request.setAttribute("birthday", birthday);
			 */

			// 회원 정보 및 포인트 정보 가져오기
			List<PointDTO> pointList = mdao.selectMemberPoint(userid);
			
			Map<String, String> memberPointInfo = mdao.getMemberWithPointInfo(userid);

			int pointcome = 0;
			for(PointDTO point : pointList) {
				pointcome += Integer.parseInt(point.getPoincome());
				point.getPodate();
			}
			
			// 필요한 정보 세팅
			request.setAttribute("memberPointInfo", memberPointInfo);
			request.setAttribute("pointList", pointList);
			request.setAttribute("pointcome", pointcome);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/member/mypageShopPoint.jsp");
		} else {
			// 마이페이지에서 로그아웃했을 경우 아닐경우 홈페이지로 이동
			super.setRedirect(true);
			super.setViewPage(request.getContextPath() + "/index.wine");
			return;
		}

	}

}