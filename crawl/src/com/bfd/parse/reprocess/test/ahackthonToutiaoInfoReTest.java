package com.bfd.parse.reprocess.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bfd.parse.ParseTestForPlugin;
import com.bfd.parse.client.DownloadClient;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.reprocess.ahackthonToutiaoInfoRe;
import com.bfd.parse.util.JsonUtil;

public class ahackthonToutiaoInfoReTest {
	// 初始化taskdata数据结构，包含任务的各种配置
		private static Map<String, Object> initTaskMap(
				Map<String, Object> spiderData, String url, String iid, String cid,
				String type, int pagetypeid, int siteid, String pageType) {

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> taskData = new HashMap<String, Object>();

			taskData.put("url", url);
			taskData.put("type", type);
			taskData.put("iid", iid);
			taskData.put("purl", "");
			taskData.put("datatype", "html");
			taskData.put("ajaxdatatype", "1");
			taskData.put("projname", "hackthon");
			taskData.put("cate", "cate");
			taskData.put("parsetype", 0);
			taskData.put("cid", cid);
			taskData.put("pagetypeid", pagetypeid);
			taskData.put("siteid", siteid);
			taskData.put("pagetype", pageType);

			map.put("taskdata", taskData);
			map.put("spiderdata", spiderData);

			return map;
		}

		@SuppressWarnings("unchecked")
		public static void main(String[] args) {

			String url = "http://toutiao.com/i6280625127994425857/";
			
			DownloadClient crawler = new DownloadClient();
			String cid = "hackthon_toutiao";
			String pagetype = "newscontent";
			int siteid = 188;
			int pagetypeid = 177;

			// 以下为测试ajaxdata。
			String stringZip = crawler.getPage(url, "1", cid, "test", "", "",
					pagetype, siteid);
			System.err.println("stringZip:" + stringZip);
			Map<String, Object> resMap = null;
			try {
				resMap = (Map<String, Object>) JsonUtil.parseObject(stringZip);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.err.println("resMap:" + JsonUtil.toJSONString(resMap));

			Map<String, Object> spiderData = (Map<String, Object>) resMap
					.get("spiderdata");
			Map<String, Object> map = initTaskMap(spiderData, url, "", cid, "info",
					pagetypeid, siteid, pagetype);
			ParseUnit unit = ParseUnit.fromMap(map, new Date().getTime());

			// 测试后处理插件
			ReProcessor reprocessor = new ahackthonToutiaoInfoRe();
			new ParseTestForPlugin().parseTest(unit, null, null, reprocessor);
		}
}
