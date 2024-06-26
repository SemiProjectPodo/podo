package login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;

public class VerifyCertification extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String method = request.getMethod();
		
		if("POST".equalsIgnoreCase(method)) {
			
			String userCertificationCode = request.getParameter("userCertificationCode");
			String userid = request.getParameter("userid");
			
			HttpSession session = request.getSession(); // 세션 불러오기
			String certification_code = (String)session.getAttribute("certification_code");
			
			String msg = "";
			String loc = "";
			
			if(certification_code.equals(userCertificationCode)) { // 휴대폰 or 이메일로 보낸 인증코드와 사용자가 입력한 인증코드가 같다면
				msg = "인증이 성공되었습니다.";
				loc = request.getContextPath() + "/login/pwdUpdateEnd.wine?userid=" + userid;
				
			} else {
				msg = "발급된 인증코드와 일치하지 않습니다.\\n인증 코드를 다시 발급받으세요!";
				loc = request.getContextPath() + "/login/pwdFind.wine";
			}
			
			request.setAttribute("msg", msg);
			request.setAttribute("loc", loc);
			
//			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
			
			// !!!! 중요 !!!! //
	        // !!!! 세션에 저장된 인증코드 삭제하기 !!!! //
			session.removeAttribute("certification_code");
			
		} // end of if("POST".equalsIgnoreCase(method)) -----------------
	}

}
