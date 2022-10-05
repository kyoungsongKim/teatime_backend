package castis.domain.board;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardGroupSvc {

private SqlSessionTemplate sqlSession;

    public BoardGroupSvc(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<?> selectBoardGroupList() {
        return sqlSession.selectList("selectBoardGroupList");
    }
    
    /**
     *  게시판 그룹 수정 및 신규 저장.
     */
    public void insertBoard(BoardGroupVO param) {
        if ("".equals(param.getBgparent())) {
            param.setBgparent(null);
        }
        
        if (param.getBgno() == null || "".equals(param.getBgno())) {
            sqlSession.insert("insertBoardGroup", param);
        } else {
            sqlSession.insert("updateBoardGroup", param);
        }
    }
 
    public BoardGroupVO selectBoardGroupOne(String param) {
        return sqlSession.selectOne("selectBoardGroupOne", param);
    }

    public BoardGroupVO selectBoardGroupOne4Used(String param) {
        return sqlSession.selectOne("selectBoardGroupOne4Used", param);
    }
    
    public void deleteBoardGroup(String param) {
        sqlSession.delete("deleteBoardGroup", param);
    }
    
}
