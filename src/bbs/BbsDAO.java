package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {

	private Connection conn;
	private ResultSet rs;

	final int PAGING = 5;

	public BbsDAO() {
		try {
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "JSPBOARD";
			String password = "1234";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 현재 날짜
	public String getDate() {
		String SQL = "SELECT SYSDATE FROM DUAL";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ""; // 데이터베이스 오류
	}

	// 다음 게시물 번호
	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
//		String SQL = "SELECT COUNT(bbsID) FROM BBS";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
//			System.out.println(rs.getInt("count(bbsID)"));
				return rs.getInt("bbsID") + 1;
			}
			return 1; // 첫 번째 게시물인 경우
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

	// 게시물 갯수
	public int getCount() {
//		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
		String SQL = "SELECT COUNT(bbsID) FROM BBS";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
//			System.out.println(rs.getInt("count(bbsID)"));
				return rs.getInt("COUNT(bbsID)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

	// 게시물 작성
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "INSERT INTO BBS VALUES(bbs_seq.nextval, ?, ?, ?, sysdate, 0, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, userID);
			pstmt.setString(3, bbsContent);
			pstmt.setInt(4, 1);

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

	// 게시물 목록
	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM (SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC) WHERE ROWNUM <= "
				+ PAGING;
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * PAGING);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt("bbsID"));
				bbs.setBbsTitle(rs.getString("bbsTitle"));
				bbs.setUserID(rs.getString("userID"));
				bbs.setBbsContent(rs.getString("bbsContent"));
				bbs.setBbsDate(rs.getString("bbsDate"));
				bbs.setBbsViewcount(rs.getInt("bbsViewCount"));
				bbs.setBbsAvailble(rs.getInt("bbsAvailable"));
				list.add(bbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 다음 페이지 이동
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getCount() - (pageNumber - 1) * PAGING);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

//	// 게시물 상세내용
//	public Bbs getBbs(int bbsID) {
//		String SQL = "SELECT * FROM BBS WHERE bbsID = ?";
//		String updataSQL = "UPDATE BBS set bbsViewCount = bbsViewCount + 1 WHERE bbsID = ?";
//		try {
//			PreparedStatement pstmt2 = conn.prepareStatement(updataSQL);
//			pstmt2.setInt(1, bbsID);
//			pstmt2.executeUpdate();
//			// 조회수 1 증가 : update
//
//			PreparedStatement pstmt = conn.prepareStatement(SQL);
//			pstmt.setInt(1, bbsID);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				Bbs bbs = new Bbs();
//				bbs.setBbsID(rs.getInt(1));
//				bbs.setBbsTitle(rs.getString(2));
//				bbs.setUserID(rs.getString(3));
//				bbs.setBbsContent(rs.getString(4));
//				bbs.setBbsDate(rs.getString(5));
//				bbs.setBbsViewcount(rs.getInt(6));
//				bbs.setBbsAvailble(rs.getInt(7));
//				return bbs;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	// 게시물 상세내용
	public Bbs getBbs(int bbsID) {
		String updataSQL = "UPDATE BBS set bbsViewCount = bbsViewCount + 1 WHERE bbsID = ?";
		String SQL = "SELECT * FROM BBS WHERE bbsID = ?";
		try {
			PreparedStatement pstmt2 = conn.prepareStatement(updataSQL);
			pstmt2.setInt(1, bbsID);
			pstmt2.executeUpdate();
			// 조회수 1 증가 : update

			PreparedStatement pstmt = conn.prepareStatement(SQL);
//			pstmt.setString(1, "bbsID");
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsContent(rs.getString(4));
				bbs.setBbsDate(rs.getString(5));
				bbs.setBbsViewcount(rs.getInt(6));
				bbs.setBbsAvailble(rs.getInt(7));
				return bbs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 게시물 수정
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

	// 게시물 삭제 (bbsAvailable 를 0으로 변경 데이터에서는 삭제x)
	public int delete(int bbsID) {
		String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 데이터베이스 오류
	}

}
