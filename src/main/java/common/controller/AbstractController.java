package common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import member.domain.MemberDTO;


public abstract class AbstractController implements InterCommand {
	
	private boolean isRedirect = false;
	private String viewPage;

	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}
	
	public boolean isDir(HttpSession httpSession) {
		
		boolean result = false;
		
		MemberDTO mdto = (MemberDTO) httpSession.getAttribute("loginUser");
		
		if("9".equals(mdto.getMemberIdx())) {
			result = true;
		}
		
		return result;
	
	}
	
	public boolean checkLogin(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		MemberDTO loginUser = (MemberDTO)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			// 로그인 한 경우
			return true;
			
		} else {
			// 로그인 하지 않은 경우
			return false;
		}
		
	}

}
