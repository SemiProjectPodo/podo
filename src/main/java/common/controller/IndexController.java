package common.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import coupon.model.CouponDAO;
import coupon.model.CouponDAO_imple;
import member.domain.MemberDTO;
import member.model.MemberDAO;
import member.model.MemberDAO_imple;
import shop.domain.ProductDTO;
import shop.model.ProductDAO;
import shop.model.ProductDAO_imple;

public class IndexController extends AbstractController {

	ProductDAO pdao = null;
	MemberDAO mdao = null;
	CouponDAO codao = null;

	public IndexController() {
		pdao = new ProductDAO_imple();
		mdao = new MemberDAO_imple();
		codao = new CouponDAO_imple();
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {

		try {

			HttpSession session = request.getSession();
			MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");

			List<ProductDTO> pdtoList = pdao.listReadDesc();
			List<ProductDTO> pdtoList2 = pdao.listPopReadDesc();
			
			request.setAttribute("newProductList", pdtoList);
			request.setAttribute("popProductList", pdtoList2);
			
			
			if(loginUser != null) {
				
				int reviewCnt = mdao.getReviewCnt(loginUser.getUserid());
				session.setAttribute("reviewCnt", reviewCnt);
				
				int availableCoupons = codao.getAvailableCoupons(loginUser.getUserid());
				session.setAttribute("availableCoupons", availableCoupons);
				
			}
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/index.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
