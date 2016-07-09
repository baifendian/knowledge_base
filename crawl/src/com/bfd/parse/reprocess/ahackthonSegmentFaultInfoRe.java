package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.DateUtil;

public class ahackthonSegmentFaultInfoRe  implements ReProcessor{
	private static final Log LOG = LogFactory.getLog(ahackthonSegmentFaultInfoRe.class);

	//<div class="col-md-9 col-sm-8 col-xs-12">[\s\S]*?</div>
	//<div class="col-xs-12 col-md-9 main ">[\s\S]*?</div>
	private Pattern postTimePartten = Pattern.compile("\\s(.*?)发布");
	//标题、文章内容过滤
	private Pattern titleHtmlPartten = Pattern.compile("<div class=\"col-md-9 col-sm-8 col-xs-12\">[\\s\\S]*?</div>");
	private Pattern contentHtmlPartten = Pattern.compile("<div class=\"col-xs-12 col-md-9 main \">[\\s\\S]*?</div>");
	private String HTML_INDEX = "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, "
			+ "initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\"><title>";
	@SuppressWarnings("unchecked")
	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		Map<String, Object> taskData = result.getTaskdata();
		//从列表页入口获取topic
		try {
			if(taskData.containsKey("attr")){
				Map<String, Object> attrMap = (Map<String, Object>) taskData.get("attr");
				String topic = (String) attrMap.get("keyword");
				resultData.put("topic", topic);
			}
			//获取源码
			String data = unit.getPageData();
//		System.out.println("length:" + data.length());
			Matcher titleHtmlMatch = titleHtmlPartten.matcher(data);
			Matcher contentHtmlMatch = contentHtmlPartten.matcher(data);
			if(titleHtmlMatch.find() && contentHtmlMatch.find()){
				String htmlData = HTML_INDEX + resultData.get("title") + "</title></head><body>"
						+ titleHtmlMatch.group(0) + contentHtmlMatch.group(0) + "</body></html>";
				resultData.put("html", htmlData);
			}
			
			if(resultData.containsKey("post_time")){
				String pTime = (String) resultData.get("post_time");
				Matcher pTimeMatch = postTimePartten.matcher(pTime);
				if(pTimeMatch.find()){
					String postTime = pTimeMatch.group(1);
					if(postTime.indexOf("前") > 0){
						postTime = DateUtil.convertTime(postTime);
					}
					else if(postTime.indexOf("月") > 0 && postTime.indexOf("日") > 0 && postTime.indexOf("年") < 0){
						postTime = "2016-" + postTime.replaceAll("年|月", "-").replace("日", "");
						
					}
					else if(postTime.indexOf("月") > 0 && postTime.indexOf("日") > 0 && postTime.indexOf("年") > 0){
						postTime = postTime.replaceAll("年|月", "-").replace("日", "");
					}
					long postTimeLong = DateUtil.getTimeMillis(postTime + " 00:00:00");
					resultData.put("post_time", postTimeLong);
				}
			}
			LOG.info("url:" + unit.getUrl() + "after:" + JsonUtils.toJSONString(resultData));
			return new ReProcessResult(SUCCESS, processdata);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("url:" + unit.getUrl() + " reprocess error!");
			return new ReProcessResult(FAILED, processdata); 
		}
	}
	
}
