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

<script type="text/javascript">

$(document).ready(function() {

});

function searchData() {
    var startDate = document.getElementById("startDate").value;
    var endDate = document.getElementById("endDate").value;
    
    let timeFalse = false; // timeFalse 변수를 searchData() 내부에서 선언

    // 데이터베이스에서 가져온 날짜 형식으로 변환
    var pointHistory = document.querySelectorAll("tbody.date-search-result tr");

    // pointHistory가 null 또는 빈 배열인지 확인
    if (pointHistory && pointHistory.length > 0) {
        pointHistory.forEach(function(row) {
            var dateCell = row.querySelector("td:first-child");
            if (dateCell) {
                var dateText = dateCell.innerText;
                var rowDate = new Date(dateText.split(' ')[0].replace(/-/g, '/') + ' 00:00:00'); // 시간 부분을 00:00:00으로 설정

                // 시작일과 종료일이 모두 있는 경우
                if (startDate && endDate) {
                    var start = new Date(startDate + ' 00:00:00'); // 시작일을 그 날의 첫 시간으로 설정하여 포함되도록 함
                    var end = new Date(endDate + ' 23:59:59'); // 종료일을 그 날의 마지막 시간으로 설정하여 포함되도록 함

                    // 종료일이 시작일보다 이전인 경우
                    if (end < start) {
                        timeFalse = true;
                        return; // 검색 중단
                    }
                }

                // 시작일만 있는 경우
                if (startDate && !endDate) {
                    if (rowDate >= new Date(startDate + ' 00:00:00')) { // 시작일을 그 날의 첫 시간으로 설정하여 포함되도록 함
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                }
                // 종료일만 있는 경우
                else if (!startDate && endDate) {
                    if (rowDate <= new Date(endDate + ' 23:59:59')) { // 종료일을 그 날의 마지막 시간으로 설정하여 포함되도록 함
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                }
                // 시작일과 종료일이 모두 있는 경우
                else if (startDate && endDate) {
                    if (rowDate >= new Date(startDate + ' 00:00:00') && rowDate <= new Date(endDate + ' 23:59:59')) { // 시작일과 종료일을 각각 그 날의 첫 시간과 마지막 시간으로 설정하여 포함되도록 함
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                }
            }
        });
    }
    
    if (timeFalse) {
        alert("종료일은 시작일 이후의 날짜여야 합니다.");
    }
}

</script>


<div class="container" style="padding: 3% 0;">
	<h2 style="font-weight:bold; margin-bottom:2%; text-align:center;"><img src="<%=ctxPath%>/images/point.png" style="width:40px; vertical-align: text-top;">&nbsp;마이페이지</h2>
	<hr>
	
	<table class="table" style="width:50;">
	  <thead>
	    <tr>
	      <th scope="col" style="font-size:16pt"><span style="font-weight:bold; font-size:20pt; color:#ff6666;"><img src="<%=ctxPath%>/images/mypageP.png" style="width:35px; vertical-align: text-top;">&nbsp;${sessionScope.loginUser.name}</span>님</th>
	    </tr>
	  </thead>
	  <tbody class="table-group-divider" style="text-align:center;">
	    <tr class="table-secondary">
	      <th scope="row">누적 적립금</th>
	      <th>사용 적립금</th>
	      <th>가용 적립금</th>
	      <%-- 
	      <th>소멸예정 적립금</th>
	      --%>
	    </tr>
	    <tr class="table-group-divider">
	      <td id="totalPoints">${requestScope.userpointdto.totalPoints}</td>
	      <td id="usedPoints">${requestScope.userpointdto.usedPoints}</td>
	      <td id="availablePoints">${requestScope.userpointdto.availablePoints}</td>
	      <%--
	      <td id="expiringPoints">${requestScope.expiringPoints}</td>
	      --%>
	    </tr>
	  </tbody>
	</table>
	
	<div style="margin-top:4%;">
	    <input id="startDate" type="date" style="width:20%; height:50px; margin-left:52%;"> ~ 
	    <input id="endDate" type="date" style="width:20%; height:50px;">&nbsp;&nbsp;
	    <button type="button" class="btn btn-secondary" style="width:5%; height:50px; font-size:13pt; font-weight:bold;" onclick="searchData()">검색</button>
	</div>
	
	<div class="vr" style="border:solid 1px blue; height:20px; margin-top:4%;"></div>
		<span style="font-weight:bold; font-size:16pt;">나의 적립금</span>
	<div class="vr" style="border:solid 1px blue; height:20px;"></div>
	
	
	<!-- Nav tabs -->
	<ul class="nav nav-tabs" data-bs-theme="light" style="margin-top:4%;">
	  <li class="nav-item">
	    <a class="nav-link active" data-bs-toggle="tab" href="#point1" style="font-weight: bold; font-size: 13pt;">적립내역</a>
	  </li>
	</ul>
	
	<!-- Tab panes -->
	<div class="tab-content pt-5 pb-5">
	  	<div class="tab-pane container active" id="point1" style="font-size: 10pt;">
			<table class="table" style="width:50;">
				<tbody class="date-search-result" style="text-align:center;">
				    <tr class="table-light">
				        <th scope="row">적립 날짜</th>
				        <th>적립 내용</th>
				        <th>포인트</th>
				    </tr>
				    <c:if test="${requestScope.myPointpaging == null}">
				        <tr>
				            <td colspan="3">적립된 포인트가 없습니다.</td>
				        </tr>
				    </c:if>
				
				    <c:if test="${requestScope.myPointpaging != null}">
				        <c:forEach var="podto" items="${requestScope.myPointpaging}">
					            <tr>
					                <td>${podto.poDate}</td>
					                <td>${podto.poDetail}</td>
					                <td style="position: relative;">
					                    ${podto.poIncome} point
					                </td>
					            </tr>
				        </c:forEach>
				    </c:if>
				</tbody>
			</table>
		</div>
	</div>
</div>





<jsp:include page="/WEB-INF/footer.jsp" />     