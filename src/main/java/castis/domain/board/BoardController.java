package castis.domain.board;

import castis.domain.point.PointHistoryDao;
import castis.domain.model.PointHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Autowired
	private BoardSvc boardSvc;
	@Autowired
	private BoardGroupSvc boardGroupSvc;
	@Autowired
	ApplicationContext context;

	/**
	 * 리스트.
	 */
	@RequestMapping(value = "/boardList")
	public String boardList(SearchVO searchVO, ModelMap modelMap) {
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
		PointHistoryDao pointHistoryDao = context.getBean("pointHistoryDao", PointHistoryDao.class);
		List<PointHistory> pointHistoryList = pointHistoryDao.getPointHistoryByUser(name);
		int totalPoint = 0;
		for (PointHistory pHistory : pointHistoryList) {
			if (pHistory.getUsedate() != null ) {
				//add only used code
				totalPoint += pHistory.getPoint();
			}
		}
		int userLevel = (int) Math.sqrt((int)(totalPoint/1000));
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

		if ( boardInfo.getBgno().equalsIgnoreCase("2") ) {
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
					logger.error("some exception:", ex);
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
						logger.error("some exception:", ex);
					}
				}
				newMemo += (token + "<br>");
			}
		}
		boardInfo.setBrdmemo(newMemo);
		if ( validYoutubeVideoIdList.size() > 0 ) {
			modelMap.addAttribute("youtubeDataIdList", validYoutubeVideoIdList);
		}
		if ( listview != null && listview.size() > 0 ) {
			FileUtil fs = new FileUtil();
			FileVO originalFile = (FileVO)listview.get(0);
			fs.copyFile(originalFile.getRealname());
		}
		modelMap.addAttribute("boardInfo", boardInfo);
		modelMap.addAttribute("listview", listview);
		modelMap.addAttribute("replylist", replylist);
		modelMap.addAttribute("bgInfo", bgInfo);

		if ( boardInfo.getBgno().equalsIgnoreCase("2")) {
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
