<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
   String ctxPath = request.getContextPath();
%>

<jsp:include page="../header.jsp" />

   <div id="container">
      
      <div class="cart_header" style="background-color: #F8F8F8; width: 100%; height: 200px; text-align: center;">
         <br><br>
         <h2 style="margin-bottom: 1.5%;">주문하기</h2>
         <div style="text-align: center; margin:0 7%;">
         <div style="display: flex; width: 16.5%; margin:0 auto;">
            
            <div style="margin-right: 8%;">
               <i class="fa-solid fa-cart-shopping" style="color: #bcbcbc;"></i>
               <br>
               <div style="font-weight: bold; color: #bcbcbc;">장바구니</div>
            </div>
            <div style="margin-right: 5%;">
               <br>
               <i class="fa-solid fa-chevron-right" style="color: #c0c0c1;"></i>
            </div>
            <div style="margin-right: 5%;">
               <i class="fa-regular fa-credit-card"></i>
               <br>
               <div style="font-weight: bold;">주문결제</div>
            </div>
            <div style="margin-right: 5%;">
               <br>
               <i class="fa-solid fa-chevron-right"></i>
            </div>
            <div>
               <i class="fa-regular fa-circle-check" style="color: #bcbcbc;"></i>
               <br>
               <div style="color: #bcbcbc; font-weight: bold;">주문완료</div>
            </div>
         
         </div>
         </div>
      </div>
      
      <div style="padding: 0; width: 80%; margin: 0 auto;"> 
      <c:forEach var="cdtolist" items="${requestScope.cdtoList}">
      		   <hr>
               <div class="hstack">
                 
                 <div style="width: 8%; min-width: 8%;">
                    <img src="<%=ctxPath %>/images/product/${cdtolist.pdto.pimg}" style="width: 100%;">
                 </div>
                 
                 <div class="curpointer" style="width: 28%; min-width: 28%; font-weight: bold; font-size: 1.0vw;" onclick="showProduct('${cdtolist.pdto.pindex}')">
                       ${cdtolist.pdto.pname}
                 </div>
                 
                 
                 <div id="div-volume" style="width: 5%; min-width: 5%; text-align: center; font-size: 1.5vw;">
                  ${cdtolist.cvolume}EA
                 </div>
                 
                 <div style="width:10%; min-width: 10%; font-size: 1.0vw; font-weight: bold; text-align: right; color: gray;">
                        ${cdtolist.pdto.pprice}원
                 </div>
                 
                 <div class="priceOne" style="width:10%; min-width: 10%; font-size: 1.0vw; font-weight: bold; text-align: right;">
                        ${cdtolist.sumprice}원
                 </div>
                 
               <div class="cart-index" style="font-size: 0;">${cdtolist.cindex}</div>  
                 
               <div id="dcnt" class="dcntAll">${cdtolist.cvolume}</div>
                 
               </div>
      </c:forEach>
      <hr style="border: solid 2px black; margin-bottom: 50px;">
      </div>
      
      
      <div class="cart_body_2" style="margin: 0 auto; display: flex;">
      
         <div style="border:solid 0px red; width: 50%;">
            <h3>주문자 정보</h3>
            
            <hr style="width: 90%;">
            <div class="form-group row my-4">
               <label class="col-2" style="width: 14%; font-weight: bold;">이름</label>
               <div class="col-sm-7">
                  <input type="text" name="name" class="form-control" placeholder="이름을 입력해주세요." style="border-color: black;"/>
               </div>
            </div>
            <div class="form-group row  my-4">
               <label class="col-2" style="width: 14%; font-weight: bold;">이메일</label>
               <div class="col-sm-7">
                  <input type="text" name="email" class="form-control" placeholder="이메일을 입력해주세요." style="border-color: black;"/>
               </div>
            </div>
            <div class="form-group row  my-4">
               <label class="col-2" style="width: 14%; font-weight: bold;">연락처</label>
               <div class="col-sm-7">
                  <input type="text" name="mobile" class="form-control" placeholder='"-"를 제외한 숫자만 입력해주세요.' style="border-color: black;"/>
               </div>
            </div>
            <div class="form-group row" style="margin-bottom: 1.8%;">
               <label class="col-2" style="width: 14%; font-weight: bold;">주소</label>
               <div class="col-sm-7">
                  <input type="text" name="address" class="form-control" style="border-color: black;"/>
                  <input type="text" name="addressDetail" class="form-control mt-1" placeholder="상세주소" style="border-color: black;"/>
               </div>
            </div>
            
            <div class="form-group row" style="margin-bottom: 1.8%;">
               <label class="col-2" style="width: 15.5%; font-weight: bold;">배송메시지</label>
            <select class="form-select" id="order_msg" name="order_msg" style="width: 55.2%; border-color: black;">
              <option selected>-- 메시지 선택--</option>
              <option>배송 전에 미리 연락바랍니다.</option>
              <option>부재 시 경비실에 맡겨주세요.</option>
              <option>부재 시 문 앞에 놓아주세요.</option>
              <option>빠른 배송 부탁드립니다.</option>
              <option>택배함에 보관해 주세요.</option>
              <option id="memo">직접입력</option>
            </select>
            <input type="text" name="memo" class="form-control" style="margin-left: 15.5%; margin-top: 0.5%; width: 55.2%; border-color: black;"/>
            </div>


         </div>
         
         <div style="border:solid 0px red;  width: 50%;">
            
            <h3>주문 계산서</h3>
            <hr style="width: 100%;">
	            <div class="form-group row" style="margin-bottom: 1.8%;">
	               <label class="col-2" style="width: 15.5%; font-weight: bold;">쿠폰 할인</label>
	            <select class="form-select" id="" name="" style="width: 55.2%; border-color: black;">
	              <option selected>-- 쿠폰 선택--</option>
	              <option>배송 전에 미리 연락바랍니다.</option>
	            </select>
	            </div>
            <h4>결제정보</h4>
            <div style="border:solid 1px black; height: 250px; margin-top: 3%;">
               <br>
               <div style="width: 90%; display:flex; justify-content: space-between; margin: 0 auto;">
                  <div>총 구매금액</div>
                  <div style="font-weight: bold;">14,900원</div>
               </div>
               <br>
               <div style="width: 90%; display:flex; justify-content: space-between; margin: 0 auto;">
                  <div>쿠폰할인</div>
                  <div style="font-weight: bold;">-2,000원</div>
               </div>
               <br>
               <div style="width: 90%; display:flex; justify-content: space-between; margin: 0 auto;">
                  <div>배송비</div>
                  <div style="font-weight: bold;">+3,000원</div>
               </div>
               <br>
               <hr style="width: 90%; margin: 0 auto;">               
               <br>
               <div style="width: 90%; display:flex; justify-content: space-between; margin: 0 auto;">
                  <div style="margin-top: 1%;">최종결제금액</div>
                  <div style="font-weight: bold; font-size: 20pt;">15,900원</div>
               </div>
            </div>
         </div>
         
      </div>

      
      <div class="cart_footer" style="text-align: center; margin-top: 4%;">
         <button type="button" class="btn btn-outline-secondary">취소</button>
         <button type="button" class="btn btn-outline-secondary" onclick="location.href='<%=ctxPath%>/shop/orderEnd.wine';">주문하기</button>
      </div>

   </div>
    </div>

<jsp:include page="../footer.jsp" />