<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="bbs.BbsDAO"%>
<%@ page import="bbs.Bbs"%>
<%@ page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width" , initial-scale="1">
<link rel="stylesheet" href="css/bootstrap.css">
<link rel="stylesheet" href="css/custom.css">
<title>JSP게시판 웹사이트</title>
<style type="text/css">
.search {
	margin: 50px;
}
</style>
<script type="text/javascript">
	window.onload = function() {
		function getParameterByName(name) {
			name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
			var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex
					.exec(location.search);
			return results == null ? "" : decodeURIComponent(results[1]
					.replace(/\+/g, " "));
		}
		const opt = getParameterByName("opt");
		const condition = getParameterByName("condition");
		console.log(opt, condition);

		const comboBox = document.getElementsByClassName("comboBox")[0];
		const inputCondition = document
				.getElementsByClassName("inputCondition")[0];

		if (condition != null) {
			inputCondition.setAttribute("value", condition);
		}

		console.log("selected = "
				+ comboBox.children[0].getAttribute("selected"));
		if (comboBox.children[0].getAttribute("selected") == "selected") {
			inputCondition.setAttribute("value", "");
			inputCondition.setAttribute("readonly", "");
		}
		for (var i = 0; i < comboBox.children.length; i++) {
			if (comboBox.children[i].value === opt) {
				comboBox.children[i].setAttribute('selected', '')
			}
		}

	}
</script>
<style type="text/css">
a, a:hover {
	color: black;
	text-decoration: none;
}
</style>
</head>
<body>
	<%
		String userID = null;
		if (session.getAttribute("userID") != null) {
			userID = (String) session.getAttribute("userID");
		}
		int pageNumber = 1;
		if (request.getParameter("pageNumber") != null) {
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}

		// 검색조건과 검색내용을 가져옴
		String opt = request.getParameter("opt");
		if (request.getParameter("opt") == null) {
			opt = "0";
		}

		String condition = request.getParameter("condition");
		if (request.getParameter("condition") == null) {
			condition = "";
		}

		// 검색조건과 내용을 Map에 담음
		HashMap<String, Object> listOpt = new HashMap<String, Object>();
		listOpt.put("opt", opt);
		listOpt.put("condition", condition);
		listOpt.put("pageNumber", pageNumber);
	%>
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="main.jsp">JSP 게시판 웹 사이트</a>
		</div>
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="main.jsp">메인</a></li>
				<li class="active"><a href="bbs.jsp">게시판</a></li>
			</ul>
			<%
				if (userID == null) {
			%>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"
						role="button" aria-haspopup="true" aria-expanded="false">
						접속하기
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li><a href="login.jsp">로그인</a></li>
						<li><a href="join.jsp">회원가입</a></li>
					</ul></li>
			</ul>
			<%
				} else {
			%>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"
						role="button" aria-haspopup="true" aria-expanded="false">
						회원관리
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li><a href="logoutAction.jsp">로그아웃</a></li>
					</ul></li>
			</ul>
			<%
				}
			%>
		</div>
	</nav>
	<div class="container">
		<div class="search row">
			<form>
				<div class="col-xs-2 col-sm-2">
					<select class="form-control comboBox" name="opt">
						<option value="0">----</option>
						<option value="1">제목</option>
						<option value="2">내용</option>
						<option value="3">제목+내용</option>
						<option value="4">글쓴이</option>
					</select>
				</div>
				<div class="col-xs-10 col-sm-10">
					<div class="input-group">
						<input type="text" size="20" name="condition" class="form-control inputCondition" />
						<span class="input-group-btn">
							<button type="submit" class="btn btn-default">검색</button>
						</span>
					</div>
				</div>
			</form>
		</div>
		<div class="row">
			<table class="table table-striped"
				style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align: center;">번호</th>
						<th style="background-color: #eeeeee; text-align: center;">제목</th>
						<th style="background-color: #eeeeee; text-align: center;">작성자</th>
						<th style="background-color: #eeeeee; text-align: center;">작성일</th>
						<th style="background-color: #eeeeee; text-align: center;">조회수</th>
					</tr>
				</thead>
				<tbody>
					<%
						BbsDAO bbsDAO = new BbsDAO();
						ArrayList<Bbs> list = bbsDAO.getList(listOpt);
						for (int i = 0; i < list.size(); i++) {
					%>
					<tr>
						<td><%=list.get(i).getBbsID()%></td>
						<td>
							<a href="view.jsp?bbsID=<%=list.get(i).getBbsID()%>"><%=list.get(i).getBbsTitle().replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;").replaceAll("\n", "<br>")%></a>
						</td>
						<td><%=list.get(i).getUserID()%></td>
						<td><%=list.get(i).getBbsDate()%></td>
						<td><%=list.get(i).getBbsViewcount()%></td>
					</tr>
					<%
						}
					%>

				</tbody>
			</table>
			<%
				if (pageNumber != 1) {
			%>
			<a href="bbs.jsp?pageNumber=<%=pageNumber - 1%>&opt=<%=opt%>&condition=<%=condition%>"
				class="btn btn-success btn-arraw-left">이전</a>
			<%
				}
				if (bbsDAO.nextPage(pageNumber + 1)) {
			%>
			<a href="bbs.jsp?pageNumber=<%=pageNumber + 1%>&opt=<%=opt%>&condition=<%=condition%>"
				class="btn btn-success btn-arraw-left">다음</a>
			<%
				}
			%>
			<a href="write.jsp" class="btn btn-primary pull-right">글쓰기</a>
		</div>



	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>