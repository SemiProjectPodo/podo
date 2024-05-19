<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
   String ctxPath = request.getContextPath();
%>

<style>
    .star-rating {
      display: flex;
    }

    .star {
      appearance: none;
      padding: 1px;
    }

    .star::after {
      content: '☆';
      color: purple;
      font-size: 50px;
    }

    .star:hover::after,
    .star:has(~ .star:hover)::after,
    .star:checked::after,
    .star:has(~ .star:checked)::after {
      content: '★';
    }

    .star:hover ~ .star::after {
      content: '☆';
    }
</style>

<jsp:include page="../../header.jsp" />

<form>
   <div id="container" style="width: 100%;">
      
      <div style="width: 60%; text-align: center;">

      	 <h2>리뷰작성</h2>
      	 <div>
	         <hr><br>
	         <h5>와인을 평가해주세요</h5>
	         <div class="star-rating" style="margin-left:36%;">
			    <input type="radio" class="star" value="1">
			    <input type="radio" class="star" value="2">
			    <input type="radio" class="star" value="3">
			    <input type="radio" class="star" value="4">
			    <input type="radio" class="star" value="5">
	         </div>
	         <br><br><br>
	         <h5>사진과 후기를 남겨주세요</h5>
	         <div style="display: flex;">
	         	<input type="text" class="form-control" style="width: 30%; margin-right: 3%;">
				<textarea class="form-control h-25" id="exampleFormControlTextarea1" rows="8" placeholder="만족도에 대한 후기를 남겨주세요"></textarea>
	         </div>
	         <hr>
      	 </div>
      	 
      	
	     <button type="button" class="btn btn-secondary" style="width: 20%;">등록</button>


      </div>
      
    </div>
    
</form>
<jsp:include page="../../footer.jsp" />