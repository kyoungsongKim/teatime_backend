package castis.domain.board;

import castis.domain.model.PointHistory;
import castis.domain.point.PointHistoryDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class BoardController {

    private BoardSvc boardSvc;
    private BoardGroupSvc boardGroupSvc;
    private PointHistoryDao pointHistoryDao;

    private final BoardRepository boardRepository;
    private final BoardreplyRepository boardreplyRepository;
    /**
     * 리스트.
     */
    @GetMapping("/boardList")
    public BoardListDto boardList(@RequestParam(value="board", defaultValue="1")long board, @RequestParam(value="page" , defaultValue = "1") int page, @RequestParam(value="page_size", defaultValue = "10") int size){
        long count = boardRepository.countBoardByBoardGroupAndBrddeleteflag(board, 'N');
        List<Board> boards = boardRepository.findByBoardGroupAndBrddeleteflagOrderByBoardNumDesc(board, 'N', PageRequest.of(page - 1, size)).orElse(null); //,
        BoardListDto boardDto = new BoardListDto(count/size);
        boardDto.setBoardDataList(boards);
        return boardDto;
    }

    @GetMapping("/boardData")
    public BoardDataDto boardData(@RequestParam(value="boardNum", defaultValue = "1") long bdNum, @RequestParam(value="hit", defaultValue = "false") boolean hit){
        Board boardData = boardRepository.findByBoardNumAndBrddeleteflag(bdNum, 'N').orElse(null);
        if(hit) {
            boardData.hitUp();
            boardRepository.save(boardData);
        }
        BoardDataDto boardDataDto = new BoardDataDto(boardData);
        return boardDataDto;
    }

    @GetMapping("/boardReplies")
    public BoardReplyDto boardReplies(@RequestParam(value="boardNum", defaultValue = "1")int bdNum){
        List<Boardreply> replies = boardreplyRepository.findByBrdnoAndRedeleteflagOrderByReorder(bdNum, 'N').orElse(null);
        BoardReplyDto boardReplyDto = new BoardReplyDto(replies);
        return boardReplyDto;
    }

    @PostMapping("/writeDoc")
    public String writeDocument(@RequestBody WriteDataDto data){
        Board boardData = boardRepository.findByBoardNum(data.getBoardNum())
                .map(board -> board.updateData(data))
                .orElse(new Board(data));

        Board result = boardRepository.save(boardData);
        return result.getBoardNum().toString();
    }

    @DeleteMapping("/deleteDoc/{boardNum}")
    public ResponseEntity deleteDocument(@PathVariable long boardNum ) {
        Board boardData = boardRepository.findByBoardNum(boardNum)
                .map(Board::deleteData)
                .orElse(null);

        if(boardData == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else {
            boardRepository.save(boardData);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @PostMapping("/writeReply")
    public ResponseEntity writeReply(@RequestBody WriteReplyDto reply) {
        Boardreply boardreply = new Boardreply(reply);
        Boardreply parentReply = null;
        int order = 0;
        if(reply.getParentId() > 0) {
            parentReply = boardreplyRepository.findByBrdnoAndIdOrderByReorderDesc(reply.getBoardNum(), reply.getParentId()).orElse(null);
            if(parentReply != null) {
                boardreply.setRedepth(parentReply.getRedepth()+1);
                boardreply.setReparent(parentReply.getId());

                Boardreply nextReply =  boardreplyRepository.findTop1ByBrdnoAndRedepthAndReorderGreaterThanOrderByReorder(reply.getBoardNum(), parentReply.getRedepth(), parentReply.getReorder()).orElse(null);

                if(nextReply != null) {
                    order = nextReply.getReorder();
                }
            }
        }else {
            parentReply = boardreplyRepository.findTop1ByBrdnoOrderByReorderDesc(reply.getBoardNum()).orElse(null);
        }
        List<Boardreply> nextReply = new ArrayList<>();
        if(parentReply == null) {
            order = 1;
        }else {
            if(order == 0){
                order = parentReply.getReorder()+1;
            }
            nextReply = boardreplyRepository.findByBrdnoAndReorderGreaterThanEqual(reply.getBoardNum(), order).orElse(new ArrayList<>());
        }

        boardreply.setReorder(order);

        boardreplyRepository.save(boardreply);
        //전체 증가해주는 거 하나 필요.
        //저장하는거 ㅏ나필요.
        if(nextReply != null ) {
            for (Boardreply value : nextReply) {
                value.setReorder(value.getReorder() + 1);
            }
            boardreplyRepository.saveAll(nextReply);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/deleteReply/{replyId}")
    public ResponseEntity deleteReply(@PathVariable int replyId ) {
        Boardreply deleteReplyData = boardreplyRepository.findById(replyId)
                .map(Boardreply::deleteData)
                .orElse(null);

        if(deleteReplyData == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else {
            boardreplyRepository.save(deleteReplyData);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }


    public String boardList1(SearchVO searchVO, ModelMap modelMap) {
        if (searchVO.getBgno() == null) {
            searchVO.setBgno("1");
        }

        BoardGroupVO bgInfo = boardGroupSvc.selectBoardGroupOne4Used(searchVO.getBgno());
        if (bgInfo == null) {
            return "castis/domain/BoardGroupFail";
        }
        searchVO.pageCalculate(boardSvc.selectBoardCount(searchVO)); // startRow,
        // endRow

        List<BoardVO> listview = boardSvc.selectBoardList(searchVO);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedToday = df.format(new Date());
        int substringCount = "YYYY-mm-ddT".length();
        for (BoardVO bvd : listview) {
            if (bvd.getBrddate().contains(formattedToday) == true) {
                // this article was written today (YYYY-mm-ddT 11char)
                bvd.setBrddate(bvd.getBrddate().substring(substringCount));
            } else {
                // this article was written yesterday or ago
                bvd.setBrddate(bvd.getBrddate().substring(0, substringCount - 1));
            }
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername(); // get logged in username
        List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
        int totalPoint = 0;
        for (PointHistory pHistory : pointHistoryList) {
            if (pHistory.getUsedate() != null) {
                //add only used code
                totalPoint += pHistory.getPoint();
            }
        }
        int userLevel = (int) Math.sqrt((int) (totalPoint / 1000));
        String totalPointStr = String.format("%,d", totalPoint);
        modelMap.addAttribute("userpoint", totalPointStr);
        modelMap.addAttribute("username", name);
        modelMap.addAttribute("userlevel", userLevel);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("bgInfo", bgInfo);

        return "castis/domain/BoardList";
    }

    /**
     * 글 쓰기.
     */
    @RequestMapping(value = "/boardForm")
    public String boardForm(HttpServletRequest request, ModelMap modelMap) {
        String bgno = request.getParameter("bgno");
        String brdno = request.getParameter("brdno");

        if (brdno != null) {
            BoardVO boardInfo = boardSvc.selectBoardOne(brdno);
            List<?> listview = boardSvc.selectBoardFileList(brdno);

            modelMap.addAttribute("boardInfo", boardInfo);
            modelMap.addAttribute("listview", listview);
            bgno = boardInfo.getBgno();
        }
        BoardGroupVO bgInfo = boardGroupSvc.selectBoardGroupOne4Used(bgno);
        if (bgInfo == null) {
            return "castis/domain/BoardGroupFail";
        }

        modelMap.addAttribute("bgno", bgno);
        modelMap.addAttribute("bgInfo", bgInfo);

        return "castis/domain/BoardForm";
    }

    /**
     * 글 저장.
     */
    @RequestMapping(value = "/boardSave")
    public String boardSave(HttpServletRequest request, BoardVO boardInfo) {
        String[] fileno = request.getParameterValues("fileno");

        FileUtil fs = new FileUtil();
        List<FileVO> filelist = fs.saveAllFiles(boardInfo.getUploadfile());

        boardSvc.insertBoard(boardInfo, filelist, fileno);

        if (boardInfo.getBgno().equalsIgnoreCase("2")) {
            return "redirect:/hiring";
        }
        return "redirect:/boardList?bgno=" + boardInfo.getBgno();
    }

    /**
     * 글 읽기.
     */
    @RequestMapping(value = "/boardRead")
    public String BoardRead(HttpServletRequest request, ModelMap modelMap) {
        String brdno = request.getParameter("brdno");

        boardSvc.updateBoardRead(brdno);
        BoardVO boardInfo = boardSvc.selectBoardOne(brdno);
        List<?> listview = boardSvc.selectBoardFileList(brdno);
        List<?> replylist = boardSvc.selectBoardReplyList(brdno);

        BoardGroupVO bgInfo = boardGroupSvc.selectBoardGroupOne4Used(boardInfo.getBgno());
        if (bgInfo == null) {
            return "castis/domain/BoardGroupFail";
        }

        String[] memoArray = boardInfo.getBrdmemo().split("\r\n");
        String youtubeRegexPattern = "^(?:http(?:s?):\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
        String httpRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String newMemo = "";
        List<String> validYoutubeVideoIdList = new ArrayList<String>();
        for (String token : memoArray) {
            boolean isFindYoutubeURL = false;
            Pattern youtubeRegexCompiled = Pattern.compile(youtubeRegexPattern, Pattern.CASE_INSENSITIVE);
            Matcher youtubeRegexMatcher = youtubeRegexCompiled.matcher(token);
            if (youtubeRegexMatcher.find()) {
                try {
                    String validYoutubeVideoId = youtubeRegexMatcher.group(1);
                    validYoutubeVideoIdList.add(validYoutubeVideoId);
                } catch (Exception ex) {
                    log.error("some exception:", ex);
                }
                isFindYoutubeURL = true;
            }
            if (isFindYoutubeURL == false) {
                Pattern httpRegexCompiled = Pattern.compile(httpRegex, Pattern.CASE_INSENSITIVE);
                Matcher httpRegexMatcher = httpRegexCompiled.matcher(token);
                if (httpRegexMatcher.find()) {
                    try {
                        token = "<a href='" + token + "' target='_blank'>" + token + "</a>";
                    } catch (Exception ex) {
                        log.error("some exception:", ex);
                    }
                }
                newMemo += (token + "<br>");
            }
        }
        boardInfo.setBrdmemo(newMemo);
        if (validYoutubeVideoIdList.size() > 0) {
            modelMap.addAttribute("youtubeDataIdList", validYoutubeVideoIdList);
        }
        if (listview != null && listview.size() > 0) {
            FileUtil fs = new FileUtil();
            FileVO originalFile = (FileVO) listview.get(0);
            fs.copyFile(originalFile.getRealname());
        }
        modelMap.addAttribute("boardInfo", boardInfo);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("replylist", replylist);
        modelMap.addAttribute("bgInfo", bgInfo);

        if (boardInfo.getBgno().equalsIgnoreCase("2")) {
            return "castis/domain/HiringRead";
        }
        return "castis/domain/BoardRead";
    }

    /**
     * 글 삭제.
     */
    @RequestMapping(value = "/boardDelete")
    public String boardDelete(HttpServletRequest request) {
        String brdno = request.getParameter("brdno");
        String bgno = request.getParameter("bgno");

        boardSvc.deleteBoardOne(brdno);

        return "redirect:/boardList?bgno=" + bgno;
    }

    /* ===================================================================== */

    /**
     * 댓글 저장.
     */
    @RequestMapping(value = "/boardReplySave")
    public String BoardReplySave(HttpServletRequest request, BoardReplyVO boardReplyInfo, ModelMap modelMap) {

        boardSvc.insertBoardReply(boardReplyInfo);

        modelMap.addAttribute("replyInfo", boardReplyInfo);

        return "castis/domain/BoardReadAjax4Reply";
    }

    /**
     * 댓글 삭제.
     */
    @RequestMapping(value = "/boardReplyDelete")
    public void BoardReplyDelete(HttpServletResponse response, BoardReplyVO boardReplyInfo) {

        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");

        try {
            if (!boardSvc.deleteBoardReply(boardReplyInfo.getReno())) {
                response.getWriter().print(mapper.writeValueAsString("Fail"));
            } else {
                response.getWriter().print(mapper.writeValueAsString("OK"));
            }
        } catch (IOException ex) {
            System.out.println("오류: 댓글 삭제에 문제가 발생했습니다.");
        }
    }
}
