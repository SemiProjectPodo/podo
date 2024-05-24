<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
   String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/header.jsp" /> 

<!-- DataPicker -->
<script type="text/javascript" src="https://cdn.jsdelivr.net/momentjs/latest/moment.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />


<style type="text/css">


</style>

    <c:if test="${not empty memberPointInfo}">
        <p>회원 아이디: ${memberPointInfo.userid}</p>
        <p>회원 이름: ${memberPointInfo.name}</p>
        <p>총 포인트: ${memberPointInfo.point}</p>
        <p>적립된 포인트: ${memberPointInfo.poincome}</p>
        <p>포인트 변동 일자: ${memberPointInfo.podate}</p>
    </c:if>


<div class="container" style="padding: 3% 0;">
   <h2 style="font-weight:bold; margin-bottom:2%; text-align:center;"><img src="<%=ctxPath%>/images/point.png" style="width:40px; vertical-align: text-top;">&nbsp;마이페이지</h2>
   <hr>
   
   <table class="table" style="width:50;">
     <thead>
       <tr>
         <th scope="col" style="font-size:16pt"><span style="font-weight:bold; font-size:20pt; color:#ff6666;"><img src="<%=ctxPath%>/images/mypageP.png" style="width:35px; vertical-align: text-top;">&nbsp;${requestScope.name}</span>님</th>
       </tr>
     </thead>
     <tbody class="table-group-divider" style="text-align:center;">
       <tr class="table-secondary">
         <th scope="row">누적 적립금</th>
         <th>사용 적립금</th>
         <th>가용 적립금</th>
         <th>소멸예정 적립금</th>
       </tr>
       <tr class="table-group-divider">
         <td>${requestScope.pointcome}</td>
         <td>${requestScope.memberPointInfo.point-requestScope.pointcome}</td>
         <td>${requestScope.memberPointInfo.point}</td>
         <td>0</td>
       </tr>
     </tbody>
   </table>
   
   <div style="margin-top:4%;">
      <input type="date" style="width:20%; height:50px; margin-left:52%;"> ~ <input type="date" style="width:20%; height:50px;">&nbsp;&nbsp;
      <button type="button" class="btn btn-secondary" style="width:5%; height:50px; font-size:13pt; font-weight:bold;">검색</button>
   </div>
   
   <div class="vr" style="border:solid 1px blue; height:20px; margin-top:4%;"></div>
      <span style="font-weight:bold; font-size:16pt;">나의 적립금</span>
   <div class="vr" style="border:solid 1px blue; height:20px;"></div>
   
   
   <!-- Nav tabs -->
   <ul class="nav nav-tabs" data-bs-theme="light" style="margin-top:4%;">
     <li class="nav-item">
       <a class="nav-link active" data-bs-toggle="tab" href="#point1" style="font-weight: bold; font-size: 13pt;">적립내역</a>
     </li>
     <li class="nav-item">
       <a class="nav-link" data-bs-toggle="tab" href="#point2" style="font-weight: bold; font-size: 13pt;">사용내역</a>
     </li>
   </ul>
   
   <!-- Tab panes -->
   <div class="tab-content pt-5 pb-5">
        <div class="tab-pane container active" id="point1" style="font-size: 10pt;">
         <table class="table" style="width:50;">
              <tbody style="text-align:center;">
                 <tr class="table-light">
                     <th scope="row">적립 날짜</th>
                     <th>적립 내용</th>
                     <th>포인트</th>
                 </tr>
	  			 <c:if test="${not empty requestScope.pointList}">
		         	 <c:forEach var="pointdto" items="${requestScope.pointList}" varStatus="status">
		                 <tr>
		                     <td>${pointdto.podate}</td>
		                     <td>${pointdto.podetail}</td>
		                     <td>${pointdto.poincome} point</td>
		                </tr>
					 </c:forEach>
				 </c:if>
            </tbody>
         </table>
      </div>
        <div class="tab-pane container fade" id="point2" style="font-size: 10pt;">
           <table class="table" style="width:50;">
              <tbody style="text-align:center;">
                 <tr class="table-light">
                     <th scope="row">사용 날짜</th>
                     <th>내용</th>
                     <th>사용 포인트</th>
                 </tr>
                 <tr>
                     <td>2024-04-21</td>
                     <td>와인구매내역</td>
                     <td>5 point</td>
                </tr>
                <tr>
                     <td>2024-04-22</td>
                     <td>와인구매내역</td>
                     <td>10 point</td>
                </tr>
            </tbody>
         </table>
        </div>
   </div>
   
   
   
   
   
   
   
   
</div>





<jsp:include page="/WEB-INF/footer.jsp" />     