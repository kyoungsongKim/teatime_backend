package castis.domain.board.controller;

import castis.domain.board.dto.*;
import castis.domain.board.entity.Boardfile;
import castis.domain.board.repository.BoardfileRepository;
import castis.domain.board.*;
import castis.domain.board.entity.Board;
import castis.domain.board.entity.Boardreply;
import castis.domain.board.repository.BoardRepository;
import castis.domain.board.repository.BoardreplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class BoardController {
    private final BoardRepository boardRepository;
    private final BoardreplyRepository boardreplyRepository;
    private final BoardfileRepository boardfileRepository;
    private final FileUtil fs;

    /**
     * 리스트.
     */
    @GetMapping("/boardList")
    @Transactional
    public BoardListDto boardList(@RequestParam(value="board", defaultValue="1")long board,
                                  @RequestParam(value="page" , defaultValue = "1") int page,
                                  @RequestParam(value="page_size", defaultValue = "10") int size,
                                  @RequestParam(value="agreementUserId", defaultValue="") String agreementUserId,
                                  @RequestParam(value="searchKeyword", defaultValue="") String searchKeyword){
        long count;
        List<Board> boards;
        if ( searchKeyword.isEmpty() == false ) {
            boards = boardRepository.findByBoardGroupAndBrddeleteflagOrderByBoardNumDesc(board, 'N').orElse(null);
            Iterator<Board> iter = boards.iterator();
            while (iter.hasNext()) {
                Board curBoard = iter.next();
                String title = curBoard.getBrdtitle()==null?"":curBoard.getBrdtitle();
                String agreeId = curBoard.getAgreementUserId()==null?"":curBoard.getAgreementUserId();
                if ( title.contains(searchKeyword) || agreeId.contains(searchKeyword)) {
                    continue;
                }
                iter.remove();
            }
            count=boards.size();
        } else if (agreementUserId.isEmpty()) {
            count = boardRepository.countBoardByBoardGroupAndBrddeleteflag(board, 'N');
            boards = boardRepository.findByBoardGroupAndBrddeleteflagOrderByBoardNumDesc(board, 'N', PageRequest.of(page - 1, size)).orElse(null); //,
        } else {  // user
            count = boardRepository.countBoardByBoardGroupAndAgreementUserIdAndBrddeleteflag(board, agreementUserId, 'N');
            boards = boardRepository.findByBoardGroupAndAgreementUserIdAndBrddeleteflagOrderByBoardNumDesc(board, agreementUserId, 'N', PageRequest.of(page - 1, size)).orElse(null); //,
        }
        BoardListDto boardDto = new BoardListDto(count , size);
        boardDto.setBoardDataList(boards);
        return boardDto;
    }

    @GetMapping("/boardData")
    @Transactional
    public BoardDataDto boardData(@RequestParam(value="boardNum", defaultValue = "1") long bdNum, @RequestParam(value="hit", defaultValue = "false") boolean hit){
        Board boardData = boardRepository.findByBoardNumAndBrddeleteflag(bdNum, 'N').orElse(null);
        if (hit) {
            boardData.hitUp();
            boardRepository.save(boardData);
        }
        BoardDataDto boardDataDto = new BoardDataDto(boardData);
        String[] memoArray = boardDataDto.getSummary().split("\r\n");
        String youtubeRegexPattern = "^(?:http(?:s?):\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
        String httpRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        for (String line : memoArray) {
            Pattern youtubeRegexCompiled = Pattern.compile(youtubeRegexPattern, Pattern.CASE_INSENSITIVE);
            Matcher youtubeRegexMatcher = youtubeRegexCompiled.matcher(line);
            String type = "";
            String summary_line = "";
            if(youtubeRegexMatcher.find()) {
                try{
                    String validYoutubeVideoID = youtubeRegexMatcher.group(1);
                    summary_line = validYoutubeVideoID;
                    type = "youtube";
                }catch (Exception ex){
                    log.error("some exception : {}" , ex);
                }
            } else {
                Pattern httpRegexCompiled = Pattern.compile(httpRegex, Pattern.CASE_INSENSITIVE);
                Matcher httpRegexMatcher = httpRegexCompiled.matcher(line);
                if(httpRegexMatcher.find()) {
                    try {
                        summary_line = "<a href='" + line + "' target='_blank'>" + line + "</a>";
                        type = "html";
                    } catch (Exception ex) {
                        log.error("some exception : {}" , ex);
                    }
                }else {
                    type = "default";
                    summary_line = line;
                }
            }
            boardDataDto.addSummary(type, summary_line);
        }
        boardfileRepository.findByBrdno(Long.valueOf(bdNum).intValue()).ifPresent(boardDataDto::setBoardfiles);

        return boardDataDto;
    }

    @GetMapping("/boardReplies")
    public BoardReplyDto boardReplies(@RequestParam(value="boardNum", defaultValue = "1")int bdNum){
        Board board = boardRepository.findByBoardNum(bdNum).orElse(null);
        List<Boardreply> replies = null;
        if(board!=null) {
            replies = boardreplyRepository.findByBrdnoAndRedeleteflagOrderByReorder(board, 'N').orElse(null);
        }
        BoardReplyDto boardReplyDto = new BoardReplyDto(replies);
        return boardReplyDto;
    }

    @PostMapping("/writeDoc")
    public String writeDocument(@RequestBody WriteDataDto data) {
        Board boardData = boardRepository.findByBoardNum(data.getBoardNum())
                .map(board -> board.updateData(data))
                .orElse(new Board(data));

        Board result = boardRepository.save(boardData);
        return result.getBoardNum().toString();
    }

    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("uploadFile") MultipartFile file, @RequestParam("boardNum") int num) throws IOException {
        Boardfile boardfile = new Boardfile(num, file);
        fs.saveFile(file, boardfile.getRealname());
        boardfileRepository.save(boardfile);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity downloadFile(@RequestParam("fileName") String file, @RequestParam("saveName") String save) throws IOException {
        Path path = Paths.get(fs.getUploadFolder() + save);
        String contentType = Files.probeContentType(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(file, StandardCharsets.UTF_8).build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @DeleteMapping("/deleteDoc/{boardNum}")
    public ResponseEntity deleteDocument(@PathVariable long boardNum) {
        Board boardData = boardRepository.findByBoardNum(boardNum)
                .map(Board::deleteData)
                .orElse(null);

        List<Boardreply> boardreplies = boardreplyRepository.findByBrdno(boardData).orElse(null);

        if(boardreplies != null) {
            for (Boardreply reply : boardreplies){
                reply.deleteData();
            }
            boardreplyRepository.saveAll(boardreplies);
        }

        if(boardData == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else {
            boardRepository.save(boardData);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @PostMapping("/writeReply")
    public ResponseEntity writeReply(@RequestBody WriteReplyDto reply) {
        Board board = boardRepository.findByBoardNum(reply.getBoardNum()).orElse(null);
        if(board != null) {
            Boardreply boardreply = new Boardreply(reply, board);
            Boardreply parentReply = null;
            int order = 0;
            if (reply.getParentId() > 0) {
                parentReply = boardreplyRepository.findByBrdnoAndIdOrderByReorderDesc(board, reply.getParentId()).orElse(null);
                if (parentReply != null) {
                    boardreply.setRedepth(parentReply.getRedepth() + 1);
                    boardreply.setReparent(parentReply.getId());

                    Boardreply nextReply = boardreplyRepository.findTop1ByBrdnoAndRedepthAndReorderGreaterThanOrderByReorder(board, parentReply.getRedepth(), parentReply.getReorder()).orElse(null);

                    if (nextReply != null) {
                        order = nextReply.getReorder();
                    }
                }
            } else {
                parentReply = boardreplyRepository.findTop1ByBrdnoOrderByReorderDesc(board).orElse(null);
            }
            List<Boardreply> nextReply = new ArrayList<>();
            if (parentReply == null) {
                order = 1;
            } else {
                if (order == 0) {
                    order = parentReply.getReorder() + 1;
                }
                nextReply = boardreplyRepository.findByBrdnoAndReorderGreaterThanEqual(board, order).orElse(new ArrayList<>());
            }

            boardreply.setReorder(order);

            boardreplyRepository.save(boardreply);

            if (nextReply != null) {
                for (Boardreply value : nextReply) {
                    value.setReorder(value.getReorder() + 1);
                }
                boardreplyRepository.saveAll(nextReply);
            }
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
}
