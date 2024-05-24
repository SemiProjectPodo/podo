package member.domain;

public class PointDTO {

	private String userid, poincome, podetail, podate;
	
	private MemberDTO member;
	
	public PointDTO() {}
	
	public PointDTO(String userid, String poincome,String podetail, String podate ) {
		this.userid = userid;
		this.poincome = poincome;
		this.podetail = podetail;
		this.podate = podate;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPoincome() {
		return poincome;
	}

	public void setPoincome(String poincome) {
		this.poincome = poincome;
	}

	public String getPodetail() {
		return podetail;
	}

	public void setPodetail(String podetail) {
		this.podetail = podetail;
	}

	public String getPodate() {
		return podate;
	}

	public void setPodate(String podate) {
		this.podate = podate;
	}

	public MemberDTO getMember() {
		return member;
	}

	public void setMember(MemberDTO member) {
		this.member = member;
	}
	
	
}
