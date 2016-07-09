package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import com.bfd.parse.util.ahackthonUtil;

public class ahackthonCodeceoListRe implements ReProcessor {
	private static final Log LOG = LogFactory.getLog(ahackthonCodeceoListRe.class);
	private Pattern nextPartten = Pattern.compile("page/([0-9]*)");
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		String url = unit.getUrl();
		String data = unit.getPageData();
		try {
			if(data.indexOf("下一页") > 0){
				Matcher nextMatch = nextPartten.matcher(url);
				if(nextMatch.find()){
					// 处理下一页链接
					Map<String, Object> nextpageTask = new HashMap<String, Object>();
					String curStr = nextMatch.group(1);
					int next = Integer.valueOf(curStr) + 1;
					String nextUrl = url.replace("page/" + curStr, "page/" + next);
					resultData.put("nextpage", nextUrl);
					nextpageTask.put("link", nextUrl);
					nextpageTask.put("rawlink", nextUrl);
					nextpageTask.put("linktype", "newslist");
					
					List<Map> tasks = (List<Map>) resultData.get("tasks");
					tasks.add(nextpageTask);
				}
			}
			//items里增加iid与url
			ahackthonUtil.getIid(resultData);
			// 后处理插件加上iid
			ParseUtils.getIid(unit, result);
			LOG.info("url:" + unit.getUrl() + "after:" + JsonUtils.toJSONString(resultData));
			return new ReProcessResult(SUCCESS, processdata);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			LOG.error("url:" + unit.getUrl() + " reprocess error!");
			return new ReProcessResult(FAILED, processdata); 
		}
	}
}
